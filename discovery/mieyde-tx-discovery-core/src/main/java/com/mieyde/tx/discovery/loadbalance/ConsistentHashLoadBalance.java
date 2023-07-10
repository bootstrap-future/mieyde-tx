package com.mieyde.tx.discovery.loadbalance;

import com.mieyde.tx.common.loader.LoadLevel;
import com.mieyde.tx.common.util.CollectionUtils;
import com.mieyde.tx.config.ConfigurationFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.mieyde.tx.discovery.loadbalance.LoadBalanceFactory.LOAD_BALANCE_PREFIX;

/**
 * @author 我吃稀饭面
 * @date 2023/7/10 18:04
 */
@LoadLevel(name = LoadBalanceFactory.CONSISTENT_HASH_LOAD_BALANCE)
public class ConsistentHashLoadBalance implements LoadBalance{

    public static final String LOAD_BALANCE_CONSISTENT_HASH_VIRTUAL_NODES = LOAD_BALANCE_PREFIX + "virtualNodes";
    private static final int VIRTUAL_NODES_DEFAULT = 10;

    private static final int VIRTUAL_NODES_NUM = Integer.valueOf(ConfigurationFactory.getInstance().getConfig(LOAD_BALANCE_CONSISTENT_HASH_VIRTUAL_NODES, String.valueOf(VIRTUAL_NODES_DEFAULT)));

    @Override
    public <T> T select(List<T> invokers, String xid) throws Exception {
        return new ConsistentHashSelector<T>(invokers,VIRTUAL_NODES_NUM).select(xid);
    }

    private static final class ConsistentHashSelector<T> {
        private final SortedMap<Long, T> virtualInvokers = new TreeMap<>();
        private final HashFunction hashFunction = new MD5Hash();

        ConsistentHashSelector(List<T> invokers, int virtualNodes) {
            for (T invoker : invokers) {
                for (int i = 0; i < virtualNodes; i++) {
                    virtualInvokers.put(hashFunction.hash(invoker.toString() + i),invoker);
                }
            }
        }

        public T select(String objectKey){
            SortedMap<Long, T> tailMap = virtualInvokers.tailMap(hashFunction.hash(objectKey));
            Long nodeHashVal = CollectionUtils.isEmpty(tailMap) ? virtualInvokers.firstKey() : tailMap.firstKey();
            return virtualInvokers.get(nodeHashVal);
        }
    }

    private static class MD5Hash implements HashFunction {

        MessageDigest instance;

        public MD5Hash() {
            try {
                instance = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        @Override
        public long hash(String key) {
            instance.reset();
            instance.update(key.getBytes());
            byte[] digest = instance.digest();
            long h = 0;
            for (int i = 0; i < 4; i++) {
                h <<= 8;
                h |= ((int) digest[i]) & 0xFF;
            }
            return h;
        }
    }

    public interface HashFunction{

        long hash(String key);
    }
}
