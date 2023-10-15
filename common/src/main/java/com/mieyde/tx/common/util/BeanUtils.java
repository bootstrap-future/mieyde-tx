package com.mieyde.tx.common.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.mieyde.tx.common.exception.NotSupportYetException;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * bean工具
 *
 * @author 我吃稀饭面
 * @date 2023/6/25 15:34
 */
public class BeanUtils {

    public static <T> T mapToBean(Map<String, String> map, Class<T> clazz) {
        if (CollectionUtils.isEmpty(map)){
            return null;
        }

        try {
            T instance = clazz.newInstance();
            Field[] fields = instance.getClass().getDeclaredFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)){
                    continue;
                }
                String value = map.get(field.getName());
                if (StrUtil.isBlank(value)){
                    continue;
                }
                Class<?> type = field.getType();
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                if (ObjectUtils.equals(type, Date.class) || ObjectUtils.equals(type, DateTime.class)){
                    field.set(instance,DateUtil.parseDateTime(value));
                }else if (ObjectUtils.equals(type, LocalDateTime.class) || ObjectUtils.equals(type, LocalDate.class)){
                    field.set(instance,DateUtil.parseLocalDateTime(value));
                }else if (ObjectUtils.equals(type, Long.class)){
                    field.set(instance,Long.valueOf(value));
                }else if (ObjectUtils.equals(type, Integer.class)){
                    field.set(instance,Integer.valueOf(value));
                }else if (ObjectUtils.equals(type, Double.class)){
                    field.set(instance,Double.valueOf(value));
                }else if (ObjectUtils.equals(type, String.class)){
                    field.set(instance,value);
                }
                field.setAccessible(accessible);
            }
            return instance;
        } catch (InstantiationException e) {
            throw new NotSupportYetException("map to " + clazz.toString() + " failed:" + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new NotSupportYetException("map to " + clazz.toString() + " failed:" + e.getMessage(), e);
        }
    }

    public static Map<String, String> beanToMap(Object object) {
        Map<String, String> map = new LinkedHashMap<>();
        if (ObjectUtils.isNull(object)){
            return map;
        }
        Field[] fields = object.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                Object value = field.get(object);
                if (ObjectUtils.isNull(value)){
                    continue;
                }
                Class<?> type = field.getType();
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                if (ObjectUtils.equals(type, Date.class) || ObjectUtils.equals(type, DateTime.class)){
                    DateTime date = (DateTime) field.get(object);
                    if (date != null) {
                        map.put(field.getName(), String.valueOf(date.getTime()));
                    }
                } else {
                    map.put(field.getName(), field.get(object).toString());
                }
                field.setAccessible(accessible);
            }
        } catch (IllegalAccessException e) {
            throw new NotSupportYetException(
                    "object " + object.getClass().toString() + " to map failed:" + e.getMessage());
        }
        return map;
    }

    public static <E,T> T copy(final Object source, final Class<T> clazz, BeanCopyFunctional<E>... ignoreProperties) {
        if (source == null) {
            return null;
        }
        try {
            List<String> ignorePropertiesData = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(ignoreProperties)) {
                for (BeanCopyFunctional<E> ignoreProperty : ignoreProperties) {
                    ignorePropertiesData.add(getPropertyName(ignoreProperty));
                }
            }
            T t = clazz.getConstructor().newInstance();
            if (CollectionUtils.isNotEmpty(ignorePropertiesData)){
                BeanUtil.copyProperties(source,t,ignorePropertiesData.toArray(new String[ignorePropertiesData.size()]));
            }else {
                BeanUtil.copyProperties(source,t);
            }
            return t;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("对象copy出错");
        }
    }

    private static <E> String getPropertyName(BeanCopyFunctional<E> lambda){
        try {
            Method method = lambda.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambda);
            String methodName = serializedLambda.getImplMethodName();
            String methodPrefix="get";
            if (methodName.startsWith("is")){
                methodPrefix = "is";
            }
            return Introspector.decapitalize(methodName.replace(methodPrefix, ""));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <E, V> List<V> copyListToList(List<E> source, Class<V> clazz) {
        if(source == null){
            return Collections.emptyList();
        }
        return source.stream().map(pojo -> copy(pojo, clazz)).collect(toList());
    }

    public static <E, V> List<V> copyListToList(List<E> source, Class<V> clazz, BeanCopyFunctional<E>... ignoreProperties) {
        if(source == null){
            return Collections.emptyList();
        }
        return source.stream().map(pojo -> copy(pojo, clazz,ignoreProperties)).collect(toList());
    }

}
