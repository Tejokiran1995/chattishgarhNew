package com.visiontek.chhattisgarhpds.Models.IssueModel.MemberDetailsModel.GetURLDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Member implements Serializable {
    //==========================carddetails==================================
    public carddetails carddetails = new carddetails();

    //==========================commDetails==================================
    public List<commDetails> commDetails = new ArrayList<>();

    //==========================memberdetails================================
    public List<memberdetails> memberdetails = new ArrayList<>();
}
