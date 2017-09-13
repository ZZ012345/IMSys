package com.rinc.imsys;

import java.io.Serializable;

/**
 * Created by ZhouZhi on 2017/9/11.
 */

public class PartInRecord implements Serializable {

    private int recordId;

    private String inputDateTime;

    private String operator;

    private String inputNum;

    private PartStock partStock;

    private int owner;

    public PartInRecord(int recordId, String inputDateTime, String operator, String inputNum,
                            PartStock partStock, int owner) {
        this.recordId = recordId;
        this.inputDateTime = inputDateTime;
        this.operator = operator;
        this.inputNum = inputNum;
        this.partStock = partStock;
        this.owner = owner;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getInputDateTime() {
        return inputDateTime;
    }

    public String getOperator() {
        return operator;
    }

    public String getInputNum() {
        return inputNum;
    }

    public PartStock getPartStock() {
        return partStock;
    }

    public int getOwner() {
        return owner;
    }
}
