package com.znv.fssrqs.util.file;

import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;
import java.util.Locale;

/**
 * Created by dongzelong on 2019/8/14 11:15.
 *
 * @author dongzelong
 * @version 1.0
 */
public class StringCellGetter implements ICellGetter {
    private final String column;

    public StringCellGetter(String column) {
        this.column = column;
    }

    @Override
    public String get(JSONObject jsonObject, Locale locale) {
        if (jsonObject.get(column) instanceof Integer) {
            return String.valueOf(jsonObject.getInteger(column));
        } else if (jsonObject.get(column) instanceof Timestamp) {
            return String.valueOf(jsonObject.getTimestamp(column));
        }
        return jsonObject.getString(column);
    }

    public String getColumn() {
        return column;
    }
}
