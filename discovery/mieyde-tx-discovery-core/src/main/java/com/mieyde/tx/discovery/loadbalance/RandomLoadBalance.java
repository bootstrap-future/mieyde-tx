package com.mieyde.tx.discovery.loadbalance;

import com.mieyde.tx.common.loader.LoadLevel;
import com.mieyde.tx.common.util.RandomUtils;

import java.util.List;

/**
 * @author 我吃稀饭面
 * @date 2023/7/10 15:22
 */
@LoadLevel(name = LoadBalanceFactory.RANDOM_LOAD_BALANCE)
public class RandomLoadBalance implements LoadBalance{
    @Override
    public <T> T select(List<T> invokers, String xid) throws Exception {
        return RandomUtils.randomEle(invokers);
    }
}
