package com.mieyde.tx.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 数字工具
 *
 * @author 我吃稀饭面
 * @date 2023/6/26 22:05
 */
public class NumberUtils {

    public static int toInt(final String str, final int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static long toLong(String str, final long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static double toDouble(String str, final double defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Double.valueOf(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static BigDecimal toBigDecimal(Object obj, final BigDecimal defaultValue) {
        if (ObjectUtils.isNull(obj)) {
            return defaultValue;
        }
        try {
            return new BigDecimal(String.valueOf(obj));
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * 小数位数处理
     */
    public static BigDecimal numberDecimal(BigDecimal number, int scale, RoundingMode roundingMode){
        return number.setScale(scale, roundingMode);
    }

    /**
     * 通入
     */
    public static BigDecimal numberDecimalUp(BigDecimal number,int scale){
        return numberDecimal(number,scale,RoundingMode.UP);
    }

    /**
     * 通舍
     */
    public static BigDecimal numberDecimalDown(BigDecimal number,int scale){
        return numberDecimal(number,scale,RoundingMode.DOWN);
    }

    /**
     * 四舍五入
     * 2.35->2.4
     */
    public static BigDecimal numberDecimalHalfUp(BigDecimal number,int scale){
        return numberDecimal(number,scale,RoundingMode.HALF_UP);
    }

    /**
     * 四舍五入(也就是五舍六入)
     * 2.35->2.3
     */
    public static BigDecimal numberDecimalHalfDown(BigDecimal number,int scale){
        return numberDecimal(number,scale,RoundingMode.HALF_DOWN);
    }
}
