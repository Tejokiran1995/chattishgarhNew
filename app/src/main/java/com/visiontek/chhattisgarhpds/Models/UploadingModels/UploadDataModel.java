package com.visiontek.chhattisgarhpds.Models.UploadingModels;

import java.util.List;

public class UploadDataModel {

    private String fpsId;
    private String stateCode;
    private String token;
    private String sessionId;
    private String terminalId;


    List<CommWiseData> fpsOfflineTransResponses;
    List<StockData> fpsCbs;

    private  int totalRecords;
    private  int uploadingRecords;
    private  int pendingRecords;
    private  String distributionMonth;
    private  String distributionYear;

    public String getFpsId() {
        return fpsId;
    }

    public void setFpsId(String fpsId) {
        this.fpsId = fpsId;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public List<CommWiseData> getFpsOfflineTransResponses() {
        return fpsOfflineTransResponses;
    }

    public void setFpsOfflineTransResponses(List<CommWiseData> fpsOfflineTransResponses) {
        this.fpsOfflineTransResponses = fpsOfflineTransResponses;
    }

    public List<StockData> getFpsCbs() {
        return fpsCbs;
    }

    public void setFpsCbs(List<StockData> fpsCbs) {
        this.fpsCbs = fpsCbs;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getUploadingRecords() {
        return uploadingRecords;
    }

    public void setUploadingRecords(int uploadingRecords) {
        this.uploadingRecords = uploadingRecords;
    }

    public int getPendingRecords() {
        return pendingRecords;
    }

    public void setPendingRecords(int pendingRecords) {
        this.pendingRecords = pendingRecords;
    }

    public String getDistributionMonth() {
        return distributionMonth;
    }

    public void setDistributionMonth(String distributionMonth) {
        this.distributionMonth = distributionMonth;
    }

    public String getDistributionYear() {
        return distributionYear;
    }

    public void setDistributionYear(String distributionYear) {
        this.distributionYear = distributionYear;
    }
}


