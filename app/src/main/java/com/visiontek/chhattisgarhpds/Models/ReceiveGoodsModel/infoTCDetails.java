package com.visiontek.chhattisgarhpds.Models.ReceiveGoodsModel;

import java.io.Serializable;
import java.util.ArrayList;

public class infoTCDetails implements Serializable {
    public String
            fpsId,
            allotedMonth,
            allotedYear,
            truckChitNo,
            challanId,
            allocationOrderNo,
            truckNo,
            CommLength;
    public ArrayList<tcCommDetails> tcCommDetails= new ArrayList<>();
}
