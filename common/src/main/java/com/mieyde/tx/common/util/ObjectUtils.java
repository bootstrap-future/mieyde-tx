package com.mieyde.tx.common.util;

import java.util.Objects;

/**
 * 对象工具
 *
 * @author 我吃稀饭面
 * @date 2023/6/25 16:48
 */
public class ObjectUtils {

    public static boolean isNull(Object object){
        return Objects.isNull(object);
    }

    public static boolean isNotNull(Object object){
        return Objects.nonNull(object);
    }

    public static boolean equals(Object obj1,Object obj2){
        return Objects.equals(obj1,obj2);
    }
}
