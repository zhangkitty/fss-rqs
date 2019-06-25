package com.znv.fssrqs.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串转数据工具包
 *
 * @author XuKaihua
 */
@Slf4j
public final class DataConvertUtils {
    private static Locale locale = Locale.getDefault();
    private static TimeZone timeZone = TimeZone.getDefault();

    private DataConvertUtils() {

    }

    public static void setLocale(Locale l) {
        locale = l;
    }

    public static Locale getLocale() {
        return locale;
    }

    public static void setTimeZone(TimeZone tz) {
        timeZone = tz;
    }

    public static TimeZone getTimeZone() {
        return timeZone;
    }

    public static Calendar getNow() {
        return Calendar.getInstance(getTimeZone(), getLocale());
    }

    /**
     * ************************************** 整数 ***************************************.
     * @param value the value
     * @return the int
     */
    /**
     * 字符串到整数。如果转换错误，则返回默认值-1。
     *
     * @param value 字符串
     * @return 转换后的整数。
     */
    public static int strToInt(String value) {
        return strToInt(value, -1);
    }

    /**
     * 字符串到整数。如果转换错误，则返回默认值。.
     *
     * @param value    字符串。
     * @param defValue 默认值。
     * @return 返回转换后的整数。
     */
    public static int strToInt(String value, int defValue) {
        if (value == null || value.equals("")) {
            return defValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defValue;
        }
    }

    /**
     * 整数到字符串。.
     *
     * @param value 整数。
     * @return 转换后的字符串。
     */
    public static String intToStr(int value) {
        return Integer.toString(value);
    }

    /**
     * ************************************** 长整数 ***************************************.
     * @param value the value
     * @return the long
     */
    /**
     * 字符串到长整数。如果转换错误，则返回默认值-1L。
     *
     * @param value 字符串。
     * @return 转换后的长整数。
     */
    public static long strToLong(String value) {
        return strToLong(value, -1L);
    }

    /**
     * 字符串到长整数。如果转换错误，则返回默认值。.
     *
     * @param value    字符串。
     * @param defValue 默认值。
     * @return 转换后的长整数。
     */
    public static long strToLong(String value, long defValue) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return defValue;
        }
    }

    /**
     * 整数到字符串.
     *
     * @param value 整数。
     * @return 转换后的字符串。
     */
    public static String longToStr(long value) {
        return Long.toString(value);
    }

    /** ************************************** BOOL值 ***************************************. */

    /**
     * 字符串到BOOL值。如果转换错误，则返回默认值false。
     *
     * @param value 字符串。
     * @return 转换后的BOOL值。
     */
    public static boolean strToBool(String value) {
        return strToBool(value, false);
    }

    /**
     * 字符串到BOOL值。如果转换错误，则返回默认值。.
     *
     * @param value    字符串。
     * @param defValue 默认值。
     * @return 转换后的BOOL值。
     */
    public static boolean strToBool(String value, boolean defValue) {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return defValue;
        }
    }

    /**
     * BOOL值到字符串.
     *
     * @param value BOOL值。
     * @return 转换后的字符串。
     */
    public static String boolToStr(boolean value) {
        return Boolean.toString(value);
    }

    /** ************************************** Float值 ***************************************. */

    /**
     * 字符串到Float值。如果转换错误，则返回默认值false。
     *
     * @param value 字符串。
     * @return 转换后的Float值。
     */
    public static float strToFloat(String value) {
        return strToFloat(value, -1f);
    }

    /**
     * 字符串到Float值。如果转换错误，则返回默认值。.
     *
     * @param value    字符串。
     * @param defValue 默认值。
     * @return 转换后的Float值。
     */
    public static float strToFloat(String value, float defValue) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return defValue;
        }
    }

    /**
     * Float值到字符串.
     *
     * @param value Float值。
     * @return 转换后的字符串。
     */
    public static String floatToStr(float value) {
        return Float.toString(value);
    }

    /** ************************************** Double值 ***************************************. */

    /**
     * 字符串到Double值。如果转换错误，则返回默认值false。
     *
     * @param value 字符串。
     * @return 转换后的Double值。
     */
    public static double strToDouble(String value) {
        return strToDouble(value, -1);
    }

    /**
     * 字符串到Double值。如果转换错误，则返回默认值。.
     *
     * @param value    字符串。
     * @param defValue 默认值。
     * @return 转换后的Double值。
     */
    public static double strToDouble(String value, double defValue) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return defValue;
        }
    }

    /**
     * Double值到字符串.
     *
     * @param value Double值。
     * @return 转换后的字符串。
     */
    public static String doubleToStr(double value) {
        return Double.toString(value);
    }

    /** ************************************** 日期时间 ***************************************. */

    /**
     * The Constant DEFAULT_DATE.
     */
    protected static final Date DEFAULT_DATE = new Date(0);

    /**
     * The Constant DEFAULT_DATE_TIME_FORMAT.
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * The Constant DEFAULT_DATE_FORMAT.
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * The Constant DEFAULT_TIME_FORMAT.
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    /**
     * The Constant DEFAULT_TIME_ZONE.
     */
    public static final int SPAN_BY_MONTH = 3;

    /**
     * The Constant DEFAULT_TIME_ZONE.
     */
    public static final int SPAN_BY_WEEK = 2;

    /**
     * The Constant DEFAULT_TIME_ZONE.
     */
    public static final int SPAN_BY_DAY = 1;

    /**
     * 日期时间到字符串。使用当前日期时间和默认的格式化字符串"yyyy-MM-dd HH:mm:ss"。.
     *
     * @return 转换后的字符串。
     */
    public static String dateToStr() {
        return dateToStr(null, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 日期时间到字符串。使用默认的格式化字符串"yyyy-MM-dd HH:mm:ss"。.
     *
     * @param date 日期时间。
     * @return 转换后的字符串。
     */
    public static String dateToStr(Date date) {
        return dateToStr(date, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 日期时间到字符串。使用当前日期时间。.
     *
     * @param format 格式化字符串。
     * @return 转换后的字符串。
     */
    public static String dateToStr(String format) {
        return dateToStr(null, format);
    }

    /**
     * 日期时间到字符串。.
     *
     * @param date   日期时间。
     * @param format 格式化字符串。
     * @return 转换后的字符串。
     */
    public static String dateToStr(Date date, String format) {
        if (date == null) {
            date = getNow().getTime();
        }
        if (format == null) {
            format = DEFAULT_DATE_TIME_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(getTimeZone());
        return sdf.format(date);
    }

    /**
     * 字符串到日期时间。使用默认的格式化字符串"yyyy-MM-dd HH:mm:ss"。如果转换失败，则返回当前日期时间的字符串。.
     *
     * @param strDate 日期时间字符串。
     * @return 转换后的日期时间。
     */
    public static Date strToDate(String strDate) {
        return strToDate(strDate, DEFAULT_DATE_TIME_FORMAT, null);
    }

    /**
     * 字符串到日期时间。如果失败，则返回当前日期时间的字符串。.
     *
     * @param strDate 日期时间字符串。
     * @param format  格式化字符串。
     * @return 转换后的日期时间。
     */
    public static Date strToDate(String strDate, String format) {
        return strToDate(strDate, format, null);
    }

    /**
     * 字符串到日期时间。如果转换失败，则返回默认值的日期时间。.
     *
     * @param strDate 日期时间字符串。
     * @param format  格式化字符串。
     * @param defDate 失败之后的默认日期时间。
     * @return 转换后的日期时间字符串。
     */
    public static Date strToDate(String strDate, String format, String regex, Date defDate) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat();

        Matcher m = Pattern.compile(regex).matcher(strDate);
        boolean isMatches = m.matches();
        if (isMatches && m.groupCount() == 2) {
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+" + m.group(2)));
            sdf.applyPattern(format);
            try {
                date = sdf.parse(m.group(1));
            } catch (ParseException e) {
                log.error("", e);
            }
        }
        if (date == null) {
            date = defDate;
        }

        return date;
    }

    /**
     * 字符串到日期时间。如果转换失败，则返回默认值的日期时间。.
     *
     * @param strDate 日期时间字符串。
     * @param format  格式化字符串。
     * @param defDate 失败之后的默认日期时间。
     * @return 转换后的日期时间字符串。
     */
    public static Date strToDate(String strDate, String format, Date defDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(getTimeZone());
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            if (defDate == null) {
                defDate = DEFAULT_DATE;
            }
            return defDate;
        }
    }

    /**
     * 字符串到日期时间。使用默认的格式化字符串"yyyy-MM-dd HH:mm:ss"。如果转换失败，则返回当前日期时间的字符串。.
     *
     * @param strDate 日期时间字符串。
     * @return 转换后的日期时间。
     */
    public static Calendar strToCalendar(String strDate) {
        return strToCalendar(strDate, DEFAULT_DATE_TIME_FORMAT, null);
    }

    /**
     * 字符串到日期时间。如果失败，则返回当前日期时间的字符串。.
     *
     * @param strDate 日期时间字符串。
     * @param format  格式化字符串。
     * @return 转换后的日期时间。
     */
    public static Calendar strToCalendar(String strDate, String format) {
        return strToCalendar(strDate, format, null);
    }

    /**
     * 字符串到日期时间。如果转换失败，则返回默认值的日期时间。.
     *
     * @param strDate 日期时间字符串。
     * @param format  格式化字符串。
     * @param defDate 失败之后的默认日期时间。
     * @return 转换后的日期时间字符串。
     */
    public static Calendar strToCalendar(String strDate, String format, Date defDate) {
        Date d = strToDate(strDate, format, defDate);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c;
    }

    /**
     * 将标准格式的日期时间字符串转换成指定格式的日期时间字符串。如果转换失败，则返回当前日期时间的字符串。.
     *
     * @param strDate   标准格式的日期时间字符串。
     * @param newFormat 新格式化字符串。
     * @return 转换后的日期时间。
     */
    public static String formatDate(String strDate, String newFormat) {
        Date date = strToDate(strDate);
        return dateToStr(date, newFormat);
    }

    /**
     * 将旧格式的日期时间字符串转换成新格式的日期时间字符串。如果转换失败，则返回当前日期时间的字符串。.
     *
     * @param strDate   日期时间字符串。
     * @param oldFormat 旧格式化字符串。
     * @param newFormat 新格式化字符串。
     * @return 转换后的日期时间。
     */
    public static String formatDate(String strDate, String oldFormat, String newFormat) {
        Date date = strToDate(strDate, oldFormat);
        return dateToStr(date, newFormat);
    }

    public static String formatDate(String strDate, String oldFormat, String oldRegex, String newFormat) {
        Date date = strToDate(strDate, oldFormat, oldRegex, new Date());
        SimpleDateFormat sdf = new SimpleDateFormat(newFormat);
        sdf.setTimeZone(getTimeZone());
        return sdf.format(date);
    }

    /**
     * 时间返回格式 YYYYMMDDHHmmssSSS
     *
     * @param time
     * @return
     */
    public static String parseDateStr2DateStr(String time) {
        String formatTime = "";
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        // 转换成东八区时区
        sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date enterTime1 = sdf1.parse(time);
            formatTime = sdf2.format(enterTime1);
        } catch (ParseException e) {
            log.error("time format error.", e);
        }
        return formatTime;
    }

    /**
     * 解析时间字符串为秒数
     *
     * @param strTime 时间字符串
     * @return 秒数
     */
    public static int getRealSecond(String strTime) {
        int second = -1;

        if (strTime == null || strTime.equals("")) {
            return second;
        }

        char c = strTime.charAt(strTime.length() - 1);
        String time = strTime.substring(0, strTime.length() - 1);
        switch (c) {
            case 'S': // 秒
            case 's':
                second = strToInt(time);
                break;
            case 'M': // 分
            case 'm':
                second = strToInt(time) * 60;
                break;
            case 'H': // 小时
            case 'h':
                second = strToInt(time) * 60 * 60;
                break;
            case 'D': // 天
            case 'd':
                second = strToInt(time) * 60 * 60 * 24;
                break;
            case '0': // 数字为秒
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                second = strToInt(strTime);
                break;
            default: // 其他字符为秒
                second = strToInt(time);
                break;
        }

        return second;
    }

    /** ************************************** 日期段处理 ***************************************. */

    /**
     * 将时间段重新按时间分段类型进行分段。使用默认的格式化字符串"yyyy-MM-dd HH:mm:ss"。.
     *
     * @param type      时间分段类型
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @return 多个时间段
     */
    public static String[][] strToSections(int type, String beginDate, String endDate) {
        return strToSections(type, beginDate, endDate, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 将时间段重新按时间分段类型进行分段.
     *
     * @param type      时间分段类型
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @param format    时间格式化字符串
     * @return 多个时间段
     */
    public static String[][] strToSections(int type, String beginDate, String endDate, String format) {
        String[][] ret = null;

        Calendar cBegin = strToCalendar(beginDate, format);
        Calendar cEnd = strToCalendar(endDate, format);

        // 判断类型转换是否成功
        if (cBegin.getTimeInMillis() <= cEnd.getTimeInMillis() && cBegin.getTime().compareTo(DEFAULT_DATE) != 0
                && cEnd.getTime().compareTo(DEFAULT_DATE) != 0) {
            ret = getSections(type, cBegin, cEnd);
        }

        // 如果没有获取到时间段，则使用传入的时间段
        if (ret == null) {
            ret = new String[1][2];
            ret[0][0] = beginDate;
            ret[0][1] = endDate;
        }
        return ret;
    }

    /**
     * 获取时间段所跨越的数量.
     *
     * @param type   时间分段类型
     * @param cBegin 开始时间
     * @param cEnd   结束时间
     * @return the time span
     */
    public static int getTimeSpan(int type, Calendar cBegin, Calendar cEnd) {
        Calendar beginTime = null, endTime = null;
        switch (type) {
            case SPAN_BY_WEEK:
                beginTime = Calendar.getInstance();
                beginTime.setTime(cBegin.getTime());
                beginTime.set(Calendar.DAY_OF_WEEK, beginTime.getActualMinimum(Calendar.DAY_OF_WEEK));
                beginTime.set(Calendar.HOUR_OF_DAY, 0);
                beginTime.set(Calendar.MINUTE, 0);
                beginTime.set(Calendar.SECOND, 0);

                endTime = Calendar.getInstance();
                endTime.setTime(cEnd.getTime());
                endTime.set(Calendar.DAY_OF_WEEK, endTime.getActualMaximum(Calendar.DAY_OF_WEEK));
                endTime.set(Calendar.HOUR_OF_DAY, 23);
                endTime.set(Calendar.MINUTE, 59);
                endTime.set(Calendar.SECOND, 59);

                return (int) Math
                        .abs((endTime.getTimeInMillis() / 86400000 - beginTime.getTimeInMillis() / 86400000) / 7);
            case SPAN_BY_DAY:
                beginTime = Calendar.getInstance();
                beginTime.setTime(cBegin.getTime());
                beginTime.set(Calendar.HOUR_OF_DAY, 0);
                beginTime.set(Calendar.MINUTE, 0);
                beginTime.set(Calendar.SECOND, 0);

                endTime = Calendar.getInstance();
                endTime.setTime(cEnd.getTime());
                endTime.set(Calendar.HOUR_OF_DAY, 23);
                endTime.set(Calendar.MINUTE, 59);
                endTime.set(Calendar.SECOND, 59);

                return (int) Math.abs(endTime.getTimeInMillis() / 86400000 - beginTime.getTimeInMillis() / 86400000);
            case SPAN_BY_MONTH: // 跨月数量
            default:
                return Math.abs((cEnd.get(Calendar.YEAR) * 12 + cEnd.get(Calendar.MONTH))
                        - (cBegin.get(Calendar.YEAR) * 12 + cBegin.get(Calendar.MONTH))) + 1;
        }
    }

    /**
     * 将时间段重新按月进行分段.
     *
     * @param type   the type
     * @param cBegin 开始时间
     * @param cEnd   结束时间
     * @return 多个时间段
     */
    public static String[][] getSections(int type, Calendar cBegin, Calendar cEnd) {
        // 获取所跨的数量
        int timeSspan = getTimeSpan(type, cBegin, cEnd);

        // 返回的数组
        String[][] ret = new String[timeSspan][2];
        ret[0][0] = dateToStr(cBegin.getTime()); // 第一组的开始时间使用传入的开始时间
        ret[timeSspan - 1][1] = dateToStr(cEnd.getTime()); // 最后一组结束时间使用传入的结束时间

        if (timeSspan > 1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(cBegin.getTime());

            for (int i = 0; i < timeSspan; i++) {
                if (i != 0) {
                    // 除第一时间段外，其他的时间段都要加一
                    switch (type) {
                        case SPAN_BY_WEEK:
                            calendar.add(Calendar.WEEK_OF_YEAR, 1);
                            calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
                            break;
                        case SPAN_BY_DAY:
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                            break;
                        case SPAN_BY_MONTH:
                        default:
                            calendar.add(Calendar.MONTH, 1);
                            calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                            break;
                    }
                    // 除第一时间段外，其他的开始时间都设置为1日00:00:00
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    ret[i][0] = dateToStr(calendar.getTime());
                }
                if (i != timeSspan - 1) {
                    switch (type) {
                        case SPAN_BY_WEEK:
                            calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
                            break;
                        case SPAN_BY_DAY:
                            break;
                        case SPAN_BY_MONTH:
                        default:
                            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                            break;
                    }
                    // 除最后一个时间段外，其他的结束时间都设置为31日23:59:59
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    ret[i][1] = dateToStr(calendar.getTime());
                }
            }
        }

        return ret;
    }

    /** ************************************** 数据库日期时间 ***************************************. */

    /**
     * 字符串到数据库日期
     *
     * @param strDate 字符串
     * @return 数据库日期
     */
    public static java.sql.Date strToSqlDate(String strDate) {
        return new java.sql.Date(strToDate(strDate).getTime());
    }

    /**
     * 字符串到数据库日期.
     *
     * @param strDate 字符串
     * @param format  格式化参数
     * @return 数据库日期
     */
    public static java.sql.Date strToSqlDate(String strDate, String format) {
        return new java.sql.Date(strToDate(strDate, format).getTime());
    }

    /**
     * 字符串到数据库日期.
     *
     * @param strDate 字符串
     * @param format  格式化参数
     * @param defDate 失败之后的默认日期时间。
     * @return 数据库日期
     */
    public static java.sql.Date strToSqlDate(String strDate, String format, java.sql.Date defDate) {
        return new java.sql.Date(strToDate(strDate, format, defDate).getTime());
    }

    public static void main(String[] args) {
        String date = DataConvertUtils.formatDate("20140606064937.0Z", "yyyyMMddHHmmss", "(\\d*)\\.(\\d)Z",
                "yyyy-MM-dd HH:mm:ss");
        System.out.println(date);
    }

    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8) | ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    // 获得当天0点时间
    public static Date getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    // 获得当天24点时间
    public static Date getTimesnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    // 获得本周一0点时间
    public static Date getTimesWeekmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    // 获得本周日24点时间
    public static Date getTimesWeeknight() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesWeekmorning());
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }

    // 获得本月第一天0点时间
    public static Date getTimesMonthmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    // 获得本月最后一天24点时间
    public static Date getTimesMonthnight() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0  
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0  
        calendar.set(Calendar.MINUTE, 0);
        //将秒至0  
        calendar.set(Calendar.SECOND, 0);
        //将毫秒至0  
        calendar.set(Calendar.MILLISECOND, 0);
        //将当前月加1；  
        calendar.add(Calendar.MONTH, 1);
        //在当前月的下一月基础上减去1毫秒  
        calendar.add(Calendar.MILLISECOND, -1);
        //获得当前月最后一天  
        return calendar.getTime();
    }


}
