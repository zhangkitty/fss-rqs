package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.CustomTreeNode;

import java.util.List;

public interface CustomTreeNodeMapper {
    int deleteByPrimaryKey(String nodeId);

    int deleteByTreeId(int treeId);

    int insert(CustomTreeNode record);

    int insertSelective(CustomTreeNode record);

    CustomTreeNode selectByPrimaryKey(String nodeId);

    List<CustomTreeNode> selectByTreeId(int treeId);

    int updateByPrimaryKeySelective(CustomTreeNode record);

    int updateByPrimaryKey(CustomTreeNode record);

    int queryId(int treeId);

    int insertBatch(List<CustomTreeNode> record);

    int deleteBatch(List<String> ids);

    int updateBatch(List<CustomTreeNode> nodes);
}