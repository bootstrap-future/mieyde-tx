package com.mieyde.tx.discovery.loadbalance;

import com.mieyde.tx.common.loader.EnhancedServiceLoader;
import com.mieyde.tx.common.loader.LoadLevel;
import com.mieyde.tx.common.util.ObjectUtils;
import com.mieyde.tx.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author 我吃稀饭面
 * @date 2023/7/10 15:14
 */
@LoadLevel(name = LoadBalanceFactory.XID_LOAD_BALANCE)
public class XIDLoadBalance implements LoadBalance{

    private static final Logger log = LoggerFactory.getLogger(XIDLoadBalance.class);

    private static final LoadBalance RANDOM_LOAD_BALANCE = EnhancedServiceLoader.load(LoadBalance.class,LoadBalanceFactory.RANDOM_LOAD_BALANCE);

    private static final String SPLIT = ":";

    @Override
    public <T> T select(List<T> invokers, String xid) throws Exception {
        if (StringUtils.isNotBlank(xid) && xid.contains(SPLIT)){
            String serverAddress = xid.substring(0, xid.lastIndexOf(SPLIT));
            int index = serverAddress.lastIndexOf(SPLIT);
            int port = Integer.parseInt(serverAddress.substring(index + 1));
            String ip = serverAddress.substring(0, index);
            InetSocketAddress xidInetSocketAddress = new InetSocketAddress(ip, port);
            for (T invoker : invokers) {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) invoker;
                if (ObjectUtils.equals(xidInetSocketAddress,inetSocketAddress)){
                    return invoker;
                }
            }
            log.error("not found mieyde-server channel,xid: {}, try use random load balance", xid);
        }
        return RANDOM_LOAD_BALANCE.select(invokers,xid);
    }
}
