package com.mieyde.tx.discovery.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.mieyde.tx.common.ConfigurationKeys;
import com.mieyde.tx.common.util.CollectionUtils;
import com.mieyde.tx.common.util.ObjectUtils;
import com.mieyde.tx.common.util.StringUtls;
import com.mieyde.tx.config.Configuration;
import com.mieyde.tx.config.ConfigurationFactory;
import com.mieyde.tx.discovery.registry.RegistryService;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 我吃稀饭面
 * @date 2023/7/11 11:45
 */
public class NacosRegistryServiceImpl implements RegistryService {

    private static final Configuration FILE_CONFIG = ConfigurationFactory.CURRENT_FILE_INSTANCE;

    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    private static final String DEFAULT_APPLICATION = "seata-server";
    private static final String DEFAULT_CLUSTER = "default";
    private static final String REGISTRY_TYPE = "nacos";
    private static final String PRO_GROUP_KEY = "group";
    private static final String REGISTRY_CLUSTER = "cluster";
    private static final String SLB_PATTERN = "slbPattern";
    private static final String PRO_APPLICATION_KEY = "application";
    private static final String PRO_SERVER_ADDR_KEY = "serverAddr";
    private static final String PRO_NAMESPACE_KEY = "namespace";
    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";
    private static final String ACCESS_KEY = "accessKey";
    private static final String SECRET_KEY = "secretKey";
    private static final Pattern DEFAULT_SLB_REGISTRY_PATTERN = Pattern.compile("(?!.*internal)(?=.*seata).*mse.aliyuncs.com");
    private static volatile Boolean useSLBWay;
    private static volatile NacosRegistryServiceImpl instance;
    private static volatile NamingService naming;
    private static volatile NamingMaintainService namingMaintain;

    private static final ConcurrentMap<String, List<InetSocketAddress>> CLUSTER_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, List<EventListener>> LISTENER_SERVICE_MAP = new ConcurrentHashMap<>();
    private static final String PUBLIC_NAMING_ADDRESS_PREFIX = "public_";
    private static final String PUBLIC_NAMING_SERVICE_META_IP_KEY = "publicIp";
    private static final String PUBLIC_NAMING_SERVICE_META_PORT_KEY = "publicPort";
    private static final Object LOCK_OBJ = new Object();

    private NacosRegistryServiceImpl() {
        String configForNacosSLB = FILE_CONFIG.getConfig(getNacosUrlPatternOfSLB());
        Pattern patternOfNacosRegistryForSLB = StringUtls.isBlank(configForNacosSLB) ? DEFAULT_SLB_REGISTRY_PATTERN : Pattern.compile(configForNacosSLB);

        this.useSLBWay = patternOfNacosRegistryForSLB.matcher(getNamingProperties().getProperty(PRO_SERVER_ADDR_KEY)).matches();
    }

    public static NacosRegistryServiceImpl getInstance(){
        if (ObjectUtils.isNull(instance)){
            synchronized (NacosRegistryServiceImpl.class){
                if (ObjectUtils.isNull(instance)){
                    instance = new NacosRegistryServiceImpl();
                }
            }
        }
        return instance;
    }

    public static NamingService getNamingInstance() throws Exception {
        if (ObjectUtils.isNull(naming)){
            synchronized (NacosRegistryServiceImpl.class){
                if (ObjectUtils.isNull(naming)){
                    naming = NacosFactory.createNamingService(getNamingProperties());
                }
            }
        }
        return naming;
    }

    public static NamingMaintainService getNamingMaintainInstance() throws Exception {
        if (namingMaintain == null) {
            synchronized (NacosRegistryServiceImpl.class) {
                if (namingMaintain == null) {
                    namingMaintain = NacosFactory.createMaintainService(getNamingProperties());
                }
            }
        }
        return namingMaintain;
    }

    @Override
    public void register(InetSocketAddress address) throws Exception {
        getNamingInstance().registerInstance(getServiceName(),getServiceGroup(),address.getAddress().getHostAddress(), address.getPort(),getClusterName());
    }

    @Override
    public void unregister(InetSocketAddress address) throws Exception {
        getNamingInstance().deregisterInstance(getServiceName(),getServiceGroup(),address.getAddress().getHostAddress(),address.getPort(),getClusterName());
    }

    @Override
    public void subscribe(String cluster) throws Exception {
        getNamingInstance().subscribe(getServiceName(),getServiceGroup(),List.of(cluster),event-> System.out.println("subscribe -> " + cluster));
    }

    @Override
    public void unsubscribe(String cluster) throws Exception {
        getNamingInstance().unsubscribe(getServiceName(), getServiceGroup(), List.of(cluster), event-> System.out.println("unsubscribe -> " + cluster));
    }

    @Override
    public List<InetSocketAddress> lookup(String key) throws Exception {
        String clusterName = getServiceGroup(key);
        if (StringUtls.isBlank(clusterName)){
            return null;
        }
        if (useSLBWay){
            if (!CLUSTER_ADDRESS_MAP.containsKey(PUBLIC_NAMING_ADDRESS_PREFIX + clusterName)) {
                Service service = getNamingMaintainInstance().queryService(DEFAULT_APPLICATION, clusterName);
                String pubnetIp = service.getMetadata().get(PUBLIC_NAMING_SERVICE_META_IP_KEY);
                String pubnetPort = service.getMetadata().get(PUBLIC_NAMING_SERVICE_META_PORT_KEY);
                if (StringUtls.isBlank(pubnetIp) || StringUtls.isBlank(pubnetPort)) {
                    throw new Exception("cannot find service address from nacos naming mata-data");
                }
                InetSocketAddress publicAddress = new InetSocketAddress(pubnetIp, Integer.valueOf(pubnetPort));
                List<InetSocketAddress> publicAddressList = Arrays.asList(publicAddress);
                CLUSTER_ADDRESS_MAP.put(PUBLIC_NAMING_ADDRESS_PREFIX + clusterName,publicAddressList);
                return publicAddressList;
            }
        }
        if (!LISTENER_SERVICE_MAP.containsKey(clusterName)) {
            synchronized (LOCK_OBJ) {
                if (!LISTENER_SERVICE_MAP.containsKey(clusterName)) {
                    List<String> clusters = new ArrayList<>();
                    clusters.add(clusterName);
                    List<Instance> firstAllInstances = getNamingInstance().getAllInstances(getServiceName(), getServiceGroup(), clusters);
                    if (CollectionUtils.isNotEmpty(firstAllInstances)){
                        List<InetSocketAddress> newAddressList = firstAllInstances
                                .stream()
                                .filter(eachInstance -> eachInstance.isEnabled() && eachInstance.isHealthy())
                                .map(eachInstance -> new InetSocketAddress(eachInstance.getIp(), eachInstance.getPort()))
                                .collect(Collectors.toList());
                        CLUSTER_ADDRESS_MAP.put(clusterName,newAddressList);
                    }
                    subscribe(clusterName);
                }
            }
        }
        return CLUSTER_ADDRESS_MAP.get(clusterName);
    }

    @Override
    public void close() throws Exception {

    }

    private static Properties getNamingProperties(){
        Properties properties = new Properties();
        if (StringUtls.isNotBlank(System.getProperty(PRO_SERVER_ADDR_KEY))){
            properties.setProperty(PRO_SERVER_ADDR_KEY,System.getProperty(PRO_SERVER_ADDR_KEY));
        }else {
            String address = FILE_CONFIG.getConfig(getNacosAddrFileKey());
            if (StringUtls.isNotBlank(address)){
                properties.setProperty(PRO_SERVER_ADDR_KEY,address);
            }
        }

        if (StringUtls.isNotBlank(System.getProperty(PRO_NAMESPACE_KEY))){
            properties.setProperty(PRO_NAMESPACE_KEY,System.getProperty(PRO_NAMESPACE_KEY));
        }else {
            String namespace = FILE_CONFIG.getConfig(getNacosNameSpaceFileKey());
            if (StringUtls.isNotBlank(namespace)){
                properties.setProperty(PRO_NAMESPACE_KEY,namespace);
            }
        }

        String userName = StringUtls.isNotBlank(System.getProperty(USER_NAME)) ? System.getProperty(USER_NAME) : FILE_CONFIG.getConfig(getNacosUserName());
        if (StringUtls.isNotBlank(userName)){
            String password = StringUtls.isNotBlank(System.getProperty(PASSWORD)) ? System.getProperty(PASSWORD) : FILE_CONFIG.getConfig(getNacosPassword());
            if (StringUtls.isNotBlank(password)){
                properties.setProperty(USER_NAME, userName);
                properties.setProperty(PASSWORD, password);
            }
        }else {
            String accessKey = StringUtls.isNotBlank(System.getProperty(ACCESS_KEY)) ? System.getProperty(ACCESS_KEY) : FILE_CONFIG.getConfig(getNacosAccessKey());
            if (StringUtls.isNotBlank(accessKey)){
                String secretKey = StringUtls.isNotBlank(System.getProperty(SECRET_KEY)) ? System.getProperty(SECRET_KEY) : FILE_CONFIG.getConfig(getNacosSecretKey());
                if (StringUtls.isNotBlank(secretKey)){
                    properties.setProperty(ACCESS_KEY, accessKey);
                    properties.setProperty(SECRET_KEY, secretKey);
                }
            }
        }
        return properties;
    }

    private static String getNacosUrlPatternOfSLB() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_REGISTRY, REGISTRY_TYPE, SLB_PATTERN);
    }

    private static String getClusterName() {
        return FILE_CONFIG.getConfig(getNacosClusterFileKey(), DEFAULT_CLUSTER);
    }

    private static String getServiceName() {
        return FILE_CONFIG.getConfig(getNacosApplicationFileKey(), DEFAULT_APPLICATION);
    }

    private static String getServiceGroup() {
        return FILE_CONFIG.getConfig(getNacosApplicationGroupKey(), DEFAULT_GROUP);
    }

    private static String getNacosApplicationFileKey() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_REGISTRY, REGISTRY_TYPE, PRO_APPLICATION_KEY);
    }

    private static String getNacosApplicationGroupKey() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_REGISTRY, REGISTRY_TYPE, PRO_GROUP_KEY);
    }

    private static String getNacosClusterFileKey() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_REGISTRY, REGISTRY_TYPE, REGISTRY_CLUSTER);
    }

    public static String getNacosAddrFileKey() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_REGISTRY, REGISTRY_TYPE, PRO_SERVER_ADDR_KEY);
    }

    public static String getNacosNameSpaceFileKey() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_REGISTRY, REGISTRY_TYPE, PRO_NAMESPACE_KEY);
    }

    private static String getNacosUserName() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_REGISTRY, REGISTRY_TYPE, USER_NAME);
    }

    private static String getNacosPassword() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_REGISTRY, REGISTRY_TYPE, PASSWORD);
    }

    public static String getNacosAccessKey() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_REGISTRY, REGISTRY_TYPE, ACCESS_KEY);
    }

    public static String getNacosSecretKey() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_REGISTRY, REGISTRY_TYPE, SECRET_KEY);
    }
}
