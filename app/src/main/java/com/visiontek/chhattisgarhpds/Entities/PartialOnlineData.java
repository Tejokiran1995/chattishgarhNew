package com.visiontek.chhattisgarhpds.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "PartialOnlineData")
public class PartialOnlineData {
    /*
    * CREATE TABLE PartialOnlineData(OffPassword text,OfflineLogin text,OfflineTxnTime text,
    * Duration text,leftOfflineTime text,lastlogindate text,lastlogintime text,lastlogoutdate text,
    * lastlogouttime text,AllotMonth text,AllotYear text,pOfflineStoppedDate text);*/

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private int id;

    @ColumnInfo(name = "offPassword")
    private String offPassword;

    @ColumnInfo(name = "offlineLogin")
    private String offlineLogin;

    @ColumnInfo(name = "offlineTxnTime")
    private String offlineTxnTime;

    @ColumnInfo(name = "duration")
    private String duration;

    @ColumnInfo(name = "leftOfflineTime")
    private String leftOfflineTime;

    @ColumnInfo(name = "lastlogindate")
    private String lastlogindate;

    @ColumnInfo(name = "lastlogintime")
    private String lastlogintime;

    @ColumnInfo(name = "lastlogoutdate")
    private String lastlogoutdate;

    @ColumnInfo(name = "lastlogouttime")
    private String lastlogouttime;

    @ColumnInfo(name = "allotMonth")
    private String allotMonth;

    @ColumnInfo(name = "allotYear")
    private String allotYear;

    @ColumnInfo(name = "pOfflineStoppedDate")
    private String pOfflineStoppedDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOffPassword() {
        return offPassword;
    }

    public void setOffPassword(String offPassword) {
        this.offPassword = offPassword;
    }

    public String getOfflineLogin() {
        return offlineLogin;
    }

    public void setOfflineLogin(String offlineLogin) {
        this.offlineLogin = offlineLogin;
    }

    public String getOfflineTxnTime() {
        return offlineTxnTime;
    }

    public void setOfflineTxnTime(String offlineTxnTime) {
        this.offlineTxnTime = offlineTxnTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLeftOfflineTime() {
        return leftOfflineTime;
    }

    public void setLeftOfflineTime(String leftOfflineTime) {
        this.leftOfflineTime = leftOfflineTime;
    }

    public String getLastlogindate() {
        return lastlogindate;
    }

    public void setLastlogindate(String lastlogindate) {
        this.lastlogindate = lastlogindate;
    }

    public String getLastlogintime() {
        return lastlogintime;
    }

    public void setLastlogintime(String lastlogintime) {
        this.lastlogintime = lastlogintime;
    }

    public String getLastlogoutdate() {
        return lastlogoutdate;
    }

    public void setLastlogoutdate(String lastlogoutdate) {
        this.lastlogoutdate = lastlogoutdate;
    }

    public String getLastlogouttime() {
        return lastlogouttime;
    }

    public void setLastlogouttime(String lastlogouttime) {
        this.lastlogouttime = lastlogouttime;
    }

    public String getAllotMonth() {
        return allotMonth;
    }

    public void setAllotMonth(String allotMonth) {
        this.allotMonth = allotMonth;
    }

    public String getAllotYear() {
        return allotYear;
    }

    public void setAllotYear(String allotYear) {
        this.allotYear = allotYear;
    }

    public String getpOfflineStoppedDate() {
        return pOfflineStoppedDate;
    }

    public void setpOfflineStoppedDate(String pOfflineStoppedDate) {
        this.pOfflineStoppedDate = pOfflineStoppedDate;
    }
}
