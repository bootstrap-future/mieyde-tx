package com.mieyde.tx.common.util;

import cn.hutool.core.util.StrUtil;

/**
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

}
