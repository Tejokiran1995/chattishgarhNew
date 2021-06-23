package com.visiontek.chhattisgarhpds.Entities;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class SchemeMaster {


   /* "CREATE TABLE IF NOT EXISTS schemeMaster(schemeId text,schemeName text)"*/

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private int id;

    @ColumnInfo(name = "schemeId")
    public String schemeId;
    @ColumnInfo(name = "schemeName")
    public String schemeName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
