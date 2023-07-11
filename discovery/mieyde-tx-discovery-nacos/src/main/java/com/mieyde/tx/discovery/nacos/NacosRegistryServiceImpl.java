package com.mieyde.tx.discovery.nacos;

import com.mieyde.tx.discovery.registry.RegistryService;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author 我吃稀饭面
 * @date 2023/7/11 11:45
 */
public class NacosRegistryServiceImpl implements RegistryService {
    @Override
    public void register(InetSocketAddress address) throws Exception {

    }

    @Override
    public void unregister(InetSocketAddress address) throws Exception {

    }

    @Override
    public void subscribe(String cluster) throws Exception {

    }

    @Override
    public void unsubscribe(String cluster) throws Exception {

    }

    @Override
    public List<InetSocketAddress> lookup(String key) throws Exception {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
