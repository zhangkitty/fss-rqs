package com.znv.fssrqs.entity.mysql;

public class CustomTreeNode {
    private String nodeId;

    private String nodeName;

    private Integer nodeKind;

    private String upNodeId;

    private int treeId;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId == null ? null : nodeId.trim();
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName == null ? null : nodeName.trim();
    }

    public Integer getNodeKind() {
        return nodeKind;
    }

    public void setNodeKind(Integer nodeKind) {
        this.nodeKind = nodeKind;
    }

    public String getUpNodeId() {
        return upNodeId;
    }

    public void setUpNodeId(String upNodeId) {
        this.upNodeId = upNodeId == null ? null : upNodeId.trim();
    }

    public int getTreeId() {
        return treeId;
    }

    public void setTreeId(int treeId) {
        this.treeId = treeId;
    }
}