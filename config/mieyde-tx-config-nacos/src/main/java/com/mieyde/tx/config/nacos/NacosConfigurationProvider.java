package com.mieyde.tx.config.nacos;

import com.mieyde.tx.common.loader.LoadLevel;
import com.mieyde.tx.config.Configuration;
import com.mieyde.tx.config.ConfigurationProvider;

/**
 * @author 我吃稀饭面
 * @date 2023/7/4 18:00
 */
@LoadLevel(name = "Nacos",order = 1)
public class NacosConfigurationProvider implements ConfigurationProvider {
    /**
     * provide a AbstractConfiguration implementation instance
     *
     * @return Configuration
     */
    @Override
    public Configuration provide() {
        return null;
    }
}
