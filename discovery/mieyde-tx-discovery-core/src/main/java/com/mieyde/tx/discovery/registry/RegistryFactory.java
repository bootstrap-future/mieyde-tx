package com.mieyde.tx.discovery.registry;

import com.mieyde.tx.common.ConfigurationKeys;
import com.mieyde.tx.common.exception.NotSupportYetException;
import com.mieyde.tx.common.loader.EnhancedServiceLoader;
import com.mieyde.tx.config.ConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author 我吃稀饭面
 * @date 2023/7/10 14:29
 */
public class RegistryFactory {

    private static final Logger log = LoggerFactory.getLogger(RegistryFactory.class);

    public static RegistryService getInstance() {
        return RegistryFactoryHolder.INSTANCE;
    }

    private static RegistryService buildRegistryService() {
        RegistryType registryType;
        String registryTypeName = ConfigurationFactory.CURRENT_FILE_INSTANCE.getConfig(ConfigurationKeys.FILE_ROOT_REGISTRY + ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR + ConfigurationKeys.FILE_ROOT_TYPE);
        log.info("use registry center type: {}", registryTypeName);
        try {
            registryType = RegistryType.getType(registryTypeName);
        } catch (Exception exx) {
            throw new NotSupportYetException("not support registry type: " + registryTypeName);
        }
        return EnhancedServiceLoader.load(RegistryProvider.class, Objects.requireNonNull(registryType.name())).provide();
    }
    private static class RegistryFactoryHolder {
        private static final RegistryService INSTANCE = buildRegistryService();
    }
}
