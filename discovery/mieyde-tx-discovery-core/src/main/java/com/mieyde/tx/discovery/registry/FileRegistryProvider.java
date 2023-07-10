package com.mieyde.tx.discovery.registry;

import com.mieyde.tx.common.loader.LoadLevel;

/**
 * @author 我吃稀饭面
 * @date 2023/7/10 14:04
 */
@LoadLevel(name = "File",order = 1)
public class FileRegistryProvider implements RegistryProvider{
    @Override
    public RegistryService provide() {
        return FileRegistryServiceImpl.getInstance();
    }
}
