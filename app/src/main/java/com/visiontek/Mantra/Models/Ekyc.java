package com.visiontek.Mantra.Models;

public class Ekyc {

    public String zdistrTxnId, eKYCDOB, eKYCGeneder, eKYCMemberName,eKYCPindCode;

    public void setzdistrTxnId(String zdistrTxnId) {
        this.zdistrTxnId = zdistrTxnId;
    }

    public void seteKYCDOB(String eKYCDOB) {
        this.eKYCDOB = eKYCDOB;
    }

    public void seteKYCGeneder(String eKYCGeneder) {
        this.eKYCGeneder = eKYCGeneder;
    }

    public void seteKYCMemberName(String eKYCMemberName) {
        this.eKYCMemberName = eKYCMemberName;
    }

    public void seteKYCPindCode(String eKYCPindCode) {
        this.eKYCPindCode = eKYCPindCode;
    }
    //-------------------------------------------------------------------
    public String getzdistrTxnId() {
        return zdistrTxnId;
    }

    public String geteKYCDOB() {
        return eKYCDOB;
    }

    public String geteKYCGeneder() {
        return eKYCGeneder;
    }

    public String geteKYCMemberName() {
        return eKYCMemberName;
    }

    public String geteKYCPindCode() {
        return eKYCPindCode;
    }

}
