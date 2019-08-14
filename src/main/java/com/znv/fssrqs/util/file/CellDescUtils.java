package com.znv.fssrqs.util.file;

/**
 * Created by dongzelong on 2017/7/7 16:44.
 *
 * @author dongzelong
 * @version 1.0
 */
public class CellDescUtils {
    public static CellDesc make(String title, int idx, ICellGetter getter, boolean isSpecial) {
        return new CellDesc(title, idx, getter, isSpecial);
    }

    public static CellDesc make(String title, int idx, String column, boolean isSpecial) {
        return make(title, idx, new StringCellGetter(column), isSpecial);
    }
}
