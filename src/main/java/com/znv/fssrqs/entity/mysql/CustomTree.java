package com.znv.fssrqs.entity.mysql;

public class CustomTree {
    private int treeId;

    private String treeName;

    private String treeDesc;

    private Integer defaultTree;

    private Integer systemTree;

    public Integer getTreeId() {
        return treeId;
    }

    public void setTreeId(Integer treeId) {
        this.treeId = treeId;
    }

    public String getTreeName() {
        return treeName;
    }

    public void setTreeName(String treeName) {
        this.treeName = treeName == null ? null : treeName.trim();
    }

    public String getTreeDesc() {
        return treeDesc;
    }

    public void setTreeDesc(String treeDesc) {
        this.treeDesc = treeDesc == null ? null : treeDesc.trim();
    }

    public Integer getDefaultTree() {
        return defaultTree;
    }

    public void setDefaultTree(Integer defaultTree) {
        this.defaultTree = defaultTree;
    }

    public Integer getSystemTree() {
        return systemTree;
    }

    public void setSystemTree(Integer systemTree) {
        this.systemTree = systemTree;
    }
}