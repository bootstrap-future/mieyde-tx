package com.mieyde.tx.config;

/**
 * @author 我吃稀饭面
 * @date 2023/6/28 22:18
 */
public class FileConfiguration extends AbstractConfiguration{
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
