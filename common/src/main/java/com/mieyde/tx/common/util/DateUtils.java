package com.mieyde.tx.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 我吃稀饭面
 * @date 2023/6/25 23:14
 */
public class DateUtils {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 获取当前时间
     */
    public static Date currentDate(){
        return new Date();
    }

    /**
     * 字符串转date
     */
    public static Date parseDate(String dateStr,String format){
        if (StringUtls.isBlank(dateStr)){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期时间格式错误");
        }
    }

    /**
     * 字符串转date
     */
    public static Date parseDate(String dateStr){
        return DateUtils.parseDate(dateStr,DATE_TIME_FORMAT);
    }

    /**
     * date转字符串
     */
    public static String formatDate(Date date,String format){
        if (ObjectUtils.isNull(date)){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * date转字符串
     */
    public static String formatDate(Date date){
        return formatDate(date,DATE_TIME_FORMAT);
    }

}
