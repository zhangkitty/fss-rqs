package com.znv.fssrqs.util;

public class CountryCodeUtil {

    public static String countryCodeTransToGB1400(String src) {
        if ("中国".equals(src)) {
            return "156";
        } else {
            return "";
        }
    }

    public static String countryCodeTransFromGB1400(String src) {
        if ("156".equals(src)) {
            return "中国";
        } else {
            return "";
        }
    }

    public static String ethnicCodeTransFromGB1400(String src) {
        if ("01".equals(src)) {
            return "汉";
        } else if ("02".equals(src)){
            return "蒙古";
        } else if ("03".equals(src)){
            return "回";
        } else if ("04".equals(src)){
            return "藏";
        } else if ("05".equals(src)){
            return "维吾尔";
        } else if ("06".equals(src)){
            return "苗";
        } else if ("07".equals(src)){
            return "彝";
        } else if ("08".equals(src)){
            return "壮";
        } else if ("09".equals(src)){
            return "布依";
        } else if ("10".equals(src)){
            return "朝鲜";
        } else if ("11".equals(src)){
            return "满";
        }

        return "";
    }

    public static String ethnicCodeTransToGB1400(String src) {
        if ("汉".equals(src)) {
            return "01";
        } else if ("蒙古".equals(src)){
            return "02";
        } else if ("回".equals(src)){
            return "03";
        } else if ("藏".equals(src)){
            return "04";
        } else if ("维吾尔".equals(src)){
            return "05";
        } else if ("苗".equals(src)){
            return "06";
        } else if ("彝".equals(src)){
            return "07";
        } else if ("壮".equals(src)){
            return "08";
        } else if ("布依".equals(src)){
            return "09";
        } else if ("朝鲜".equals(src)){
            return "10";
        } else if ("满".equals(src)){
            return "11";
        }

        return "";
    }
}
