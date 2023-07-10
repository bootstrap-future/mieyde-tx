package com.mieyde.tx.discovery.loadbalance;

import com.mieyde.tx.common.loader.LoadLevel;
import com.mieyde.tx.common.rpc.RpcStatus;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author 我吃稀饭面
 * @date 2023/7/10 15:24
 */
@LoadLevel(name = LoadBalanceFactory.LEAST_ACTIVE_LOAD_BALANCE)
public class LeastActiveLoadBalance implements LoadBalance{
    @Override
    public <T> T select(List<T> invokers, String xid) throws Exception {
        int length = invokers.size();
        long leastActive = -1;
        int leastCount = 0;
        int[] leastIndexes = new int[length];
        for (int i = 0; i < length; i++) {
            long active = RpcStatus.getStatus(invokers.get(i).toString()).getActive();
            if (leastActive == -1 || active < leastActive) {
                leastActive = active;
                leastCount = 1;
                leastIndexes[0] = i;
            } else if (active == leastActive) {
                leastIndexes[leastCount++] = i;
            }
        }
        if (leastCount == 1) {
            return invokers.get(leastIndexes[0]);
        }
        return invokers.get(leastIndexes[ThreadLocalRandom.current().nextInt(leastCount)]);
    }
}
