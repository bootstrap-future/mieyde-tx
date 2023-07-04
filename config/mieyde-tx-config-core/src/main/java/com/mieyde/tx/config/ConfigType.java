package com.mieyde.tx.config;

/**
 * @author 我吃稀饭面
 * @date 2023/7/4 17:08
 */
public enum ConfigType {
    /**
     * File config type.
     */
    File,
    /**
     * zookeeper config type.
     */
    ZK,
    /**
     * Nacos config type.
     */
    Nacos,
    /**
     * Apollo config type.
     */
    Apollo,
    /**
     * Consul config type
     */
    Consul,
    /**
     * Etcd3 config type
     */
    Etcd3,
    /**
     * spring cloud config type
     */
    SpringCloudConfig,
    /**
     * Custom config type
     */
    Custom;
    public static ConfigType convertType(String name) {
        for (ConfigType configType : values()) {
            if (configType.name().equalsIgnoreCase(name)) {
                return configType;
            }
        }
        throw new IllegalArgumentException("not support config type: " + name);
    }

}
