package com.mieyde.tx.config;

import com.mieyde.tx.common.util.StringUtls;

import java.util.Map;

/**
 * @author 我吃稀饭面
 * @date 2023/6/28 14:12
 */
public interface Configuration {

    Map<String,String> ENV_MAP = System.getenv();

    String getConfig(String dataId,String defaultValue);

    String getLatestConfig(String dataId,String defaultValue);

    boolean putConfig(String dataId, String content);

    boolean putConfigIfAbsent(String dataId, String content);

    boolean removeConfig(String dataId);

    /**
     * 获取系统变量值
     */
    default String getConfigBySystem(String dataId){
        if (StringUtls.isBlank(dataId)){
            return null;
        }
        String config = ENV_MAP.get(dataId);
        if (StringUtls.isNotBlank(config)){
            return config;
        }
        config = ENV_MAP.get(dataId.toUpperCase().replace(".","_"));
        if (StringUtls.isNotBlank(config)){
            return config;
        }
        return System.getProperty(dataId);
    }
}
