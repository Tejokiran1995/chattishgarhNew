package com.visiontek.Mantra.Models;

public class InspectionAuth {
    public String auth_transaction_code, inspectorDesignation, inspectorName;

    public  void setauth_transaction_code(String auth_transaction_code) {
        this.auth_transaction_code = auth_transaction_code;
    }

    public  void setinspectorDesignation(String inspectorDesignation) {
        this.inspectorDesignation = inspectorDesignation;
    }

    public  void setinspectorName(String inspectorName) {
        this.inspectorName = inspectorName;
    }
    //----------------------------------------------------------

    public  String getauth_transaction_code() {
        return auth_transaction_code;
    }

    public  String getinspectorDesignation() {
        return inspectorDesignation;
    }

    public  String getinspectorName() {
        return inspectorName;
    }

}
