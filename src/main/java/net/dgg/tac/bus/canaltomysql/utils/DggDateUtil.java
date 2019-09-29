package net.dgg.tac.bus.canaltomysql.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/04/18
 * @Description: 日期工具类(java8 新的日期类【默认时区为东8区】)
 */
public class DggDateUtil {

    /**
     * 默认date格式
     */
    private static final String DEFAULT_DATE_FORMATTER = "yyyy-MM-dd";
    /**
     * 默认datetime格式
     */
    private static final String DEFAULT_DATETIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    /**
     * 默认秒格式
     */
    private static final String DEFAULT_TIMESTAMP_SECOND = "yyyyMMddHHmmss";
    /**
     * 默认毫秒格式
     */
    private static final String DEFAULT_TIMESTAMP_MILLISECOND = "yyyyMMddHHmmssSSS";

    /**
     * @Todo: 获取当前系统时间
     * @Date: 2019/04/18
     * @param: []
     * @return: java.util.Date
     */
    public static Date getCurrentDate(){
        return new Date();
    }
    
    /**
     * @Todo: 返回当前日期的时间戳---秒
     * @Date: 2019/04/18
     * @param: []
     * @return: java.lang.String
     */
    public static String getSecondTimeStamp(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_TIMESTAMP_SECOND);
        return LocalDateTime.now().format(formatter);
    }

    /**
     * @Todo: 返回当前日期的时间戳---毫秒
     * @Date: 2019/04/18
     * @param: []
     * @return: java.lang.String
     */
    public static String getMilliSecondTimeStamp(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_TIMESTAMP_MILLISECOND);
        return LocalDateTime.now().format(formatter);
    }


    /**
     * @Todo: 返回当前日期 格式为: yyyy-MM-dd
     * @Date: 2019/04/18
     * @param: []
     * @return: java.lang.String
     */
    public static String getCurrentDateString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMATTER);
        return LocalDateTime.now().format(formatter);
    }

    /**
     * @Todo: 返回当前日期 格式为: yyyy-MM-dd HH:mm:ss
     * @Date: 2019/04/18
     * @param: []
     * @return: java.lang.String
     */
    public static String getCurrentDateTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMATTER);
        return LocalDateTime.now().format(formatter);
    }

    /**
     * @Todo: 返回指定日期 格式为: yyyy-MM-dd
     * @Date: 2019/04/18
     * @param: [date]
     * @return: java.lang.String
     */
    public static String getFixedDate(Date date){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMATTER);
        LocalDateTime localDateTime = dateConvertToLocalDateTime(date);
        return dateTimeFormatter.format(localDateTime);
    }

    /**
     * @Todo: 返回指定日期 格式为: yyyy-MM-dd HH:mm:ss
     * @Date: 2019/04/18
     * @param: [date]
     * @return: java.lang.String
     */
    public static String getFixedDateTime(Date date){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMATTER);
        LocalDateTime localDateTime = dateConvertToLocalDateTime(date);
        return dateTimeFormatter.format(localDateTime);
    }

    /**
     * @Todo: 获取---秒
     * @Date: 2019/04/18
     * @param: []
     * @return: java.lang.Long
     */
    public static Long getSecond(){
        return LocalDateTime.now().toEpochSecond(OffsetDateTime.now().getOffset());
    }

    /**
     * @Todo: 获取---毫秒
     * @Date: 2019/04/18
     * @param: []
     * @return: java.lang.Long
     */
    public static Long getMilliSecond(){
        return LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
    }

    //将java.util.Date 转换为java8 的java.time.LocalDateTime,默认时区为东8区
    /**
     * @Todo: java.util.Date 转换为 LocalDateTime
     * @Date: 2019/04/18
     * @param: [localDateTime]
     * @return: java.util.Date
     */
    public static LocalDateTime dateConvertToLocalDateTime(Date date) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * @Todo: LocalDateTime 转换为 java.util.Date
     * @Date: 2019/04/18
     * @param: [localDateTime]
     * @return: java.util.Date
     */
    public static Date localDateTimeConvertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(OffsetDateTime.now().getOffset()));
    }

    /**
     * @TODO 根据date 获取LocalDateTime
     * @param date
     * @return
     */
    public static LocalDateTime getLocalDateTime(Date date){
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, zone);
    }
    /**
     * @TODO 获取当前字符串时间
     * @param formatter
     * @return
     */
    public static String getCurrentDateString(String formatter){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatter);
        LocalDateTime localDateTime = LocalDateTime.now();
        return dateTimeFormatter.format(localDateTime);
    }

    /**
     * @TODO 字符串日期转date日期
     * @param date
     * @param formatter
     * @return
     */
    public static Date stringConvertDate(String date, String formatter){
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(formatter));
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * @TODO 字符串日期转dateTime日期
     * @param dateTime
     * @param formatter
     * @return
     */
    public static Date stringConvertDateTime(String dateTime, String formatter){
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(formatter));
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * @TODO date或dateTime日期格式化为字符串日期
     * @param date
     * @param formatter
     * @return
     */
    public static String dateFormatString(Date date, String formatter){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatter);
        LocalDateTime localDateTime = getLocalDateTime(date);
        return dateTimeFormatter.format(localDateTime);
    }

    /**
     * @TODO 将原始字符串日期(仅限年月日)格式成为新的字符串日期
     * @param date
     * @param originalFormatter 原始格式类型
     * @param newlyFormatter 格式化类型
     * @return
     */
    public static String stringFormatDateString(String date, String originalFormatter, String newlyFormatter){
        DateTimeFormatter originalDateTimeFormatter = DateTimeFormatter.ofPattern(originalFormatter);
        DateTimeFormatter newlyDateTimeFormatter = DateTimeFormatter.ofPattern(newlyFormatter);
        LocalDate localDateTime = LocalDate.parse(date, originalDateTimeFormatter);
        return localDateTime.format(newlyDateTimeFormatter);
    }

    /**
     * @TODO 将原始字符串日期(包含时间)格式成为新的字符串日期
     * @param date
     * @param originalFormatter 原始格式类型
     * @param newlyFormatter 格式化类型
     * @return
     */
    public static String stringFormatDateTimeString(String date, String originalFormatter, String newlyFormatter){
        DateTimeFormatter originalDateTimeFormatter = DateTimeFormatter.ofPattern(originalFormatter);
        DateTimeFormatter newlyDateTimeFormatter = DateTimeFormatter.ofPattern(newlyFormatter);
        LocalDateTime localDateTime = LocalDateTime.parse(date, originalDateTimeFormatter);
        return localDateTime.format(newlyDateTimeFormatter);
    }

    /**
     * @TODO 将日期减少相应(年、月、日、时、分、秒)的count数
     * @param date
     * @param count
     * @param chronoUnit
     * @return
     */
    public static Date dateMinus(Date date, long count, ChronoUnit chronoUnit){
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = getLocalDateTime(date);
        switch (chronoUnit){
            case YEARS:
                return Date.from(localDateTime.minusYears(count).atZone(zone).toInstant());
            case MONTHS:
                return Date.from(localDateTime.minusMonths(count).atZone(zone).toInstant());
            case DAYS:
                return Date.from(localDateTime.minusDays(count).atZone(zone).toInstant());
            case HOURS:
                return Date.from(localDateTime.minusHours(count).atZone(zone).toInstant());
            case MINUTES:
                return Date.from(localDateTime.minusMinutes(count).atZone(zone).toInstant());
            case SECONDS:
                return Date.from(localDateTime.minusSeconds(count).atZone(zone).toInstant());
            default:
                return date;
        }
    }

    /**
     * @TODO 将日期增加相应(年、月、日、时、分、秒)的count数
     * @param date
     * @param count
     * @param chronoUnit
     * @return
     */
    public static Date datePlus(Date date, long count, ChronoUnit chronoUnit){
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = getLocalDateTime(date);
        switch (chronoUnit){
            case YEARS:
                return Date.from(localDateTime.plusYears(count).atZone(zone).toInstant());
            case MONTHS:
                return Date.from(localDateTime.plusMonths(count).atZone(zone).toInstant());
            case DAYS:
                return Date.from(localDateTime.plusDays(count).atZone(zone).toInstant());
            case HOURS:
                return Date.from(localDateTime.plusHours(count).atZone(zone).toInstant());
            case MINUTES:
                return Date.from(localDateTime.plusMinutes(count).atZone(zone).toInstant());
            case SECONDS:
                return Date.from(localDateTime.plusSeconds(count).atZone(zone).toInstant());
            default:
                return date;
        }
    }

    /**
     * @TODO 计算两个日期相差数(日、时、分、秒等也可以使用Duration.between())
     * @param smallDate
     * @param bigDate
     * @param chronoUnit
     * @return
     */
    public static long dateDiffer(Date smallDate, Date bigDate, ChronoUnit chronoUnit){
        LocalDateTime smallLocalDateTime = getLocalDateTime(smallDate);
        LocalDateTime bigLocalDateTime = getLocalDateTime(bigDate);
        switch (chronoUnit){
            case YEARS:
                return smallLocalDateTime.until(bigLocalDateTime, ChronoUnit.YEARS);
            case MONTHS:
                return smallLocalDateTime.until(bigLocalDateTime, ChronoUnit.MONTHS);
            case DAYS:
                return smallLocalDateTime.until(bigLocalDateTime, ChronoUnit.DAYS);
            case HOURS:
                return smallLocalDateTime.until(bigLocalDateTime, ChronoUnit.HOURS);
            case MINUTES:
                return smallLocalDateTime.until(bigLocalDateTime, ChronoUnit.MINUTES);
            case SECONDS:
                return smallLocalDateTime.until(bigLocalDateTime, ChronoUnit.SECONDS);
            default:
                return 0L;
        }
    }

    /**
     * @TODO 根据传入的日期返回星期(传入的标识为String则返回中文表示形式,传入为Integer则返回数值表示形式)
     * @param date
     * @param flag
     * @param <T>
     * @return
     */
    public static <T> T dateOfWeek(Date date, T flag){
        LocalDateTime localDateTime = getLocalDateTime(date);
        if(flag instanceof String){
            switch (localDateTime.getDayOfWeek()){
                case MONDAY:
                    return (T)"星期一";
                case TUESDAY:
                    return (T)"星期二";
                case WEDNESDAY:
                    return (T)"星期三";
                case THURSDAY:
                    return (T)"星期四";
                case FRIDAY:
                    return (T)"星期五";
                case SATURDAY:
                    return (T)"星期六";
                case SUNDAY:
                    return (T)"星期日";
                default:
                    return (T)"星期八";
            }

        }else if(flag instanceof Integer){
            return (T)Integer.valueOf(localDateTime.getDayOfWeek().getValue());
        }
        return null;
    }

    public static void main(String[] args) {
//        System.out.println(OffsetDateTime.now().getOffset());

        String currentDay = getCurrentDateString("yyyyMM");
        System.out.println(currentDay);
    }

}
