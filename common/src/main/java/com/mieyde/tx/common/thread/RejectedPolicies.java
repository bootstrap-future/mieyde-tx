package com.mieyde.tx.common.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;

/**
 * 拒绝策略
 *
 * @author 我吃稀饭面
 * @date 2023/6/27 10:28
 */
public class RejectedPolicies {

    /**
     * 将队列头任务取出来，并将新任务放入队尾，如果取出来的任务不为null，则当前线程执行取出来的任务；再判断任务是否插入到队尾，如果已插入就结束，否则当前线程执行未插入成功的任务
     */
    public static RejectedExecutionHandler runsOldestTaskPolicy() {
        return (r, executor) -> {
            if (executor.isShutdown()) {
                return;
            }
            BlockingQueue<Runnable> workQueue = executor.getQueue();
            Runnable firstWork = workQueue.poll();
            boolean newTaskAdd = workQueue.offer(r);
            if (firstWork != null) {
                firstWork.run();
            }
            if (!newTaskAdd) {
                executor.execute(r);
            }
        };
    }
}
