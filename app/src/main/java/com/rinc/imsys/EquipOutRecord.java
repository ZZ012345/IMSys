package com.rinc.imsys;

import java.io.Serializable;

/**
 * Created by ZhouZhi on 2017/9/23.
 */

public class EquipOutRecord implements Serializable {

    private int recordId;

    private String outputDateTime;

    private String user;

    private String operator;

    private String outputNum;

    private String leftNum;

    private String description;

    public EquipOutRecord(int recordId ,String outputDateTime, String user, String operator, String outputNum,
                             String leftNum, String description) {
        this.recordId = recordId;
        this.outputDateTime = outputDateTime;
        this.user = user;
        this.operator = operator;
        this.outputNum = outputNum;
        this.leftNum = leftNum;
        this.description = description;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getOutputDateTime() {
        return outputDateTime;
    }

    public String getUser() {
        return user;
    }

    public String getOperator() {
        return operator;
    }

    public String getOutputNum() {
        return outputNum;
    }

    public String getLeftNum() {
        return leftNum;
    }

    public String getDescription() {
        return description;
    }
}
