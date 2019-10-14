package com.znv.fssrqs.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dongzelong on  2019/10/13 10:41.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 实现对list集合分页
 */
public class Paging {
    private Integer totalNum;//总条数
    private Integer totalPage;//总页数
    private Integer pageSize;//每页条数
    private Integer pageNum;//当前页码
    private Integer queryIndex;//当前页从第几条开始查

    /**
     * @param totalNum 查询总数
     * @param pageSize 分页大小
     * @param pageNum  页码
     * @return
     */
    public static Paging pagination(Integer totalNum, Integer pageSize, Integer pageNum) {
        Paging page = new Paging();
        page.setTotalNum(totalNum);
        Integer totalPage = totalNum % pageSize == 0 ? totalNum / pageSize :
                totalNum / pageSize + 1;
        page.setTotalPage(totalPage);
        page.setPageNum(pageNum + 1);
        page.setPageSize(pageSize);
        page.setQueryIndex(ParamUtils.getPageOffset(pageNum, pageSize));
        return page;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        Paging paging = Paging.pagination(list.size(), 1, 2);
        int fromIndex = paging.getQueryIndex();
        int toIndex = 0;
        if (fromIndex + paging.getPageSize() >= list.size()) {
            toIndex = list.size();
        } else {
            toIndex = fromIndex + paging.getPageSize();
        }
        if (fromIndex > toIndex) {
            System.out.println(Collections.EMPTY_LIST);
        }
        System.out.println(list.subList(fromIndex, toIndex));
    }
}
