package com.visiontek.chhattisgarhpds.Models.DATAModels;


public class DataModel {
    public String name;
    public String type;
    public boolean isSelected = false;

    public DataModel(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}