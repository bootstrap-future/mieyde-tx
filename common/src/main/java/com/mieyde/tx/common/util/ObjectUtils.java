package com.mieyde.tx.common.util;

import cn.hutool.extra.cglib.CglibUtil;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

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

    /**
     * 拷贝Bean对象属性到目标类型<br>
     * 此方法通过指定目标类型自动创建之，然后拷贝属性
     *
     * @param <T>         目标对象类型
     * @param source      源bean对象
     * @param targetClass 目标bean类，自动实例化此对象
     * @return 目标对象
     */
    public static <T> T copy(Object source, Class<T> targetClass) {
        return CglibUtil.copy(source, targetClass, null);
    }

    /**
     * 拷贝Bean对象属性
     *
     * @param source 源bean对象
     * @param target 目标bean对象
     */
    public static void copy(Object source, Object target) {
        CglibUtil.copy(source, target, null);
    }

    /**
     * 拷贝List Bean对象属性
     *
     * @param <S>    源bean类型
     * @param <T>    目标bean类型
     * @param source 源bean对象list
     * @param target 目标bean对象
     * @return 目标bean对象list
     */
    public static <S, T> List<T> copyList(Collection<S> source, Supplier<T> target) {
        return CglibUtil.copyList(source, target, null, null);
    }

    /**
     * 拷贝List Bean对象属性
     *
     * @param source   源bean对象list
     * @param target   目标bean对象
     * @param callback 回调对象
     * @param <S>      源bean类型
     * @param <T>      目标bean类型
     * @return 目标bean对象list
     * @since 5.4.1
     */
    public static <S, T> List<T> copyList(Collection<S> source, Supplier<T> target, BiConsumer<S, T> callback) {
        return CglibUtil.copyList(source, target, null, callback);
    }

}
