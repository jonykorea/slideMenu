package com.tws.network.lib;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.AppController;
import com.app.define.LOG;
import com.tws.network.data.CoreGetPublicKey;
import com.tws.network.data.RetCode;
import com.tws.network.data.RetUserChecker;
import com.tws.network.lib.AirGsonRequest;
import com.tws.network.lib.JsonGsonRequest;
import com.tws.network.util.CommonParams;
import com.tws.network.util.DeviceInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jonychoi on 14. 11. 21..
 */
public class ApiAgent {

    private static final String TAG = "VolleyRequest";

    // into Domain
    //private static final String URL_DOMAIN = "https://airagt.inavi.com";
    private static final String URL_DOMAIN = "http://61.33.249.180:8020";


    // publickey info
    private static final String URL_PUBLICKEY = "/iNaviAir/air/inaviweb/PublicKey";

    // upload loaction
    private static final String URL_SETUSERLOC = "/soulbrown/if/setuserloc";

    // check user
    private static final String URL_USER_CHECKER = "/soulbrown/if/storeuserchecker";


    // 없으면 입력창 노출.

    // get publickey
    public void apiPublicKey(Context context, Response.Listener<CoreGetPublicKey> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_PUBLICKEY;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();

        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
           header.put("auth_key", auth);

        header.put("mdn", DeviceInfo.getMDN(context));

        // set add header E

        // set params S
        JSONObject jsonParams = new JSONObject();
        try {

            jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiPublicKey error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("reqParams " + reqParams);


        // set params E

        // request!
        AirGsonRequest<CoreGetPublicKey> gsObjRequest = new AirGsonRequest<CoreGetPublicKey>(
                Request.Method.POST,
                url,
                CoreGetPublicKey.class, header, reqParams,
                succListener, failListener

        );

        // request queue!
        AppController.getInstance().addToRequestQueue(gsObjRequest);

    }

    // setuserloc
    public void apiUserLoc(Context context,String userid,String lon,String lat, Response.Listener<RetCode> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_SETUSERLOC;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();
/*
        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
            header.put("auth_key", auth);

        header.put("mdn", DeviceInfo.getMDN(context));
*/
        // set add header E

        // set params S
        JSONObject jsonParams = new JSONObject();
        try {

            jsonParams.put("source","USERUI");
            jsonParams.put("userid","jony@thinkware.co.kr");
            jsonParams.put("lon","127.234234");
            jsonParams.put("lat","36.364354");

            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiUserLoc error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("reqParams " + reqParams);


        // set params E

        // request!
        JsonGsonRequest<RetCode> gsObjRequest = new JsonGsonRequest<RetCode>(
                Request.Method.POST,
                url,
                RetCode.class, header, reqParams,
                succListener, failListener

        );

        // request queue!
        AppController.getInstance().addToRequestQueue(gsObjRequest);

    }

    // apiUserChecker
    public void apiUserChecker(Context context,String userid, Response.Listener<RetUserChecker> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_USER_CHECKER;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();
/*
        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
            header.put("auth_key", auth);

        header.put("mdn", DeviceInfo.getMDN(context));
*/
        // set add header E

        // set params S
        JSONObject jsonParams = new JSONObject();
        try {

            jsonParams.put("source","STARTUI");
            jsonParams.put("usercode",userid);

            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiUserChecker error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("reqParams " + reqParams);


        // set params E

        // request!
        JsonGsonRequest<RetUserChecker> gsObjRequest = new JsonGsonRequest<RetUserChecker>(
                Request.Method.POST,
                url,
                RetUserChecker.class, header, reqParams,
                succListener, failListener

        );

        // request queue!
        AppController.getInstance().addToRequestQueue(gsObjRequest);

    }

    // ui Api E


}
