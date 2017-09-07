package com.rinc.imsys;

import java.io.Serializable;

/**
 * Created by ZhouZhi on 2017/9/6.
 */

public class PartStock implements Serializable {

    private int databaseid;

    private String id;

    private String type;

    private String storestate;

    private String mark;

    private String band;

    private String original;

    private String year;

    private String state;

    private String position;

    private String unit;

    private String name;

    private String company;

    private String machineName;

    private String machineType;

    private String machineBand;

    private String condition;

    private String vulnerability;

    private String description;

    private String num;

    public PartStock(int databaseid, String id, String type, String storestate, String mark, String band, String original,
                     String year, String state, String position, String unit, String name, String company, String machineName,
                     String machineType, String machineBand, String condition, String vulnerability, String description, String num) {
        this.databaseid = databaseid;
        this.id = id;
        this.type = type;
        this.storestate = storestate;
        this.mark = mark;
        this.band = band;
        this.original = original;
        this.year = year;
        this.state = state;
        this.position = position;
        this.unit = unit;
        this.name = name;
        this.company = company;
        this.machineName = machineName;
        this.machineType = machineType;
        this.machineBand = machineBand;
        this.condition = condition;
        this.vulnerability = vulnerability;
        this.description = description;
        this.num = num;
    }

    public int getDatabaseid() {
        return databaseid;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getStorestate() {
        return storestate;
    }

    public String getMark() {
        return mark;
    }

    public String getBand() {
        return band;
    }

    public String getOriginal() {
        return original;
    }

    public String getYear() {
        return year;
    }

    public String getState() {
        return state;
    }

    public String getPosition() {
        return position;
    }

    public String getUnit() {
        return unit;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getMachineName() {
        return machineName;
    }

    public String getMachineType() {
        return machineType;
    }

    public String getMachineBand() {
        return machineBand;
    }

    public String getCondition() {
        return condition;
    }

    public String getVulnerability() {
        return vulnerability;
    }

    public String getDescription() {
        return description;
    }

    public String getNum() {
        return num;
    }
}
