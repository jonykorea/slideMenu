package com.tws.network.lib;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.define.LOG;
import com.tws.network.api.ApiBase;
import com.tws.network.data.CoreGetPublicKey;
import com.tws.network.data.RetCode;
import com.tws.network.data.RetOrderList;
import com.tws.network.data.RetOrderMenu;
import com.tws.network.data.RetUserChecker;
import com.tws.network.lib.AirGsonRequest;
import com.tws.network.lib.JsonGsonRequest;
import com.tws.network.util.CommonParams;
import com.tws.network.util.DeviceInfo;
import com.tws.soul.soulbrown.data.Menu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jonychoi on 14. 11. 21..
 */
public class ApiAgent {

    private static final String TAG = "VolleyRequest";

    // into Domain
    //private static final String URL_DOMAIN = "https://airagt.inavi.com";
    private static final String URL_DOMAIN = "http://107.191.60.183:8020";


    // publickey info
    private static final String URL_PUBLICKEY = "/iNaviAir/air/inaviweb/PublicKey";

    // upload loaction
    private static final String URL_SETUSERLOC = "/soulbrown/if/setuserloc";

    // check user
    private static final String URL_USER_CHECKER = "/soulbrown/if/storeuserchecker";

    // order
    private static final String URL_ORDER_MENU = "/soulbrown/if/ordermenu";

    // orderlist
    private static final String URL_ORDER_LIST = "/soulbrown/if/getorderlist";

    // push key user
    private static final String URL_PUSHKEY_USER = "/soulbrown/if/reguserkey";

    // push key owner
    private static final String URL_PUSHKEY_OWNER = "/soulbrown/if/setstoreon";

    // chg status order menu
    private static final String URL_STATUS_ORDER_MENU = "/soulbrown/if/chgorderstatus";






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

        LOG.d("url : "+url+" reqParams : " + reqParams);


        // set params E

        // request!
        AirGsonRequest<CoreGetPublicKey> gsObjRequest = new AirGsonRequest<CoreGetPublicKey>(
                Request.Method.POST,
                url,
                CoreGetPublicKey.class, header, reqParams,
                succListener, failListener

        );

        // request queue!
        ApiBase.getInstance(context).addToRequestQueue(gsObjRequest);



    }

    // setuserloc
    public void apiUserLoc(Context context,String userid, String lon,String lat, Response.Listener<RetCode> succListener, Response.ErrorListener failListener) {

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
            jsonParams.put("userid",userid);
            jsonParams.put("lon",lon);
            jsonParams.put("lat",lat);

            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiUserLoc error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : "+url+" reqParams : " + reqParams);


        // set params E

        // request!
        JsonGsonRequest<RetCode> gsObjRequest = new JsonGsonRequest<RetCode>(
                Request.Method.POST,
                url,
                RetCode.class, header, reqParams,
                succListener, failListener

        );

        // request queue!
        ApiBase.getInstance(context).addToRequestQueue(gsObjRequest);

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

        LOG.d("url : "+url+" reqParams : " + reqParams);


        // set params E

        // request!
        JsonGsonRequest<RetUserChecker> gsObjRequest = new JsonGsonRequest<RetUserChecker>(
                Request.Method.POST,
                url,
                RetUserChecker.class, header, reqParams,
                succListener, failListener

        );

        // request queue!
        ApiBase.getInstance(context).addToRequestQueue(gsObjRequest);

    }
    // apiOrderMenu
    public void apiOrderMenu(Context context,String userid, String storeid, String arriveTime , List<Menu> listMenu, Response.Listener<RetOrderMenu> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_ORDER_MENU;

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
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonSubParams = null;

        try {

            jsonParams.put("source","USERUI");
            jsonParams.put("userid",userid);
            jsonParams.put("storeid",storeid);
            jsonParams.put("arrivaltime",arriveTime);

            if(listMenu!=null) {
                for (int i = 0; i < listMenu.size(); i++) {


                    jsonSubParams = new JSONObject();

                    int cnt = listMenu.get(i).count;

                    if( cnt != 0) {

                        jsonSubParams.put("menuname", listMenu.get(i).name);
                        jsonSubParams.put("menuprice", Integer.toString(listMenu.get(i).price));
                        jsonSubParams.put("count", Integer.toString(cnt));
                        jsonArray.put(jsonSubParams);
                    }
                }
            jsonParams.put("orderdata", jsonArray);

            }
            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiOrderMenu error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : "+url+" reqParams : " + reqParams);


        // set params E

        // request!
        JsonGsonRequest<RetOrderMenu> gsObjRequest = new JsonGsonRequest<RetOrderMenu>(
                Request.Method.POST,
                url,
                RetOrderMenu.class, header, reqParams,
                succListener, failListener

        );

        // request queue!
        ApiBase.getInstance(context).addToRequestQueue(gsObjRequest);

    }

    // apiGetOrderList
    public void apiGetOrderList(Context context, String source, String userid, String storeid, String selectFlag , Response.Listener<RetOrderList> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_ORDER_LIST;

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
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonSubParams = null;

        try {

            jsonParams.put("source",source);

            if(!TextUtils.isEmpty(userid))
                jsonParams.put("userid",userid);

            if(!TextUtils.isEmpty(storeid))
                jsonParams.put("storeid",storeid);

            jsonParams.put("selectflag",selectFlag);


            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiGetOrderList error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : "+url+" reqParams : " + reqParams);


        // set params E

        // request!
        JsonGsonRequest<RetOrderList> gsObjRequest = new JsonGsonRequest<RetOrderList>(
                Request.Method.POST,
                url,
                RetOrderList.class, header, reqParams,
                succListener, failListener

        );

        // request queue!
        ApiBase.getInstance(context).addToRequestQueue(gsObjRequest);
    }


    // apiGetOrderList
    public void apiSetPushKey(Context context, String source, String userid, String storeid, String pushKey, Response.Listener<RetCode> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_PUSHKEY_USER;

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
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonSubParams = null;

        try {

            jsonParams.put("source",source);
            jsonParams.put("pushkey",pushKey);

            if(!TextUtils.isEmpty(userid)) {
                jsonParams.put("userid", userid);
            }

            if(!TextUtils.isEmpty(storeid)) {
                url = URL_DOMAIN + URL_PUSHKEY_OWNER;
                jsonParams.put("storeid", storeid);
            }

            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiSetPushKey error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : "+url+" reqParams : " + reqParams);


        // set params E

        // request!
        JsonGsonRequest<RetCode> gsObjRequest = new JsonGsonRequest<RetCode>(
                Request.Method.POST,
                url,
                RetCode.class, header, reqParams,
                succListener, failListener

        );

        // request queue!
        ApiBase.getInstance(context).addToRequestQueue(gsObjRequest);

    }

    // apiChgOrderMenu
    public void apiChgOrderMenu(Context context,String storeID, String orderKey, String status, Response.Listener<RetCode> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_STATUS_ORDER_MENU;

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

            jsonParams.put("storeid",storeID);

            jsonParams.put("orderkey",orderKey);

            jsonParams.put("status",status);

            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiChgOrderMenu error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : "+url+" reqParams : " + reqParams);


        // set params E

        // request!
        JsonGsonRequest<RetCode> gsObjRequest = new JsonGsonRequest<RetCode>(
                Request.Method.POST,
                url,
                RetCode.class, header, reqParams,
                succListener, failListener

        );

        // request queue!
        ApiBase.getInstance(context).addToRequestQueue(gsObjRequest);

    }
    // ui Api E


}
