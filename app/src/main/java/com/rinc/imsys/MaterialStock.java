package com.rinc.imsys;

import java.io.Serializable;

/**
 * Created by zhouzhi on 2017/8/17.
 */

public class MaterialStock implements Serializable {

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

    private String description;

    private String num;

    public MaterialStock(int databaseid, String id, String type, String storestate, String mark, String band, String original,
                         String year, String state, String position, String unit, String description, String num) {
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

    public String getDescription() {
        return description;
    }

    public String getNum() {
        return num;
    }
}
