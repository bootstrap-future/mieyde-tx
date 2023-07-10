package com.mieyde.tx.discovery.registry;

import com.mieyde.tx.config.ConfigurationFactory;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 我吃稀饭面
 * @date 2023/7/10 13:35
 */
public interface RegistryService {
    /**
     * The constant PREFIX_SERVICE_MAPPING.
     */
    String PREFIX_SERVICE_MAPPING = "vgroupMapping.";
    /**
     * The constant PREFIX_SERVICE_ROOT.
     */
    String PREFIX_SERVICE_ROOT = "service";
    /**
     * The constant CONFIG_SPLIT_CHAR.
     */
    String CONFIG_SPLIT_CHAR = ".";

    Set<String> SERVICE_GROUP_NAME = new HashSet<>();

    /**
     * Service node health check
     */
    Map<String,List<InetSocketAddress>> CURRENT_ADDRESS_MAP = new ConcurrentHashMap<>();
    void register(InetSocketAddress address) throws Exception;
    void unregister(InetSocketAddress address) throws Exception;
    void subscribe(String cluster) throws Exception;
    void unsubscribe(String cluster) throws Exception;
    List<InetSocketAddress> lookup(String key) throws Exception;
    void close() throws Exception;

    default String getServiceGroup(String key){
        key = PREFIX_SERVICE_ROOT + CONFIG_SPLIT_CHAR + PREFIX_SERVICE_MAPPING + key;
        if (!SERVICE_GROUP_NAME.contains(key)){
            SERVICE_GROUP_NAME.add(key);
        }
        return ConfigurationFactory.getInstance().getConfig(key);
    }

    default List<InetSocketAddress> aliveLookup(String transactionServiceGroup){
        return CURRENT_ADDRESS_MAP.computeIfAbsent(transactionServiceGroup,key -> new ArrayList<>());
    }

    default List<InetSocketAddress> refreshAliveLookup(String transactionServiceGroup,List<InetSocketAddress> aliveAddress){
        return CURRENT_ADDRESS_MAP.put(transactionServiceGroup,aliveAddress);
    }
}
