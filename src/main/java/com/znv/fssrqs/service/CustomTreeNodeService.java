package com.znv.fssrqs.service;

import com.znv.fssrqs.dao.mysql.CustomTreeNodeMapper;
import com.znv.fssrqs.entity.mysql.CustomTreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by dongzelong on  2019/8/1 11:41.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class CustomTreeNodeService {
    @Autowired
    private CustomTreeNodeMapper customTreeNodeMapper;

    public void batchInsert(List<CustomTreeNode> record) {
        for (int i = 0; i < record.size(); i++) {
            CustomTreeNode node = record.get(i);
            if (node.getNodeKind() != 4) {
                int treeId = node.getTreeId();
                int numberId = customTreeNodeMapper.queryId(treeId);
                String nodeId = String.format("%s%s", String.valueOf(treeId), String.valueOf(numberId));
                node.setNodeId(nodeId);
            }
        }
        customTreeNodeMapper.insertBatch(record);
    }

    public String insertParentNode(CustomTreeNode pnode) {
        int treeId = pnode.getTreeId();
        int numberId = customTreeNodeMapper.queryId(treeId);
        String nodeId = String.format("%s%s", String.valueOf(treeId), String.valueOf(numberId));
        pnode.setNodeId(nodeId);
        customTreeNodeMapper.insertSelective(pnode);

        return nodeId;
    }

    public List<CustomTreeNode> selectTreeNodeByTreeId(int treeId) {
        return customTreeNodeMapper.selectByTreeId(treeId);
    }

    public int deleteBatch(List<String> ids) {
        return customTreeNodeMapper.deleteBatch(ids);
    }

    public int updateBatch(List<CustomTreeNode> nodes) {
        return customTreeNodeMapper.updateBatch(nodes);
    }

    public int deleteByTreeId(int treeId) {
        return customTreeNodeMapper.deleteByTreeId(treeId);
    }
}
