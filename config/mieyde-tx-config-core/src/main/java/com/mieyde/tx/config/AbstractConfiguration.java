package com.mieyde.tx.config;

import com.mieyde.tx.common.util.StringUtils;

/**
 * @author 我吃稀饭面
 * @date 2023/6/28 22:10
 */
public abstract class AbstractConfiguration implements Configuration{

    @Override
    public String getConfig(String dataId, String defaultValue) {
        String value = getConfigBySystem(dataId);
        if (StringUtils.isNotBlank(value)){
            return value;
        }
        return getLatestConfig(dataId,defaultValue);
    }

    @Override
    public String getConfig(String dataId) {
        return getConfig(dataId,null);
    }

    public abstract String getTypeName();
}
