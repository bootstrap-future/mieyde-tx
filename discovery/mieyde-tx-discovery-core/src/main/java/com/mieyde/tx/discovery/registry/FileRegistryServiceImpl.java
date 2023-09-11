package com.mieyde.tx.discovery.registry;

import com.mieyde.tx.common.util.ObjectUtils;
import com.mieyde.tx.common.util.StringUtils;
import com.mieyde.tx.config.Configuration;
import com.mieyde.tx.config.ConfigurationFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 我吃稀饭面
 * @date 2023/7/10 13:39
 */
public class FileRegistryServiceImpl implements RegistryService{

    private static final Configuration CONFIG = ConfigurationFactory.getInstance();
    private static volatile FileRegistryServiceImpl instance;
    private static final String POSTFIX_GROUPLIST = ".grouplist";
    private static final String ENDPOINT_SPLIT_CHAR = ";";
    private static final String IP_PORT_SPLIT_CHAR = ":";

    private FileRegistryServiceImpl() {
    }

    public static FileRegistryServiceImpl getInstance(){
        if (ObjectUtils.isNull(instance)){
            synchronized (FileRegistryServiceImpl.class){
                if (ObjectUtils.isNull(instance)){
                    instance = new FileRegistryServiceImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public void register(InetSocketAddress address) throws Exception {

    }

    @Override
    public void unregister(InetSocketAddress address) throws Exception {

    }

    @Override
    public void subscribe(String cluster) throws Exception {

    }

    @Override
    public void unsubscribe(String cluster) throws Exception {

    }

    @Override
    public List<InetSocketAddress> lookup(String key) throws Exception {
        String clusterName = getServiceGroup(key);
        if (StringUtils.isBlank(clusterName)){
            return null;
        }
        String endpointStr = CONFIG.getConfig(PREFIX_SERVICE_ROOT + CONFIG_SPLIT_CHAR + clusterName + POSTFIX_GROUPLIST);
        if (StringUtils.isBlank(endpointStr)) {
            throw new IllegalArgumentException(clusterName + POSTFIX_GROUPLIST + " is required");
        }
        String[] endpoints = endpointStr.split(ENDPOINT_SPLIT_CHAR);
        List<InetSocketAddress> inetSocketAddresses = new ArrayList<>();
        for (String endpoint : endpoints) {
            String[] ipAndPort = endpoint.split(IP_PORT_SPLIT_CHAR);
            if (ipAndPort.length != 2){
                throw new IllegalArgumentException("endpoint format should like ip:port");
            }
            inetSocketAddresses.add(new InetSocketAddress(ipAndPort[0],Integer.parseInt(ipAndPort[1])));
        }
        return inetSocketAddresses;
    }

    @Override
    public void close() throws Exception {

    }
}
