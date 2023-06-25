package com.mieyde.tx.common.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;

import java.util.*;
import java.util.function.Function;

/**
 * @author 我吃稀饭面
 * @date 2023/6/25 16:46
 */
public class CollectionUtils {

    private static final String KV_SPLIT = "=";

    private static final String PAIR_SPLIT = "&";

    /**
     * 集合判空
     */
    public static boolean isEmpty(Collection<?> col) {
        return CollectionUtil.isEmpty(col);
    }

    /**
     * Map集合判空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return CollectionUtil.isEmpty(map);
    }

    /**
     * 数组判空
     */
    public static <T> boolean isEmpty(T[] arrays){
        return ArrayUtil.isEmpty(arrays);
    }

    /**
     * 集合判非空
     */
    public static boolean isNotEmpty(Collection<?> col) {
        return CollectionUtil.isNotEmpty(col);
    }

    /**
     * Map集合判非空
     */
    public static boolean isNotEmpty(Map<?,?> map) {
        return CollectionUtil.isNotEmpty(map);
    }

    /**
     * 数组判非空
     */
    public static <T> boolean isNotEmpty(T[] arrays) {
        return ArrayUtil.isNotEmpty(arrays);
    }

    /**
     * 两集合大小是否相同
     */
    public static boolean isSizeEquals(Collection<?> col1,Collection<?> col2){
        if (isEmpty(col1)){
            return isEmpty(col2);
        }else if (isEmpty(col2)){
            return false;
        }else {
            return col1.size() == col2.size();
        }
    }

    /**
     * 集合按分隔符转字符串
     */
    public static <T> String join(Iterable<T> iterable, String split) {
        if (null == iterable) {
            return null;
        }
        return CollectionUtil.join(iterable,split);
    }

    /**
     * map转分割字符串
     */
    public static String encodeMap(Map<String,String> map){
        return encodeMap(map,KV_SPLIT,PAIR_SPLIT);
    }

    /**
     * map转分割字符串
     */
    public static String encodeMap(Map<String,String> map,String kvSplit,String pairSplit){
        if (isEmpty(map)){
            return "";
        }
        if (StringUtls.isBlank(pairSplit)){
            throw new IllegalArgumentException("map split is not null");
        }
        if (StringUtls.isBlank(kvSplit)){
            throw new IllegalArgumentException("map kvSplit is not null");
        }
        List<String> list = new ArrayList<>();
        for (Map.Entry<String,String> entry:map.entrySet()){
            list.add(entry.getKey() + kvSplit + entry.getValue());
        }
        return CollectionUtil.join(list,pairSplit);
    }

    /**
     * 字符串转map
     */
    public static Map<String,String> decodeMap(String data){
        return decodeMap(data,KV_SPLIT,PAIR_SPLIT);
    }

    /**
     * 字符串转map
     */
    public static Map<String,String> decodeMap(String data,String kvSplit,String pairSplit){
        Map<String, String> map = new LinkedHashMap<>();
        if (StringUtls.isBlank(data)) {
            return map;
        }
        if (StringUtls.isBlank(pairSplit)){
            throw new IllegalArgumentException("map split is not null");
        }
        if (StringUtls.isBlank(kvSplit)){
            throw new IllegalArgumentException("map kvSplit is not null");
        }
        String[] kvPairs = data.split(pairSplit);
        if (isEmpty(kvPairs)) {
            return map;
        }
        for (String kvPair : kvPairs) {
            if (StringUtls.isBlank(kvPair)) {
                continue;
            }
            String[] kvs = kvPair.split(kvSplit);
            if (isEmpty(kvs) || kvs.length != 2) {
                continue;
            }
            map.put(kvs[0], kvs[1]);
        }
        return map;
    }

    /**
     * 向map中取值，如果不存在则执行function逻辑，存在则返回对应的值。如果map为null，则新建一个LinkedHashMap
     */
    public static <K,V> V computeIfAbsent(Map<K,V> map, K key, Function<? super K, ? extends V> mappingFunction){
        if (isEmpty(map)){
            map = new LinkedHashMap<K,V>();
        }
        V value = map.get(key);
        if (value != null) {
            return value;
        }
        return map.computeIfAbsent(key,mappingFunction);
    }

    /**
     * 获取集合最后一个数据，在并发情况下，下标可能会越界，采用永真循环取数据，直到取到为止
     */
    public static <T> T getLast(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }

        while (true) {
            int size = list.size();
            if (size == 0) {
                return null;
            }

            try {
                return list.get(size - 1);
            } catch (IndexOutOfBoundsException ex) {
                // catch the exception and continue to retry
            }
        }
    }

}
