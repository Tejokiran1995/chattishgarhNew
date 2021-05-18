package com.visiontek.Mantra.Models;

public class DataModel2 {

    public boolean isSelected = false;
    private final String bal;
    private final String rate;
    private final String req;
    private final String tot;


    public DataModel2(String tot, String bal,
                      String req, String rate) {
        this.tot = tot;
        this.bal = bal;
        this.req = req;
        this.rate = rate;

    }


    public String getBal() {
        return bal;
    }

    public String getRate() {
        return rate;
    }

    public String getReq() {
        return req;
    }

    public String getTot() {
        return tot;
    }
}
