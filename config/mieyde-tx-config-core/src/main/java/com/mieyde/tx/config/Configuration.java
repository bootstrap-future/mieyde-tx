package com.mieyde.tx.config;

import com.mieyde.tx.common.ConfigurationKeys;
import com.mieyde.tx.common.util.StringUtils;

import java.util.Map;

/**
 * @author 我吃稀饭面
 * @date 2023/6/28 14:12
 */
public interface Configuration {

    Map<String,String> ENV_MAP = System.getenv();

    String getConfig(String dataId,String defaultValue);
    String getConfig(String dataId);

    String getLatestConfig(String dataId,String defaultValue);

    boolean putConfig(String dataId, String content);

    boolean putConfigIfAbsent(String dataId, String content);

    boolean removeConfig(String dataId);

    /**
     * 获取系统变量值
     */
    default String getConfigBySystem(String dataId){
        if (StringUtils.isBlank(dataId)){
            return null;
        }
        String config = ENV_MAP.get(dataId);
        if (StringUtils.isNotBlank(config)){
            return config;
        }
        config = ENV_MAP.get(dataId.toUpperCase().replace(".","_"));
        if (StringUtils.isNotBlank(config)){
            return config;
        }
        return System.getProperty(dataId);
    }

    default String getDataTypeKey() {
        return String.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_CONFIG, ConfigurationKeys.DATA_TYPE);
    }
}
