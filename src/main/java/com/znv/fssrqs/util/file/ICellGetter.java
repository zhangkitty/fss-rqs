package com.znv.fssrqs.util.file;

import com.alibaba.fastjson.JSONObject;

import java.util.Locale;

/**
 * Created by dongzelong on 2017/7/7 16:43.
 *
 * @author dongzelong
 * @version 1.0
 */
public interface ICellGetter {
    String get(JSONObject jsonObject, Locale locale);
}
