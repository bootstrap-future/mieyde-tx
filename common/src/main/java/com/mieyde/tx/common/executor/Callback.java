package com.mieyde.tx.common.executor;

/**
 * @author 我吃稀饭面
 * @date 2023/6/25 14:48
 */
public interface Callback<T> {

    T execute() throws Throwable;
}
