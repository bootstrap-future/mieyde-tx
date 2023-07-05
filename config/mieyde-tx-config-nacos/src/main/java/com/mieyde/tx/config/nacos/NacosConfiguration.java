package com.mieyde.tx.config.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractSharedListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.mieyde.tx.common.ConfigurationKeys;
import com.mieyde.tx.common.exception.NotSupportYetException;
import com.mieyde.tx.common.loader.EnhancedServiceLoader;
import com.mieyde.tx.common.util.ObjectUtils;
import com.mieyde.tx.common.util.StringUtls;
import com.mieyde.tx.config.AbstractConfiguration;
import com.mieyde.tx.config.Configuration;
import com.mieyde.tx.config.ConfigurationFactory;
import com.mieyde.tx.config.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author 我吃稀饭面
 * @date 2023/7/4 17:59
 */
public class NacosConfiguration extends AbstractConfiguration {

    private static final Logger log = LoggerFactory.getLogger(NacosConfiguration.class);

    protected static final long DEFAULT_CONFIG_TIMEOUT = 5 * 1000;
    private static final String CONFIG_TYPE = "nacos";
    private static final String PRO_SERVER_ADDR_KEY = "serverAddr";
    private static final String PRO_NAMESPACE_KEY = "namespace";
    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";
    private static final String ACCESS_KEY = "accessKey";
    private static final String SECRET_KEY = "secretKey";
    private static final String NACOS_DATA_ID_KEY = "dataId";
    private static final String DEFAULT_DATA_ID = "seata.yaml";
    private static final String GROUP_KEY = "group";
    private static final String DEFAULT_GROUP = "MIEYDE_GROUP";
    private static final Configuration FILE_CONFIG = ConfigurationFactory.CURRENT_FILE_INSTANCE;

    private static volatile ConfigService configService;
    private static volatile Properties mieydeConfig = new Properties();
    private static volatile NacosConfiguration instance;

    public static NacosConfiguration getInstance(){
        if (ObjectUtils.isNull(instance)){
            synchronized (NacosConfiguration.class){
                if (ObjectUtils.isNull(instance)){
                    instance = new NacosConfiguration();
                }
            }
        }
        return instance;
    }

    private NacosConfiguration() {
        if (ObjectUtils.isNull(configService)){
            try {
                configService = NacosFactory.createConfigService(getNacosConfigProperties());
                init();
            } catch (NacosException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Properties getNacosConfigProperties(){
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

    private void init(){
        try {
            String dataId = getDataId();
            String group = getGroup();
            String dataType = getDataType();
            String config = configService.getConfig(dataId,group, DEFAULT_CONFIG_TIMEOUT);
            if (StringUtls.isNotBlank(config)){
                mieydeConfig = EnhancedServiceLoader.load(Processor.class,dataType).processor(config);
                configService.addListener(dataId, group, new AbstractSharedListener() {
                    @Override
                    public void innerReceive(String dataId, String group, String configInfo) {
                        try {
                            mieydeConfig = EnhancedServiceLoader.load(Processor.class,dataType).processor(config);
                        } catch (IOException e) {
                            log.error("init config properties error", e);
                        }
                    }
                });
            }
        }catch (Exception e){
            log.error("init config properties error", e);
        }
    }

    public static String getNacosNameSpaceFileKey() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_CONFIG, CONFIG_TYPE, PRO_NAMESPACE_KEY);
    }

    public static String getNacosAddrFileKey() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_CONFIG, CONFIG_TYPE, PRO_SERVER_ADDR_KEY);
    }

    public static String getNacosUserName() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_CONFIG, CONFIG_TYPE, USER_NAME);
    }

    public static String getNacosPassword() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_CONFIG, CONFIG_TYPE, PASSWORD);
    }

    public static String getNacosAccessKey() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_CONFIG, CONFIG_TYPE, ACCESS_KEY);
    }

    public static String getNacosSecretKey() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_CONFIG, CONFIG_TYPE, SECRET_KEY);
    }

    public static String getNacosDataIdKey() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_CONFIG, CONFIG_TYPE, NACOS_DATA_ID_KEY);
    }

    public static String getNacosGroupKey() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_CONFIG, CONFIG_TYPE, GROUP_KEY);
    }

    public String getDataId(){
        return FILE_CONFIG.getConfig(getNacosDataIdKey(),DEFAULT_DATA_ID);
    }

    public String getGroup(){
        return FILE_CONFIG.getConfig(getNacosGroupKey(),DEFAULT_GROUP);
    }

    public String getDataType(){
        return FILE_CONFIG.getConfig(getDataTypeKey(),ConfigurationKeys.DEFAULT_DATA_TYPE);
    }

    public Properties getConfig() throws Exception{
        String config = configService.getConfig(getDataId(), getGroup(), DEFAULT_CONFIG_TIMEOUT);
        return EnhancedServiceLoader.load(Processor.class,getDataType()).processor(config);
    }


    @Override
    public String getTypeName() {
        return CONFIG_TYPE;
    }

    @Override
    public String getLatestConfig(String dataId, String defaultValue) {
        String value = mieydeConfig.getProperty(dataId);
        if (StringUtls.isBlank(value)){
            try {
                value = getConfig().getProperty(dataId);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return StringUtls.isBlank(value) ? defaultValue : value;
    }

    @Override
    public boolean putConfig(String dataId, String content) {
        try {
            if (mieydeConfig.isEmpty()) {
                configService.publishConfig(dataId,getGroup(),content);
            }else {
                mieydeConfig.setProperty(dataId,content);
                configService.publishConfig(getDataId(),getGroup(),propertiesToStr(mieydeConfig));
            }
            return true;
        }catch (NacosException e){
            log.error(e.getErrMsg());
            return false;
        }
    }

    @Override
    public boolean putConfigIfAbsent(String dataId, String content) {
        throw new NotSupportYetException("not support atomic operation putConfigIfAbsent");
    }

    @Override
    public boolean removeConfig(String dataId) {
        try {
            if (mieydeConfig.isEmpty()) {
                configService.removeConfig(dataId,getGroup());
            }else {
                mieydeConfig.remove(dataId);
                configService.publishConfig(getDataId(),getGroup(),propertiesToStr(mieydeConfig));
            }
            return true;
        }catch (NacosException e){
            log.error(e.getErrMsg());
            return false;
        }
    }

    private String propertiesToStr(Properties properties){
        StringBuilder sb = new StringBuilder();

        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String property = properties.getProperty(key);
            sb.append(key).append("=").append(property).append("\n");
        }

        return sb.toString();
    }
}
