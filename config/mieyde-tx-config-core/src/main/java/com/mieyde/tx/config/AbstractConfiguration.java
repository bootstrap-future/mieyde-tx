package com.mieyde.tx.config;

import com.mieyde.tx.common.util.StringUtls;

/**
 * @author 我吃稀饭面
 * @date 2023/6/28 22:10
 */
public abstract class AbstractConfiguration implements Configuration{

    @Override
    public String getConfig(String dataId, String defaultValue) {
        String value = getConfigBySystem(dataId);
        if (StringUtls.isNotBlank(value)){
            return value;
        }
        return getLatestConfig(dataId,defaultValue);
    }

    public abstract String getTypeName();
}
