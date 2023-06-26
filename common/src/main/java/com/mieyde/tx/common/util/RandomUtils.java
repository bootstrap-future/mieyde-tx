package com.mieyde.tx.common.util;

import cn.hutool.core.util.RandomUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 随机工具
 *
 * @author 我吃稀饭面
 * @date 2023/6/26 11:56
 */
public class RandomUtils {

    /**
     * 获得随机Boolean值
     */
    public static boolean randomBoolean() {
        return RandomUtil.randomBoolean();
    }

    /**
     * 获得指定范围内的随机数
     * Params:
     * min – 最小数（包含） max – 最大数（不包含）
     */
    public static int randomInt(int min, int max) {
        return RandomUtil.randomInt(min,max);
    }

    /**
     * 获得指定范围内的随机数
     * Params:
     * min – 最小数（包含） max – 最大数（不包含）
     */
    public static long randomLong(int min, int max) {
        return RandomUtil.randomLong(min,max);
    }

    /**
     * 获得指定范围内的随机数
     * Params:
     * min – 最小数（包含） max – 最大数（不包含）
     */
    public static double randomDouble(double min, double max) {
        return RandomUtil.randomDouble(min,max);
    }

    /**
     * 获得指定范围内的随机数
     * Params:
     * min – 最小数（包含） max – 最大数（不包含）scal = 保留小数位数 roundingMode – 保留小数的模式
     */
    public static double randomDouble(double min, double max,int scale, RoundingMode roundingMode) {
        return RandomUtil.randomDouble(min,max,scale,roundingMode);
    }

    /**
     * 获得指定范围内的随机数
     * Params:
     * min – 最小数（包含） max – 最大数（不包含）
     */
    public static BigDecimal randomBigDecimal(BigDecimal min, BigDecimal max) {
        return RandomUtil.randomBigDecimal(min,max);
    }

    /**
     * 随机获得数组中的元素
     */
    public static <T> T randomEle(T[] array) {
        return RandomUtil.randomEle(array);
    }

    /**
     * 随机获得数组中的元素
     */
    public static <T> T randomEle(T[] array, int limit) {
        return RandomUtil.randomEle(array,limit);
    }

    /**
     * 随机获得列表中的元素
     * Params:
     * list – 列表 limit – 限制列表的前N项
     */
    public static <T> T randomEle(List<T> list,int limit){
        if (CollectionUtils.isEmpty(list) || limit <= 0){
            return null;
        }
        return RandomUtil.randomEle(list,limit);
    }

    /**
     * 随机获得列表中的元素
     */
    public static <T> T randomEle(List<T> list){
        return randomEle(list,list.size());
    }

    /**
     * 随机获得列表中的一定量元素（有重复的概率）
     * Params:
     * list – 列表 count – 随机取出的个数
     * Returns:
     * 随机元素
     */
    public static <T> List<T> randomEles(List<T> list, int count) {
        if (CollectionUtils.isEmpty(list) || count <= 0){
            return null;
        }
        return RandomUtil.randomEles(list,count);
    }

    /**
     * 随机获得列表中的一定量的元素，返回List 此方法与randomEles(List, int) 不同点在于，不会获取重复位置的元素
     * Params:
     * source – 列表 count – 随机取出的个数
     * Returns:
     * 随机列表
     */
    public static <T> List<T> randomEleList(List<T> source, int count) {
        if (CollectionUtils.isEmpty(source) || count <= 0){
            return null;
        }
        return RandomUtil.randomEleList(source,count);
    }

    /**
     * 随机获得列表中的一定量的不重复元素，返回Set
     * Params:
     * collection – 列表 count – 随机取出的个数
     * Returns:
     * 随机元素
     */
    public static <T> Set<T> randomEleSet(Collection<T> collection, int count) {
        if (CollectionUtils.isEmpty(collection) || count <= 0){
            return null;
        }
        return RandomUtil.randomEleSet(collection,count);
    }

    /**
     * 获得一个随机的字符串（只包含数字和字符）
     */
    public static String randomString(int length) {
        return RandomUtil.randomString(length);
    }

    /**
     * 获得一个随机的字符串（只包含数字和大写字符）
     */
    public static String randomStringUpper(int length) {
        return RandomUtil.randomStringUpper(length);
    }
}
