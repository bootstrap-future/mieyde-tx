package com.mieyde.tx.common.thread;

import com.mieyde.tx.common.util.CollectionUtils;
import com.mieyde.tx.common.util.StringUtls;
import io.netty.util.concurrent.FastThreadLocalThread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 我吃稀饭面
 * @date 2023/6/25 15:19
 */
public class NamedThreadFactory implements ThreadFactory {

    private final static Map<String, AtomicInteger> PREFIX_COUNTER = new ConcurrentHashMap<>();
    private final ThreadGroup group;
    private final AtomicInteger counter = new AtomicInteger(0);
    private final String prefix;
    private final int totalSize;
    private final boolean makeDaemons;

    public NamedThreadFactory(String prefix, int totalSize, boolean makeDaemons) {
        int prefixCounter = CollectionUtils.computeIfAbsent(PREFIX_COUNTER, prefix, key -> new AtomicInteger(0)).incrementAndGet();
        this.group = Thread.currentThread().getThreadGroup();
        this.prefix = StringUtls.join("_",prefix,prefixCounter);
        this.totalSize = totalSize;
        this.makeDaemons = makeDaemons;
    }

    public NamedThreadFactory(String prefix, boolean makeDaemons) {
        this(prefix, 0, makeDaemons);
    }

    public NamedThreadFactory(String prefix, int totalSize) {
        this(prefix, totalSize, true);
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = StringUtls.join("_",prefix,counter.incrementAndGet());
        if (totalSize > 1){
            name = StringUtls.join("_",name,totalSize);
        }

        Thread thread = new FastThreadLocalThread(group, r, name);
        thread.setDaemon(makeDaemons);
        if (thread.getPriority() != Thread.NORM_PRIORITY){
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
