package com.visiontek.Mantra.Models;

public class DataModel5 {

    public boolean isSelected = false;
    private final String Name;
    private final String Uid;
    private final String Status;

    public DataModel5(String name, String uid, String status) {
        this.Name = name;
        this.Uid = uid;
        this.Status = status;
    }

    public String getName() {
        return Name;
    }

    public String getUid() {
        return Uid;
    }

    public String getStatus() {
        return Status;
    }
}
