package com.visiontek.chhattisgarhpds.Models.DealerDetailsModel.GetURLDetails.fpsCommonInfoModel;

import java.io.Serializable;
import java.util.ArrayList;

public class fpsCommonInfo implements Serializable {
    public String
            dealer_password,
            distCode,
            flasMessage1,
            flasMessage2,
            fpsId,
            fpsSessionId,
            keyregisterDataDeleteStatus,
            keyregisterDownloadStatus,
            latitude,
            loginRequestTime,
            logoutTime,
            longtude,
            minQty,
            partialOnlineOfflineStatus,
            responseTimedOutTimeInSec,
            routeOffEnable,
            versionUpdateRequired,
            wadhValue,
            weighAccuracyValueInGms,
            weighingStatus,
            eKYCPrompt;

//================================fpsDetails===========================

    public ArrayList<fpsDetails> fpsDetails=new ArrayList<>();
}
