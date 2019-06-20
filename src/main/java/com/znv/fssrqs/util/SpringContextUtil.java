package com.znv.fssrqs.util;

import org.springframework.context.ApplicationContext;

/**
 * Created by dongzelong on  2019/6/18 10:18.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class SpringContextUtil {
    private static ApplicationContext ctx;

    public static ApplicationContext getCtx() {
        return ctx;
    }

    public static void setCtx(ApplicationContext ctx) {
        SpringContextUtil.ctx = ctx;
    }
}
