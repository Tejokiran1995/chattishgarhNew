package com.visiontek.chhattisgarhpds.Models.IssueModel;

import java.io.Serializable;
import java.util.ArrayList;

public class LastReceipt implements Serializable {
    //=========================lastReceiptComm=======================
    public ArrayList<LastReceiptComm> lastReceiptComm=new ArrayList<>();

    public String
            rcId,
            retail_price;

}
