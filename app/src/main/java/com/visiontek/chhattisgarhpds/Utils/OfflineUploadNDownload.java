package com.visiontek.chhattisgarhpds.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.visiontek.chhattisgarhpds.Activities.Device_Update;
import com.visiontek.chhattisgarhpds.Models.PartialOnlineData;
import com.visiontek.chhattisgarhpds.Models.ResponseData;
import com.visiontek.chhattisgarhpds.Models.UploadingModels.CommWiseData;
import com.visiontek.chhattisgarhpds.Models.UploadingModels.DataDownloadAckRequest;
import com.visiontek.chhattisgarhpds.Models.UploadingModels.UploadDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.visiontek.chhattisgarhpds.Models.AppConstants.DEVICEID;
import static com.visiontek.chhattisgarhpds.Models.AppConstants.OFFLINE_TOKEN;

public class OfflineUploadNDownload {

    Context context;
    OkHttpClient okHttpClient;
    DatabaseHelper databaseHelper;

    public OfflineUploadNDownload(Context context)
    {
        this.context = context;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        databaseHelper = new DatabaseHelper(context);
    }
    public int ManualServerUploadPartialTxns(String fpsId,String fpsSessionId)
    {
        RequestBody body = null;

        System.out.println("BOdy.." + body);
        String url="http://epos.nic.in/ePosCommonServiceCTG/eposCommon/pushFpsOfflineData";
        Gson gson = new Gson();

        int previousPendingCount = 21;
        int failedTrails = 0;


        while(true) {

            if (!Util.networkConnected(context))
            {
                Log.e("[ManlSrUpldPartialTxns]","network not connected");
                return -2;
            }
            UploadDataModel uploadDataModel = new UploadDataModel();
            List<CommWiseData> commWiseData = databaseHelper.getPendingOfflineData(20);
            PartialOnlineData partialOnlineData = databaseHelper.getPartialOnlineData();
            int totalRecords = databaseHelper.getTotCommodityTxns();
            int uploadingRecords = commWiseData.size();
            if (uploadingRecords == 0)
                break;
           /* if(uploadingRecords >= previousPendingCount)
            {
                failedTrails++;
            }
            else
                failedTrails = 0;
            if(failedTrails >= 2)
            {
                Log.e("[ManlSrUpldPartialTxns]","upload failed 2 times with same request");
                return -1;
            }*/
            //previousPendingCount = uploadingRecords;
            uploadDataModel.setFpsId(fpsId);
            uploadDataModel.setSessionId(fpsSessionId);//
            uploadDataModel.setStateCode("22");
            uploadDataModel.setTerminalId(DEVICEID);//
            uploadDataModel.setToken(OFFLINE_TOKEN);
            uploadDataModel.setFpsOfflineTransResponses(commWiseData);
            uploadDataModel.setFpsCbs(databaseHelper.getPendingStock());
            uploadDataModel.setUploadingRecords(uploadingRecords);
            uploadDataModel.setTotalRecords(totalRecords);
            uploadDataModel.setPendingRecords(Math.abs(totalRecords - uploadingRecords));
            uploadDataModel.setDistributionMonth(partialOnlineData.getAllotMonth());
            uploadDataModel.setDistributionYear(partialOnlineData.getAllotYear());

            String finalPayload = gson.toJson(uploadDataModel);

            body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),finalPayload );
            final Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
                if(response.isSuccessful()){

                    String responseData = response.body().string();
                    int updateRes= insertPosObRecords(responseData);

                    if(updateRes != 0)
                    {
                        Log.e("insertPosObRecords","returned : "+responseData);
                        return -1;
                    }

                    updateRes= updateBenfiaryTxnRecords(responseData);
                    if(updateRes != 0)
                    {
                        Log.e("updateBnfryTxnRecords","returned : "+responseData);
                        return -1;
                    }

                }else {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public int updateBenfiaryTxnRecords(String strJson)
    {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        int ret = 0;
        try {
            JSONObject jsonRootObject = new JSONObject(strJson);
            String msg = jsonRootObject.getString("respMessage");
            String respcode = jsonRootObject.getString("respCode");

            if(respcode.equals("00"))
            {
                JSONArray jsonArray1 = jsonRootObject.optJSONArray("updatedReceipts");
                int size = jsonArray1.length();
                System.out.println("[updateBenfiaryTxnRecords]  updatedReceipts.size :: " +size);
                if(size == 0)
                {
                    ret = -1;
                }
                for(int i=0; i < size; i++) {
                    JSONObject jsonObject = jsonArray1.getJSONObject(i);
                    String receiptId = jsonObject.optString("receiptId").toString();
                    String rcId = jsonObject.optString("rcId").toString();
                    String commCode = jsonObject.optString("commCode").toString();

                    String query = String.format("update BenfiaryTxn set TxnUploadSts = 'Y' where RecptId='%s' and RcId='%s' and CommCode='%s'",receiptId,rcId,commCode);
                    System.out.println("Query: " +query);
                    sqLiteDatabase.execSQL(query);
                }
            }
            else
                ret = -1;

        } catch (JSONException e) {
            e.printStackTrace();
            ret = -1;
        }
        finally {
            if(sqLiteDatabase != null && sqLiteDatabase.isOpen())
                sqLiteDatabase.close();
            return ret;
        }
    }

    public int insertPosObRecords(String strJson)
    {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        int ret = 0;
        try {
            JSONObject jsonRootObject = new JSONObject(strJson);
            String msg = jsonRootObject.getString("respMessage");
            String respcode = jsonRootObject.getString("respCode");

            if(respcode.equals("00"))
            {
                JSONArray jsonArray1 = jsonRootObject.optJSONArray("fpsCb");
                int size = jsonArray1.length();
                System.out.println("[insertPosObRecords]  fpsCb.size :: " +size);
                if(size == 0)
                {
                    ret = 0;
                }
                for(int i=0; i < size; i++) {
                    JSONObject jsonObject = jsonArray1.getJSONObject(i);
                    String commCode = jsonObject.optString("commCode").toString();
                    String commNameEn = jsonObject.optString("commNameEn").toString();
                    String commNameLl = jsonObject.optString("commNameLl").toString();
                    String closingBalance = String.format("%0.3lf",jsonObject.getDouble("closingBalance"));

                    String query = String.format("insert into Pos_Ob(commCode,commNameEn,commNameLl,closingBalance) VALUES(%s,%s,%s,%s)",commCode,commNameEn,commNameLl,closingBalance);
                    System.out.println("Query: " +query);
                    sqLiteDatabase.execSQL(query);
                }
            }
            else
                ret = -1;

        } catch (JSONException e) {
            e.printStackTrace();
            ret = -1;
        }
        finally {
            if(sqLiteDatabase != null && sqLiteDatabase.isOpen())
                sqLiteDatabase.close();
            return ret;
        }
    }

    public int updateTransStatus(String fpsId,String fpsSessionId,String partialDataDownloadFlag)
    {
        RequestBody body = null;

        System.out.println("BOdy.." + body);
        String url="http://epos.nic.in/ePosCommonServiceCTG/eposCommon/dataDownloadACK";
        Gson gson = new Gson();

        UploadDataModel uploadDataModel = new UploadDataModel();
        List<CommWiseData> commWiseData = databaseHelper.getPendingOfflineData(1000);
        PartialOnlineData partialOnlineData = databaseHelper.getPartialOnlineData();
        int totalRecords = databaseHelper.getTotCommodityTxns();
        int uploadingRecords = commWiseData.size();
        uploadDataModel.setFpsId(fpsId);
        uploadDataModel.setSessionId(fpsSessionId);//
        uploadDataModel.setStateCode("22");
        uploadDataModel.setTerminalId(DEVICEID);//
        uploadDataModel.setToken(OFFLINE_TOKEN);
        uploadDataModel.setFpsOfflineTransResponses(commWiseData);
        uploadDataModel.setUploadingRecords(uploadingRecords);
        uploadDataModel.setTotalRecords(totalRecords);
        uploadDataModel.setPendingRecords(Math.abs(totalRecords - uploadingRecords));
        uploadDataModel.setDistributionMonth(partialOnlineData.getAllotMonth());
        uploadDataModel.setDistributionYear(partialOnlineData.getAllotYear());
        uploadDataModel.setDataDownloadStatus(partialDataDownloadFlag);
        uploadDataModel.setKeyregisterDataDeleteStatus("U");
        if(partialDataDownloadFlag.equals("Y"))
            uploadDataModel.setFullDataUploadedStatus("Y");
        else
            uploadDataModel.setFullDataUploadedStatus("N");

        String finalPayload = gson.toJson(uploadDataModel);

        body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),finalPayload );
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){

                String responseData = response.body().string();
                return parseDataDownloadAckresponse(responseData);
            }else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int parseDataDownloadAckresponse(String response)
    {
        PartialOnlineData partialOnlineData = databaseHelper.getPartialOnlineData();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        int ret = -1;
        try {
            JSONObject jsonRootObject = new JSONObject(response);
            String msg = jsonRootObject.getString("respMessage");
            String respcode = jsonRootObject.getString("respCode");

            if(respcode.equals("00"))
            {
                sqLiteDatabase.execSQL("update BenfiaryTxn set TxnUploadSts = 'Y'");
                String deleteStatus = jsonRootObject.getString("deleteStatus");
                //JSONArray jsonArray1 = jsonRootObject.optJSONArray("fpsCb");
                if(deleteStatus.equals("Y") && partialOnlineData.getOfflineLogin().equals("N"))
                {
                    sqLiteDatabase.execSQL("DELETE FROM KeyRegister");
                    sqLiteDatabase.execSQL("DELETE FROM Pos_Ob");
                    sqLiteDatabase.execSQL("DELETE FROM CommodityMaster");
                    sqLiteDatabase.execSQL("DELETE FROM SchemeMaster");
                    sqLiteDatabase.execSQL("DELETE FROM BenfiaryTxn");
                }
                ret = 0;
            }
            else
                ret = -1;

        } catch (JSONException e) {
            e.printStackTrace();
            ret = -1;
        }
        finally {
            if(sqLiteDatabase != null && sqLiteDatabase.isOpen())
                sqLiteDatabase.close();
            return ret;
        }
    }

    public ResponseData postDataDownloadAck(String fpsId, String fpsSessionId, String partialDataDownloadFlag, String KeyregisterDataDeleteStatus)
    {
        RequestBody body = null;
        ResponseData responseDataModel = new ResponseData();
        responseDataModel.setRespCode(-1);
        responseDataModel.setRespMessage("Invalid Response from Server\\nPlease try again");

        System.out.println("BOdy.." + body);
        String url="http://epos.nic.in/ePosCommonServiceCTG/eposCommon/dataDownloadACK";

        DataDownloadAckRequest dataDownloadAckRequest = new DataDownloadAckRequest();
        Gson gson = new Gson();
        PartialOnlineData partialOnlineData = databaseHelper.getPartialOnlineData();

        dataDownloadAckRequest.setDataDownloadStatus("Y");
        dataDownloadAckRequest.setDistributionMonth(partialOnlineData.getAllotMonth());
        dataDownloadAckRequest.setDistributionYear(partialOnlineData.getAllotYear());
        dataDownloadAckRequest.setFpsId(fpsId);
        dataDownloadAckRequest.setKeyregisterDataDeleteStatus(KeyregisterDataDeleteStatus);
        dataDownloadAckRequest.setPendingRecords(0);
        dataDownloadAckRequest.setSessionId(fpsSessionId);
        dataDownloadAckRequest.setStateCode("22");
        dataDownloadAckRequest.setTerminalId(DEVICEID);
        dataDownloadAckRequest.setToken(OFFLINE_TOKEN);
        dataDownloadAckRequest.setTotalRecords(0);
        dataDownloadAckRequest.setUploadingRecords(0);

        if(KeyregisterDataDeleteStatus.equals("Y"))
        {
            List<CommWiseData> commWiseData = databaseHelper.getPendingOfflineData(1000);
            dataDownloadAckRequest.setFpsOfflineTransResponses(commWiseData);
        }

        String finalPayload = gson.toJson(dataDownloadAckRequest);

        body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),finalPayload );
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            Log.e("[postDataDownloadAck]","HTTP response code ==> "+response.code());
            if(response.isSuccessful()){

                String responseData = response.body().string();
                JSONObject jsonRootObject = new JSONObject(responseData);
                String respcode = jsonRootObject.getString("respCode");
                Log.e("[postDataDownloadAck]","response respCode ==> "+respcode);
                responseDataModel.setRespCode(Integer.parseInt(respcode));
                responseDataModel.setRespMessage(jsonRootObject.getString("respMessage"));
            }else {
                Log.e("[postDataDownloadAck]","Failed ==> "+response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return responseDataModel;
    }
}
