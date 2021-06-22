package com.visiontek.chhattisgarhpds.Entities;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "KeyRegister",indices = {@Index(value = {"rcId", "commCode","product_id","gunny_type_id","season_master_id"})})
public class KeyRegister {
    /*
    * CREATE TABLE KeyRegister(rcId text,commNameEn text,commNameLl text,commCode text,totalEntitlement text,
    * balanceEntitlement text,schemeId text,schemeName text,commPrice text,Unit text,memberNameLl text,
    * memberNameEn text,AllotMonth text,AllotYear text,allocationType text,allotedMonth text,allotedYear text);*/


}
