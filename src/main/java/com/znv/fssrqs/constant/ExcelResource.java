package com.znv.fssrqs.constant;

import com.znv.fssrqs.exception.ZnvException;

/**
 * Created by dongzelong on  2019/8/14 11:00.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public enum ExcelResource {
    ALARM(1, "战果数据报表", "spoil_data_report");
    int type;
    String chineseName;
    String englishName;

    ExcelResource(int type, String chineseName, String englishName) {
        this.type = type;
        this.chineseName = chineseName;
        this.englishName = englishName;
    }

    public static boolean contains(int type) {
        for (ExcelResource excelResource : values()) {
            if (excelResource.type == type) {
                return true;
            }
        }
        return false;
    }

    public static String valueOf(int type, String locale) {
        for (ExcelResource excelResource : values()) {
            if (excelResource.type == type) {
                if (locale.equals("zh_CN")) {
                    return excelResource.chineseName;
                } else {
                    return excelResource.englishName;
                }

            }
        }
        throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "ExcelResourceNotExist");
    }
}
