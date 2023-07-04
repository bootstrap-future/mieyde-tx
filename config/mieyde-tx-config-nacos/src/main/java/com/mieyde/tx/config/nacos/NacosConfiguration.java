package com.mieyde.tx.config.nacos;

import com.mieyde.tx.config.AbstractConfiguration;

/**
 * @author 我吃稀饭面
 * @date 2023/7/4 17:59
 */
public class NacosConfiguration extends AbstractConfiguration {
    @Override
    public String getTypeName() {
        return null;
    }

    @Override
    public String getLatestConfig(String dataId, String defaultValue) {
        return null;
    }

    @Override
    public boolean putConfig(String dataId, String content) {
        return false;
    }

    @Override
    public boolean putConfigIfAbsent(String dataId, String content) {
        return false;
    }

    @Override
    public boolean removeConfig(String dataId) {
        return false;
    }
}
