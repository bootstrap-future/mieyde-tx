package com.mieyde.tx.common.loader;

import java.lang.annotation.*;

/**
 * @author 我吃稀饭面
 * @date 2023/6/27 15:51
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface LoadLevel {
    String name();

    int order() default 0;

    Scope scope() default Scope.SINGLETON;
}
