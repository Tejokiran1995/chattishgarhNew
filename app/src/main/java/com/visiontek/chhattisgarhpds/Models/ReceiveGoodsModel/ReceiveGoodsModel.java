package com.visiontek.chhattisgarhpds.Models.ReceiveGoodsModel;

import java.io.Serializable;
import java.util.ArrayList;

public class ReceiveGoodsModel implements Serializable {
    public String  length,
            fps,
            month,
            year,
            chit,
            cid,
            orderno,
            truckno;
    public int select;
    public String YouEditTextValue;
    public String AFTERDATA;
    public Float textdata;

    public ArrayList<tcCommDetails> tcCommDetails=new ArrayList<>();


}
