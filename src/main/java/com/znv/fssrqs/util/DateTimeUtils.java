package com.znv.fssrqs.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongzelong on  2019/8/16 9:48.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class DateTimeUtils {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentDateString() {
        return LocalDate.now().format(formatter);
    }

    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public static LocalDate dateOf(int year, int month, int dayOfMonth) {
        return LocalDate.of(year, month, dayOfMonth);
    }

    public static boolean isEquals(LocalDate localDate, LocalDate otherDate) {
        return localDate.equals(otherDate);
    }

    public static boolean isSpecialDay(LocalDate currentDate, LocalDate otherDate) {
        MonthDay currentMonthDay = MonthDay.from(currentDate);
        final MonthDay monthDay = MonthDay.of(otherDate.getMonth(), otherDate.getDayOfMonth());
        if (currentMonthDay.equals(monthDay)) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前日期的时间戳
     *
     * @return
     */
    public static Instant getInstant() {
        final Instant now = Instant.now();
        return now;
    }

    public static Date fromInstant(Instant instant) {
        return Date.from(instant);
    }

    /**
     * @param localDate  日期
     * @param time
     * @param chronoUnit 单位
     */
    public static void plusTime(LocalDate localDate, long time, ChronoUnit chronoUnit) {
        if (chronoUnit == ChronoUnit.YEARS) {
            localDate.plusYears(time);
        }

        if (chronoUnit == ChronoUnit.MONTHS) {
            localDate.plusMonths(time);
        }

        if (chronoUnit == ChronoUnit.DAYS) {
            localDate.plusDays(time);
        }

        if (chronoUnit == ChronoUnit.WEEKS) {
            localDate.plus(time, ChronoUnit.WEEKS);
        }

        if (chronoUnit == ChronoUnit.YEARS) {
            localDate.plus(time, ChronoUnit.YEARS);
        }

        if (chronoUnit == ChronoUnit.MINUTES) {
            localDate.plus(time, ChronoUnit.MINUTES);
        }

        if (chronoUnit == ChronoUnit.SECONDS) {
            localDate.plus(time, ChronoUnit.SECONDS);
        }
    }

    /**
     * 两个日期之间差值
     *
     * @param localDate
     * @param otherDate
     * @return
     */
    public static Period diff(LocalDate localDate, LocalDate otherDate) {
        return Period.between(localDate, otherDate);
    }

    public static LocalDate fromDateString(String dateStr) {
        return LocalDate.parse(dateStr, formatter);
    }

    /**
     * @param localDate
     */
    public static String fromDate(LocalDate localDate) {
        return formatter.format(localDate);
    }

    public static String fromDateTime(LocalDateTime localDateTime) {
        return DEFAULT_FORMATTER.format(localDateTime);
    }

    public static LocalDateTime fromDateTimeString(String dateStr) {
        return LocalDateTime.parse(dateStr, DEFAULT_FORMATTER);
    }

    public static Clock getSystemUTC() {
        return Clock.systemUTC();
    }

    //使用ThreadLocal来限制SimpleDateFromat只能在线程内共享,避免了多线程导致的线程安全问题
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public static String format(Date date) {
        return threadLocal.get().format(date);
    }


    /**
     * 锁对象
     */
    private static final Object lockObj = new Object();

    /**
     * 存放不同的日期模板格式的sdf的Map
     */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();


    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     *
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }

    /**
     * 使用ThreadLocal<SimpleDateFormat>来获取SimpleDateFormat,这样每个线程只会有一个SimpleDateFormat
     * 如果新的线程中没有SimpleDateFormat，才会new一个
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    public static Date parse(String dateStr, String pattern) throws ParseException {
        return getSdf(pattern).parse(dateStr);
    }
}
