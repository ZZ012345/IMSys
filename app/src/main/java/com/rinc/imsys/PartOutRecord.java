package com.rinc.imsys;

import java.io.Serializable;

/**
 * Created by ZhouZhi on 2017/9/12.
 */

public class PartOutRecord implements Serializable {

    private int recordId;

    private String outputDateTime;

    private String user;

    private String operator;

    private String outputNum;

    private String leftNum;

    private PartStock partStock;

    private int owner;

    public PartOutRecord(int recordId ,String outputDateTime, String user, String operator, String outputNum,
                             String leftNum, PartStock partStock, int owner) {
        this.recordId = recordId;
        this.outputDateTime = outputDateTime;
        this.user = user;
        this.operator = operator;
        this.outputNum = outputNum;
        this.leftNum = leftNum;
        this.partStock = partStock;
        this.owner = owner;
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

    public PartStock getPartStock() {
        return partStock;
    }

    public int getOwner() {
        return owner;
    }
}
