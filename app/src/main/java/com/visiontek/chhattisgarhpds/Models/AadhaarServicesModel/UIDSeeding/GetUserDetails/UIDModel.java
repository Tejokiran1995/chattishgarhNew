package com.visiontek.chhattisgarhpds.Models.AadhaarServicesModel.UIDSeeding.GetUserDetails;

import com.visiontek.chhattisgarhpds.Models.RDModel;

import java.io.Serializable;

public class UIDModel implements Serializable {
    public String
            bfd_1,
            bfd_2,
            bfd_3,
            memberId,
            memberName,
            memberNamell,
            member_fusion,
            uid,
            w_uid_status,
    //-----------------------
    MEMBER_AUTH_TYPE,
    //-----------------------
    err_code = "1",
    //-----------------------
    fCount = "0",
            fType = "0",
            iCount = "0",
            iType = "0",
    //-----------------------
    UID_Details_Aadhaar,
    //----------------------
    Enter_UID;

    public int RD_SERVICE = 0;

    public int
            wadhflag,
            FIRflag,
            Fusionflag,
            fusionflag,
            EKYC;

    public boolean click = false;

    public boolean
            mMan,
            mBIO,
            mDeal;

    public RDModel rdModel=new RDModel();

}
