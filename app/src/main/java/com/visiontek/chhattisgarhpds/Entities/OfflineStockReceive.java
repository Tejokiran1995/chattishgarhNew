package com.visiontek.chhattisgarhpds.Entities;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class OfflineStockReceive {


 /* "CREATE TABLE IF NOT EXISTS OfflineStockReceive(TruckChitNum text,Challan text,VechNum text," +
          "FpsId text,SchemeId text,CommCode text,RecvdQty text,Unit text," +
          "Month text,Year text,DateTime text,RecvdUploadSts text,RecvdMode text
*/

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private int id;
    @ColumnInfo(name = "TruckChitNum")
    private String TruckChitNum;
    @ColumnInfo(name = "Challan")
    private String Challan;

    @ColumnInfo(name = "VechNum")
    private String VechNum;

    @ColumnInfo(name = "FpsId")
    private String FpsId;

    @ColumnInfo(name = "SchemeId")
    private String SchemeId;

    @ColumnInfo(name = "CommCode")
    private String CommCode;

    @ColumnInfo(name = "RecvdQty")
    private String RecvdQty;


    @ColumnInfo(name = "Unit")
    private String Unit;

    @ColumnInfo(name = "Month")
    private String Month;


    @ColumnInfo(name = "Year")
    private String Year;

    @ColumnInfo(name = "DateTime")
    private String DateTime;
    @ColumnInfo(name = "RecvdUploadSts")
    private String RecvdUploadSts;


    @ColumnInfo(name = "RecvdMode")
    private String RecvdMode;






}


