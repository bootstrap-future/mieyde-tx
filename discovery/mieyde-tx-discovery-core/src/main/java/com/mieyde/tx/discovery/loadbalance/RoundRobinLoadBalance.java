package com.mieyde.tx.discovery.loadbalance;

import com.mieyde.tx.common.loader.LoadLevel;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 我吃稀饭面
 * @date 2023/7/10 17:31
 */
@LoadLevel(name = LoadBalanceFactory.ROUND_ROBIN_LOAD_BALANCE)
public class RoundRobinLoadBalance implements LoadBalance{

    private final AtomicInteger sequence = new AtomicInteger();
    @Override
    public <T> T select(List<T> invokers, String xid) throws Exception {
        int size = invokers.size();
        return invokers.get(getPositiveSequence() % size);
    }

    /**
     * cas设置下次请求的序列号
     */
    private int getPositiveSequence(){
        for (;;){
            int current = sequence.get();
            int next = current >= Integer.MAX_VALUE ? 0 : current + 1;
            if (sequence.compareAndSet(current,next)){
                return current;
            }
        }
    }

}
