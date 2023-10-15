package com.mieyde.tx.common.util;

import java.io.Serializable;

/**
 * @author 我吃稀饭面
 * @date 2023/10/15 12:04
 */
@FunctionalInterface
public interface BeanCopyFunctional<T> extends Serializable {

    Object apply(T source);
}
