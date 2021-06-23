package com.visiontek.chhattisgarhpds.Entities;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class BenfiaryTxn {

/*
"CREATE TABLE IF NOT EXISTS BenfiaryTxn(FpsId text,RcId text,SchemeId text," +
        "CommCode text,TotQty text,BalQty text,IssuedQty text,Rate text,commAmount text," +
        "TotAmt text,RecptId text,MemberName text,DateTime text,TxnUploadSts text," +
        "TxnType text,AllotMonth text, AllotYear text,allocationType text)
*/
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private int id;

    @ColumnInfo(name = "FpsId")
    public String FpsId;
    @ColumnInfo(name = "RcId")
    public String RcId;

    @ColumnInfo(name = "SchemeId")
    public String SchemeId;
    @ColumnInfo(name = "commCode")
    public String benfiaryTxncommCode;
    @ColumnInfo(name = "TotQty")
    public String TotQty;
    @ColumnInfo(name = "BalQty")
    public String BalQty;

    @ColumnInfo(name = "IssuedQty")
    public String IssuedQty;

    @ColumnInfo(name = "Rate")
    public String Rate;

    @ColumnInfo(name = "commAmount")
    public String commAmount;

    @ColumnInfo(name = "TotAmt")
    public String TotAmt;

    @ColumnInfo(name = "RecptId")
    public String RecptId;

    @ColumnInfo(name = "MemberName")
    public String MemberName;

    @ColumnInfo(name = "DateTime")
    public String DateTime;

    @ColumnInfo(name = "TxnUploadSts")
    public String TxnUploadSts;

    @ColumnInfo(name = "TxnType")
    public String TxnType;


    @ColumnInfo(name = "AllotMonth")
    public String AllotMonth;

    @ColumnInfo(name = "allotedYear")
    public String allotedYear;

    @ColumnInfo(name = "allocationType")
    public String allocationType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFpsId() {
        return FpsId;
    }

    public void setFpsId(String fpsId) {
        FpsId = fpsId;
    }

    public String getRcId() {
        return RcId;
    }

    public void setRcId(String rcId) {
        RcId = rcId;
    }

    public String getSchemeId() {
        return SchemeId;
    }

    public void setSchemeId(String schemeId) {
        SchemeId = schemeId;
    }

    public String getBenfiaryTxncommCode() {
        return benfiaryTxncommCode;
    }

    public void setBenfiaryTxncommCode(String benfiaryTxncommCode) {
        this.benfiaryTxncommCode = benfiaryTxncommCode;
    }

    public String getTotQty() {
        return TotQty;
    }

    public void setTotQty(String totQty) {
        TotQty = totQty;
    }

    public String getBalQty() {
        return BalQty;
    }

    public void setBalQty(String balQty) {
        BalQty = balQty;
    }

    public String getIssuedQty() {
        return IssuedQty;
    }

    public void setIssuedQty(String issuedQty) {
        IssuedQty = issuedQty;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getCommAmount() {
        return commAmount;
    }

    public void setCommAmount(String commAmount) {
        this.commAmount = commAmount;
    }

    public String getTotAmt() {
        return TotAmt;
    }

    public void setTotAmt(String totAmt) {
        TotAmt = totAmt;
    }

    public String getRecptId() {
        return RecptId;
    }

    public void setRecptId(String recptId) {
        RecptId = recptId;
    }

    public String getMemberName() {
        return MemberName;
    }

    public void setMemberName(String memberName) {
        MemberName = memberName;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getTxnUploadSts() {
        return TxnUploadSts;
    }

    public void setTxnUploadSts(String txnUploadSts) {
        TxnUploadSts = txnUploadSts;
    }

    public String getTxnType() {
        return TxnType;
    }

    public void setTxnType(String txnType) {
        TxnType = txnType;
    }

    public String getAllotMonth() {
        return AllotMonth;
    }

    public void setAllotMonth(String allotMonth) {
        AllotMonth = allotMonth;
    }

    public String getAllotedYear() {
        return allotedYear;
    }

    public void setAllotedYear(String allotedYear) {
        this.allotedYear = allotedYear;
    }

    public String getAllocationType() {
        return allocationType;
    }

    public void setAllocationType(String allocationType) {
        this.allocationType = allocationType;
    }
}
