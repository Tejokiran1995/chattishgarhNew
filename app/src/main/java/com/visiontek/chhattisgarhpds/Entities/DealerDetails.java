package com.visiontek.chhattisgarhpds.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "DealerDetails")
public class DealerDetails {
    /*
    * CREATE TABLE Dealers_Table (ID INTEGER PRIMARY KEY AUTOINCREMENT,Dealers_Auth_Type TEXT ,
    * Dealers_Fusion TEXT,Dealers_Type TEXT, Dealers_Names TEXT,Dealers_Names_Ll TEXT, Dealers_Uids TEXT,Dealers_Wadh TEXT );*/

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private int id;

    @ColumnInfo(name = "authType")
    private String authType;

    @ColumnInfo(name = "fusion")
    private String fusion;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "nameLocal")
    private String nameLocal;

    @ColumnInfo(name = "aadhaarNo")
    private String aadhaarNo;

    @ColumnInfo(name = "wadh")
    private String wadh;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
