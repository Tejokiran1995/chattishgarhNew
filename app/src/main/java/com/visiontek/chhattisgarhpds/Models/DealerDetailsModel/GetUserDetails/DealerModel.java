package com.visiontek.chhattisgarhpds.Models.DealerDetailsModel.GetUserDetails;

import com.visiontek.chhattisgarhpds.Models.RDModel;

import java.io.Serializable;

public class DealerModel implements Serializable {
    public String
            DUid,
            DName,
            Dtype,
            DAtype,
            Dfusion,
            Dnamell,
            Dwadh,
    //-----------------------
            DEALER_AUTH_TYPE,
    //-----------------------
             err_code = "1",
    //-----------------------
            fCount = "0",
            fType = "0",
            iCount = "0",
            iType = "0",
    //-----------------------
             dealertype,
    //----------------------
             EnterPassword;

    public int RD_SERVICE = 0;

    public int
            wadhflag,
            FIRflag,
            Fusionflag,
            fusionflag;

    public boolean click = false;

    public RDModel rdModel=new RDModel();

}
