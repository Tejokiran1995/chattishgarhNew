package com.visiontek.chhattisgarhpds.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "PrintData")
public class PrintData {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private int id;

    @ColumnInfo(name = "balanceQty")
    private double balanceQty;

    @ColumnInfo(name = "carryOver")
    private double carryOver;

    @ColumnInfo(name = "commIndividualAmount")
    private String commIndividualAmount;

    @ColumnInfo(name = "commodityName")
    private String commodityName;

    @ColumnInfo(name = "commodityNameLocal")
    private String commodityNameLocal;

    @ColumnInfo(name = "memberName")
    private String memberName;

    @ColumnInfo(name = "memberNameLocal")
    private String memberNameLocal;

    @ColumnInfo(name = "recieptId")
    private String recieptId;

    @ColumnInfo(name = "retailPrice")
    private double retailPrice;

    @ColumnInfo(name = "schemeDesc")
    private String schemeDesc;

    @ColumnInfo(name = "schemeDescLocal")
    private String schemeDescLocal;


    /*
     * CREATE TABLE Print_Table (ID INTEGER PRIMARY KEY AUTOINCREMENT,bal_qty TEXT ,
     * carry_over TEXT,commIndividualAmount TEXT,comm_name TEXT, comm_name_ll TEXT,
     *  member_name TEXT,member_name_ll TEXT,reciept_id TEXT,retail_price TEXT,
     * scheme_desc_en TEXT,scheme_desc_ll TEXT,tot_amount TEXT,total_quantity TEXT,
     * transaction_time TEXT,uid_refer_no TEXT,allocationType TEXT,allotedMonth TEXT,
     * allotedYear TEXT,commCode TEXT,closingBalance TEXT);*/


    @ColumnInfo(name = "totalAmount")
    private double totAmount;

    @ColumnInfo(name = "totalQuantity")
    private double totalQuantity;

    @ColumnInfo(name = "transactionTime")
    private String transactionTime;


    @ColumnInfo(name = "uidReferNo")
    private String uidReferNo;

    @ColumnInfo(name = "fusion")
    private String fusion;

    @ColumnInfo(name = "allocationType")
    private String allocationType;

    @ColumnInfo(name = "allotedMonth")
    private String allotedMonth;

    @ColumnInfo(name = "allotedYear")
    private String allotedYear;

    @ColumnInfo(name = "commCode")
    private String commdityCode;

    @ColumnInfo(name = "closingBalance")
    private double closingBalance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBalanceQty() {
        return balanceQty;
    }

    public void setBalanceQty(double balanceQty) {
        this.balanceQty = balanceQty;
    }

    public double getCarryOver() {
        return carryOver;
    }

    public void setCarryOver(double carryOver) {
        this.carryOver = carryOver;
    }

    public String getCommIndividualAmount() {
        return commIndividualAmount;
    }

    public void setCommIndividualAmount(String commIndividualAmount) {
        this.commIndividualAmount = commIndividualAmount;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getCommodityNameLocal() {
        return commodityNameLocal;
    }

    public void setCommodityNameLocal(String commodityNameLocal) {
        this.commodityNameLocal = commodityNameLocal;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberNameLocal() {
        return memberNameLocal;
    }

    public void setMemberNameLocal(String memberNameLocal) {
        this.memberNameLocal = memberNameLocal;
    }

    public String getRecieptId() {
        return recieptId;
    }

    public void setRecieptId(String recieptId) {
        this.recieptId = recieptId;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public String getSchemeDesc() {
        return schemeDesc;
    }

    public void setSchemeDesc(String schemeDesc) {
        this.schemeDesc = schemeDesc;
    }

    public String getSchemeDescLocal() {
        return schemeDescLocal;
    }

    public void setSchemeDescLocal(String schemeDescLocal) {
        this.schemeDescLocal = schemeDescLocal;
    }

    public double getTotAmount() {
        return totAmount;
    }

    public void setTotAmount(double totAmount) {
        this.totAmount = totAmount;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getUidReferNo() {
        return uidReferNo;
    }

    public void setUidReferNo(String uidReferNo) {
        this.uidReferNo = uidReferNo;
    }

    public String getFusion() {
        return fusion;
    }

    public void setFusion(String fusion) {
        this.fusion = fusion;
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

    public String getCommdityCode() {
        return commdityCode;
    }

    public void setCommdityCode(String commdityCode) {
        this.commdityCode = commdityCode;
    }

    public double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(double closingBalance) {
        this.closingBalance = closingBalance;
    }

}
