package com.visiontek.chhattisgarhpds.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "KeyRegister",indices = {@Index(value = {"rcId", "commCode","product_id","gunny_type_id","season_master_id"})})
public class KeyRegister {
    /*
    * CREATE TABLE KeyRegister(rcId text,commNameEn text,commNameLl text,commCode text,totalEntitlement text,
    * balanceEntitlement text,schemeId text,schemeName text,commPrice text,Unit text,memberNameLl text,
    * memberNameEn text,AllotMonth text,AllotYear text,allocationType text,allotedMonth text,allotedYear text);*/
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private int id;

    @ColumnInfo(name = "commNameEn")
    public String commNameEn;

    @ColumnInfo(name = "commNameLl")
    public String commNameLl;

    @ColumnInfo(name = "commCode")
    public String commCode;

    @ColumnInfo(name = "totalEntitlement")
    public String totalEntitlement;

    @ColumnInfo(name = "balanceEntitlement")
    public String balanceEntitlement;

    @ColumnInfo(name = "schemeId")
    public String schemeId;

    @ColumnInfo(name = "schemeName")
    public String schemeName;

    @ColumnInfo(name = "commPrice")
    public String commPrice;

    @ColumnInfo(name = "Unit")
    public String Unit;

    @ColumnInfo(name = "memberNameLl")
    public String memberNameLl;

    @ColumnInfo(name = "memberNameEn")
    public String memberNameEn;

    @ColumnInfo(name = "AllotMonth")
    public String AllotMonth;

    @ColumnInfo(name = "AllotYear")
    public String AllotYear;

    @ColumnInfo(name = "allocationType")
    public String allocationType;


    @ColumnInfo(name = "allotedMonth")
    public String allotedMonth;

    @ColumnInfo(name = "allotedYear")
    public String allotedYear;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCommCode() {
        return commCode;
    }

    public void setCommCode(String commCode) {
        this.commCode = commCode;
    }

    public String getTotalEntitlement() {
        return totalEntitlement;
    }

    public void setTotalEntitlement(String totalEntitlement) {
        this.totalEntitlement = totalEntitlement;
    }

    public String getBalanceEntitlement() {
        return balanceEntitlement;
    }

    public void setBalanceEntitlement(String balanceEntitlement) {
        this.balanceEntitlement = balanceEntitlement;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getCommPrice() {
        return commPrice;
    }

    public void setCommPrice(String commPrice) {
        this.commPrice = commPrice;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getMemberNameLl() {
        return memberNameLl;
    }

    public void setMemberNameLl(String memberNameLl) {
        this.memberNameLl = memberNameLl;
    }

    public String getMemberNameEn() {
        return memberNameEn;
    }

    public void setMemberNameEn(String memberNameEn) {
        this.memberNameEn = memberNameEn;
    }

    public String getAllotMonth() {
        return AllotMonth;
    }

    public void setAllotMonth(String allotMonth) {
        AllotMonth = allotMonth;
    }

    public String getAllotYear() {
        return AllotYear;
    }

    public void setAllotYear(String allotYear) {
        AllotYear = allotYear;
    }

    public String getAllocationType() {
        return allocationType;
    }

    public void setAllocationType(String allocationType) {
        this.allocationType = allocationType;
    }

    public String getAllotedMonth() {
        return allotedMonth;
    }

    public void setAllotedMonth(String allotedMonth) {
        this.allotedMonth = allotedMonth;
    }

    public String getAllotedYear() {
        return allotedYear;
    }

    public void setAllotedYear(String allotedYear) {
        this.allotedYear = allotedYear;
    }
}
