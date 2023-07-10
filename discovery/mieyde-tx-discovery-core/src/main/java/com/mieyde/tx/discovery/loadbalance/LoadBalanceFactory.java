package com.mieyde.tx.discovery.loadbalance;

import com.mieyde.tx.common.loader.EnhancedServiceLoader;
import com.mieyde.tx.config.ConfigurationFactory;

/**
 * @author 我吃稀饭面
 * @date 2023/7/10 14:59
 */
public class LoadBalanceFactory {
    private static final String CLIENT_PREFIX = "client.";

    public static final String LOAD_BALANCE_PREFIX = CLIENT_PREFIX + "loadBalance.";

    public static final String LOAD_BALANCE_TYPE = LOAD_BALANCE_PREFIX + "type";

    public static final String RANDOM_LOAD_BALANCE = "RandomLoadBalance";

    public static final String XID_LOAD_BALANCE = "XID";

    public static final String ROUND_ROBIN_LOAD_BALANCE = "RoundRobinLoadBalance";

    public static final String CONSISTENT_HASH_LOAD_BALANCE = "ConsistentHashLoadBalance";

    public static final String LEAST_ACTIVE_LOAD_BALANCE = "LeastActiveLoadBalance";

    public static LoadBalance getInstance() {
        String config = ConfigurationFactory.getInstance().getConfig(LOAD_BALANCE_TYPE, XID_LOAD_BALANCE);
        return EnhancedServiceLoader.load(LoadBalance.class, config);
    }
}
