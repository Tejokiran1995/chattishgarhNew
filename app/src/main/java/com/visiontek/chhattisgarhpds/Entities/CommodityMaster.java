package com.visiontek.chhattisgarhpds.Entities;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class CommodityMaster {


/*
    "CREATE TABLE IF NOT EXISTS commodityMaster(commCode text,commNameEn text,commNameLl text,measurmentUnit text,commonCommCode text
*/
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private int id;

    @ColumnInfo(name = "commCode")
    public String commCode;

    @ColumnInfo(name = "commNameEn")
    public String commNameEn;

    @ColumnInfo(name = "commNameLl")
    public String commNameLl;

    @ColumnInfo(name = "measurmentUnit")
    public String measurmentUnit;

    @ColumnInfo(name = "commonCommCode")
    public String commonCommCode;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommCode() {
        return commCode;
    }

    public void setCommCode(String commCode) {
        this.commCode = commCode;
    }

    public String getCommNameEn() {
        return commNameEn;
    }

    public void setCommNameEn(String commNameEn) {
        this.commNameEn = commNameEn;
    }

    public String getCommNameLl() {
        return commNameLl;
    }

    public void setCommNameLl(String commNameLl) {
        this.commNameLl = commNameLl;
    }

    public String getMeasurmentUnit() {
        return measurmentUnit;
    }

    public void setMeasurmentUnit(String measurmentUnit) {
        this.measurmentUnit = measurmentUnit;
    }

    public String getCommonCommCode() {
        return commonCommCode;
    }

    public void setCommonCommCode(String commonCommCode) {
        this.commonCommCode = commonCommCode;
    }













}
