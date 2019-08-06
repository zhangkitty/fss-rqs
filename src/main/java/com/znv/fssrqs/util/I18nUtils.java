package com.znv.fssrqs.util;

import com.znv.fssrqs.support.I18nHelper;

import java.util.Locale;
import java.util.Objects;

/**
 * Created by dongzelong on  2019/8/6 10:05.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class I18nUtils {
    private static final I18nHelper[] HOLDER = new I18nHelper[1];

    public static void setHolder(I18nHelper helper) {
        if (Objects.isNull(HOLDER[0])) {
            synchronized (I18nUtils.class) {
                if (Objects.isNull(HOLDER[0])) {
                    HOLDER[0] = helper;
                }
            }
        }
    }

    public static String i18n(Locale locale, String errCode, Object... args) {
        return HOLDER[0].getMessage(errCode, locale, args);
    }
}
