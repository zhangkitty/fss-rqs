package com.znv.fssrqs.util;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dongzelong on 2019/06/26.
 *
 * @author dongzelong
 */
public class ParamUtils {
    /**
     * 计算起始偏移量
     *
     * @param currentPage 当前页面
     * @param pageSize    页面大小
     * @return
     */
    public static Integer getPageOffset(Integer currentPage, Integer pageSize) {
        if (currentPage != null && pageSize != null) {
            int offset = (currentPage - 1) * pageSize;
            if (offset < 0) {
                return 0;
            } else {
                return offset;
            }
        }
        return 0;
    }

    public static List<String> getSearchList(String search) {
        List<String> searchList = new ArrayList<>();
        if (search != null && !StringUtils.isEmpty(search.trim())) {
            String[] split = search.split("\\s+");
            searchList.addAll(Arrays.asList(split));
        }
        return searchList;
    }
}
