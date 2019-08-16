package com.znv.fssrqs.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

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

    public static void main(String[] args) {
        System.out.println(fromDateTime(fromDateTimeString("2019-08-12 12:12:12")));
    }
}
