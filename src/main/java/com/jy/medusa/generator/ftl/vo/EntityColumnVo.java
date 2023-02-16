package com.jy.medusa.generator.ftl.vo;

/**
 * Created by SuperScorpion on 2017/10/27.
 */
public class EntityColumnVo {

    private boolean primarykeyFlag;
    private String column;
    private String lowwerName;
    private String upperName;
    private String javaType;
    private String comment;
    private String defaultStr;

    private boolean notOnlyColumnFlag;
    private String associRemark;
    private String associUpperName;
    private String associLowwerName;


    public String getAssociUpperName() {
        return associUpperName;
    }

    public void setAssociUpperName(String associUpperName) {
        this.associUpperName = associUpperName;
    }

    public String getAssociLowwerName() {
        return associLowwerName;
    }

    public void setAssociLowwerName(String associLowwerName) {
        this.associLowwerName = associLowwerName;
    }

    public String getAssociRemark() {
        return associRemark;
    }

    public void setAssociRemark(String associRemark) {
        this.associRemark = associRemark;
    }

    public boolean isNotOnlyColumnFlag() {
        return notOnlyColumnFlag;
    }

    public void setNotOnlyColumnFlag(boolean notOnlyColumnFlag) {
        this.notOnlyColumnFlag = notOnlyColumnFlag;
    }

    public String getDefaultStr() {
        return defaultStr;
    }

    public void setDefaultStr(String defaultStr) {
        this.defaultStr = defaultStr;
    }

    public boolean isPrimarykeyFlag() {
        return primarykeyFlag;
    }

    public void setPrimarykeyFlag(boolean primarykeyFlag) {
        this.primarykeyFlag = primarykeyFlag;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUpperName() {
        return upperName;
    }

    public void setUpperName(String upperName) {
        this.upperName = upperName;
    }

    public String getLowwerName() {
        return lowwerName;
    }

    public void setLowwerName(String lowwerName) {
        this.lowwerName = lowwerName;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
