package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.CustomTree;

import java.util.List;

public interface CustomTreeMapper {
    int deleteByPrimaryKey(int treeId);

    int insert(CustomTree record);

    int insertSelective(CustomTree record);

    CustomTree selectByPrimaryKey(String treeId);

    List<CustomTree> selectAll();

    int updateByPrimaryKeySelective(CustomTree record);

    int updateByPrimaryKey(CustomTree record);

    int updateBatch(List<CustomTree> trees);
}