package com.mieyde.tx.config;

import com.mieyde.tx.common.ConfigurationKeys;
import com.mieyde.tx.common.exception.NotSupportYetException;
import com.mieyde.tx.common.loader.EnhancedServiceLoader;
import com.mieyde.tx.common.util.ObjectUtils;
import com.mieyde.tx.common.util.StringUtls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author 我吃稀饭面
 * @date 2023/7/4 16:46
 */
public final class ConfigurationFactory {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationFactory.class);

    private static final String SYSTEM_PROPERTY_SEATA_CONFIG_NAME = "seata.config.name";
    private static final String ENV_SEATA_CONFIG_NAME = "SEATA_CONFIG_NAME";
    private static final String REGISTRY_CONF_DEFAULT = "registry";

    public static final String ENV_PROPERTY_KEY = "seataEnv";
    private static final String ENV_SYSTEM_KEY = "SEATA_ENV";

    public static Configuration CURRENT_FILE_INSTANCE;

    static {
        load();
    }

    private static void load(){
        String seataConfigName = System.getProperty(SYSTEM_PROPERTY_SEATA_CONFIG_NAME);
        if (StringUtls.isBlank(seataConfigName)){
            seataConfigName = System.getenv(ENV_SEATA_CONFIG_NAME);
        }
        if (StringUtls.isBlank(seataConfigName)){
            seataConfigName = REGISTRY_CONF_DEFAULT;
        }

        String envValue = System.getProperty(ENV_PROPERTY_KEY);
        if (StringUtls.isBlank(envValue)){
            envValue = System.getenv(ENV_SYSTEM_KEY);
        }

        Configuration fileConfiguration = StringUtls.isBlank(envValue) ? new FileConfiguration(seataConfigName) : new FileConfiguration(seataConfigName + "-" + envValue);

        Configuration extConfiguration = null;
        try {
            extConfiguration = EnhancedServiceLoader.load(ExtConfigurationProvider.class).provide(fileConfiguration);
        }catch (Exception e){
            log.error("failed to load extConfiguration: {}", e.getMessage(), e);
        }

        CURRENT_FILE_INSTANCE = ObjectUtils.isNull(extConfiguration) ? fileConfiguration : extConfiguration;
    }

    //--------------------------------------------------以上是配置初始加载----------------------------------------------------------------

    private static final String NAME_KEY = "name";
    private static final String FILE_TYPE = "file";

    private static volatile Configuration instance = null;

    public static Configuration getInstance(){
        if (ObjectUtils.isNull(instance)){
            synchronized (Configuration.class){
                if (ObjectUtils.isNull(instance)){
                    instance = buildConfiguration();
                }
            }
        }
        return instance;
    }

    private static Configuration buildConfiguration() {
        String configTypeName = CURRENT_FILE_INSTANCE.getConfig(ConfigurationKeys.FILE_ROOT_CONFIG + ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR + ConfigurationKeys.FILE_ROOT_TYPE);
        log.info("use configuration center type: {}", configTypeName);
        if (StringUtls.isBlank(configTypeName)){
            throw new NotSupportYetException("config type can not be null");
        }
        ConfigType configType = ConfigType.convertType(configTypeName);

        Configuration extConfiguration = null;
        Configuration configuration = null;
        if (ObjectUtils.equals(configType,ConfigType.File)){
            //配置的是文件方式加载配置
            String pathDataId = StringUtls.join(ConfigurationKeys.FILE_CONFIG_SPLIT_CHAR, ConfigurationKeys.FILE_ROOT_CONFIG, FILE_TYPE, NAME_KEY);
            String name = CURRENT_FILE_INSTANCE.getConfig(pathDataId);
            configuration = new FileConfiguration(name);
            try {
                extConfiguration = EnhancedServiceLoader.load(ExtConfigurationProvider.class).provide(configuration);
            }catch (Exception e){

            }
        }else {
            //非文件方式加载配置
            configuration = EnhancedServiceLoader.load(ConfigurationProvider.class, Objects.requireNonNull(configType).name()).provide();
        }
        return ObjectUtils.isNull(extConfiguration) ? configuration : extConfiguration;
    }

    protected static void reload() {
        load();
        instance = null;
        getInstance();
    }
}
