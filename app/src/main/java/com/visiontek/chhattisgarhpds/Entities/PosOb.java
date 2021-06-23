package com.visiontek.chhattisgarhpds.Entities;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class PosOb
{
   /*"CREATE TABLE IF NOT EXISTS Pos_Ob(commCode text,commNameEn text,commNameLl text,closingBalance text*/

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private int id;

    @ColumnInfo(name = "commCode")
    public String commCode;

    @ColumnInfo(name = "commNameEn")
    public String commNameEn;


    @ColumnInfo(name = "commNameLl")
    public String commNameLl;

    @ColumnInfo(name = "closingBalance")
    public String closingBalance;

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

    public String getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(String closingBalance) {
        this.closingBalance = closingBalance;
    }
}
