package com.mieyde.tx.discovery.nacos;

import com.mieyde.tx.common.loader.LoadLevel;
import com.mieyde.tx.discovery.registry.RegistryProvider;
import com.mieyde.tx.discovery.registry.RegistryService;

/**
 * @author 我吃稀饭面
 * @date 2023/7/11 11:45
 */
@LoadLevel(name = "Nacos",order = 1)
public class NacosRegistryProvider implements RegistryProvider {
    @Override
    public RegistryService provide() {
        return null;
    }
}
