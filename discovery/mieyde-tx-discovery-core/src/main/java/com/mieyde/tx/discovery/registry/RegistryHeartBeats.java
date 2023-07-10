package com.mieyde.tx.discovery.registry;

import com.mieyde.tx.config.Configuration;
import com.mieyde.tx.config.ConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author 我吃稀饭面
 * @date 2023/7/10 14:45
 */
public class RegistryHeartBeats {
    private static final Logger log = LoggerFactory.getLogger(RegistryHeartBeats.class);
    private static final String FILE_CONFIG_SPLIT_CHAR = ".";
    private static final String FILE_ROOT_REGISTRY = "registry";
    private static final String HEARTBEAT_KEY = "heartbeat";
    private static final String HEARTBEAT_ENABLED_KEY = "enabled";
    private static final String HEARTBEAT_PERIOD_KEY = "period";
    private static final long DEFAULT_HEARTBEAT_PERIOD = 60 * 1000;
    private static final Boolean DEFAULT_HEARTBEAT_ENABLED = Boolean.TRUE;
    private static final Configuration FILE_CONFIG = ConfigurationFactory.CURRENT_FILE_INSTANCE;

    private static final ScheduledExecutorService HEARTBEAT_SCHEDULED = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("mieyde-discovery-heartbeat");
            return thread;
        }
    });

    public static void addHeartBeat(String registryType, InetSocketAddress serverAddress, ReRegister reRegister) {
        addHeartBeat(registryType, serverAddress, getHeartbeatPeriod(registryType), reRegister);
    }

    public static void addHeartBeat(String registryType, InetSocketAddress serverAddress, long period, ReRegister reRegister) {
        if (!getHeartbeatEnabled(registryType)){
            log.info("registry heartbeat disabled");
        }
        HEARTBEAT_SCHEDULED.scheduleAtFixedRate(() -> {
            try {
                reRegister.register(serverAddress);
            } catch (Exception e) {
                log.error("seata registry heartbeat failed!", e);
            }
        },period,period, TimeUnit.MILLISECONDS);
    }

    private static boolean getHeartbeatEnabled(String registryType) {
        String propertySuffix = String.join("-", HEARTBEAT_KEY, HEARTBEAT_ENABLED_KEY);
        String config = FILE_CONFIG.getConfig(String.join(FILE_CONFIG_SPLIT_CHAR, FILE_ROOT_REGISTRY, registryType, propertySuffix), DEFAULT_HEARTBEAT_ENABLED.toString());
        return Boolean.parseBoolean(config);
    }

    private static long getHeartbeatPeriod(String registryType) {
        String propertySuffix = String.join("-", HEARTBEAT_KEY, HEARTBEAT_PERIOD_KEY);
        String config = FILE_CONFIG.getConfig(String.join(FILE_CONFIG_SPLIT_CHAR, FILE_ROOT_REGISTRY, registryType, propertySuffix), String.valueOf(DEFAULT_HEARTBEAT_PERIOD));
        return Long.valueOf(config);
    }


    @FunctionalInterface
    public interface ReRegister {

        /**
         * do re-register
         *
         * @param serverAddress the server address
         * @throws Exception the exception
         */
        void register(InetSocketAddress serverAddress) throws Exception;
    }
}
