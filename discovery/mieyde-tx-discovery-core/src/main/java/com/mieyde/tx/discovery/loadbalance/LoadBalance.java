package com.mieyde.tx.discovery.loadbalance;

import java.util.List;

/**
 * @author 我吃稀饭面
 * @date 2023/7/10 14:57
 */
public interface LoadBalance {

    <T> T select(List<T> invokers, String xid) throws Exception;
}
