package com.mieyde.tx.common.util;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang.StringUtils;

/**
 * 字符串工具
 *
 * @author 我吃稀饭面
 * @date 2023/6/25 16:48
 */
public class StringUtls {

    public static boolean isBlank(String str){
        return StrUtil.isBlank(str);
    }

    public static boolean isNotBlank(String str){
        return StrUtil.isNotBlank(str);
    }

    public static boolean equals(String str1,String str2){
        return StrUtil.equals(str1,str2);
    }

    public static String join(CharSequence conjunction, Object... objs){
        return StrUtil.join(conjunction,objs);
    }

    public static <T> String join(CharSequence conjunction, Iterable<T> iterable){
        return StrUtil.join(conjunction,iterable);
    }

}
