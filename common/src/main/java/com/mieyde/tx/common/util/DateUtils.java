package com.mieyde.tx.common.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 日期时间工具
 *
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
    public static Date parseDate(String dateStr){
        if (StringUtls.isBlank(dateStr)){
            return null;
        }
        return new Date(dateStr);
    }

    /**
     * 时间戳转时间
     */
    public static Date parseDate(long timeStamp){
        return new Date(timeStamp);
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

    /**
     * 根据时间获取时间戳
     */
    public static long formatDateToMilli(Date date){
        return date.getTime();
    }

    /**
     * 计算两个时间段的交集,没有交集返回null
     * @param startTime1
     * @param endTime1
     * @param startTime2
     * @param endTime2
     * @return
     */
    public static Map<String,Date> computerOverlapTime(Date startTime1,Date endTime1,Date startTime2,Date endTime2){
        if (endTime1.before(startTime1) || endTime2.before(startTime1)){
            return null;
        }
        if (endTime1.before(startTime2) || endTime2.before(startTime1)){
            return null;
        }
        Date startTime = Collections.max(List.of(startTime1, startTime2));
        Date endTime = Collections.min(List.of(endTime1, endTime2));
        return Map.of("startTime",startTime,"endTime",endTime);
    }

    /**
     * 获取当前时间
     */
    public static LocalDateTime currentLocalDateTime(){
        return LocalDateTime.now();
    }

    /**
     * 字符串转LocalDateTime
     *
     * 如：2023-06-26T09:16:41.513
     */
    public static LocalDateTime parseLocalDateTime(String dateStr){
        if (StringUtls.isBlank(dateStr)){
            return null;
        }
        return LocalDateTime.parse(dateStr);
    }

    /**
     * 时间戳转LocalDateTime
     */
    public static LocalDateTime parseLocalDateTime(long timeStamp){
        Instant instant = Instant.ofEpochMilli(timeStamp);
        ZoneId zoneId = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant,zoneId);
    }

    /**
     * LocalDateTime转字符串
     */
    public static String formatLocalDateTime(LocalDateTime date,String format){
        if (ObjectUtils.isNull(date)){
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * LocalDateTime转字符串
     */
    public static String formatLocalDateTime(LocalDateTime date){
        return formatLocalDateTime(date,DATE_TIME_FORMAT);
    }

    /**
     * 根据时间获取时间戳
     */
    public static long formatLocalDateTimeToMilli(LocalDateTime date){
        return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 计算两个时间段的交集,没有交集返回null
     * @param startTime1
     * @param endTime1
     * @param startTime2
     * @param endTime2
     * @return
     */
    public static Map<String,LocalDateTime> computerOverlapTime(LocalDateTime startTime1, LocalDateTime endTime1, LocalDateTime startTime2, LocalDateTime endTime2){
        if (endTime1.isBefore(startTime1) || endTime2.isBefore(startTime2)){
            return null;
        }
        if (endTime1.isBefore(startTime2) || endTime2.isBefore(startTime1)){
            return null;
        }
        LocalDateTime startTime = Collections.max(List.of(startTime1, startTime2));
        LocalDateTime endTime = Collections.min(List.of(endTime1, endTime2));
        return Map.of("startTime",startTime,"endTime",endTime);
    }

}
