package com.znv.fssrqs.elasticsearch.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface ISearch {

    /**
     * 多条件and或者or关系，查找多个,最多查找max条
     *
     * @param map
     * @param index
     * @param type
     * @param max(最多查找条数）
     * @param and(and关系:true,or关系:false)
     * @return
     * @throws IOException
     */
    List<Map<String, Object>> searchList(Map<String, String> map, String index, String type, int max, Boolean and) throws IOException;

    /**
     * 多条件and或者or关系,分页查找
     *
     * @param map
     * @param index
     * @param type
     * @param pageSize
     * @param pageNum
     * @param and(and关系:true,or关系:false)
     * @return
     * @throws IOException
     */
    Map<String, Object> searchListByPage(Map<String, String> map, String index, String type, int pageSize, int pageNum, Boolean and) throws IOException;
}