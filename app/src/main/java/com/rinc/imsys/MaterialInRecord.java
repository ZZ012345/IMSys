package com.rinc.imsys;

import java.io.Serializable;

/**
 * Created by zhouzhi on 2017/8/19.
 */

public class MaterialInRecord implements Serializable {

    private int recordId;

    private String inputDateTime;

    private String operator;

    private String inputNum;

    private String description;

    public MaterialInRecord(int recordId, String inputDateTime, String operator, String inputNum, String description) {
        this.recordId = recordId;
        this.inputDateTime = inputDateTime;
        this.operator = operator;
        this.inputNum = inputNum;
        this.description = description;
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

    public String getDescription() {
        return description;
    }
}
