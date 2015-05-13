package com.brewbrew.network.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.app.define.LOG;
import com.brewbrew.network.data.RetCode;
import com.brewbrew.network.data.RetPushMsgStatus;
import com.brewbrew.network.util.CommonParams;
import com.brewbrew.network.util.DeviceInfo;
import com.flurry.android.FlurryAgent;
import com.tws.network.api.ApiBase;
import com.brewbrew.network.data.CoreGetPublicKey;
import com.brewbrew.network.data.RetMenuList;
import com.brewbrew.network.data.RetOrderList;
import com.brewbrew.network.data.RetOrderMenu;
import com.brewbrew.network.data.RetUserChecker;
import com.tws.network.lib.AirGsonRequest;
import com.tws.network.lib.JsonGsonRequest;
import com.brewbrew.managers.data.Menu;
import com.brewbrew.flurry.Define;
import com.brewbrew.google.gcm.GcmClient;
import com.brewbrew.managers.lib.GPSUtils;
import com.brewbrew.managers.lib.StoreInfo;
import com.brewbrew.managers.pref.PrefOrderInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jonychoi on 14. 11. 21..
 */
public class ApiAgent {

    private static final String TAG = "VolleyRequest";

    // into Domain
    //private static final String URL_DOMAIN = "https://airagt.inavi.com";
    //private static final String URL_DOMAIN = "http://107.191.60.183:8020";
    private static final String URL_DOMAIN = "http://api.brewbrew.co.kr:8020";


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


    // COMMON
    private static final String URL_COMMON_CHECKER = "/bb/if/user/storeuserchecker";

    // USER
    private static final String URL_USER_REG_UESERKEY = "/bb/if/user/reguserkey";

    private static final String URL_USER_GET_MENULIST = "/bb/if/user/getmenulist";

    private static final String URL_USER_ORDER_MENU = "/bb/if/user/ordermenu";

    private static final String URL_USER_GET_ORDERLIST = "/bb/if/user/getorderlist";

    private static final String URL_USER_SET_USERLOC = "/bb/if/user/setuserloc";

    private static final String URL_USER_CHG_ORDERSTATUS = "/bb/if/user/chgorderstatus";

    private static final String URL_USER_LOGOUT = "/bb/if/user/logout";


    // STORE
    private static final String URL_STORE_GET_STATUS = "/bb/if/store/getmystatus";

    private static final String URL_STORE_SET_STOREON = "/bb/if/store/setstoreon";

    private static final String URL_STORE_GET_ORDERLIST = "/bb/if/store/getorderlist";

    private static final String URL_STORE_CHG_ORDERSTATUS = "/bb/if/store/chgorderstatus";

    private static final String URL_STORE_LOGOUT = "/bb/if/store/logout";

    private static final String URL_STORE_REG_MENU_EVENT = "/bb/if/store/regmenuevt";


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

        LOG.d("url : " + url + " reqParams : " + reqParams);


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
    public void apiUserLoc(Context context, String userid, String lon, String lat, Response.Listener<RetCode> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_USER_SET_USERLOC;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();

        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
            header.put("auth", auth);

        header.put("mdn", DeviceInfo.getMDN(context));

        // set add header E

        float distance = GPSUtils.getDistance(Double.parseDouble(lat), Double.parseDouble(lon), StoreInfo.STORE_LATI, StoreInfo.STORE_LON);

        // set params S
        JSONObject jsonParams = new JSONObject();
        try {

            //jsonParams.put("source","USERUI");
            //jsonParams.put("userid",userid);
            //jsonParams.put("lon",lon);
            //jsonParams.put("lat",lat);
            //jsonParams.put("distance",GPSUtils.getDistanceStr(distance));
            PrefOrderInfo prefOrderInfo = new PrefOrderInfo(context);
            String storeID = prefOrderInfo.getOrderStore();

            jsonParams.put("store", storeID);
            jsonParams.put("status", (int)distance);

            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiUserLoc error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : " + url + " reqParams : " + reqParams);


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
    public void apiUserChecker(Context context, String userid, Response.Listener<RetUserChecker> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_COMMON_CHECKER;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();

        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
            header.put("auth", auth);

        header.put("mdn", DeviceInfo.getMDN(context));

        // set add header E

        // set params S
        JSONObject jsonParams = new JSONObject();
        try {

            jsonParams.put("nick", userid);

            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiUserChecker error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : " + url + " reqParams : " + reqParams);


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

    // apiGetMenuList
    public void apiGetMenuList(Context context, String storeid, Response.Listener<RetMenuList> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_USER_GET_MENULIST;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();

        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
            header.put("auth", auth);

        header.put("mdn", DeviceInfo.getMDN(context));

        // set add header E

        // set params S
        JSONObject jsonParams = new JSONObject();
        try {

            jsonParams.put("store", storeid);

            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiGetMenuList error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : " + url + " reqParams : " + reqParams);


        // set params E

        // request!
        JsonGsonRequest<RetMenuList> gsObjRequest = new JsonGsonRequest<RetMenuList>(
                Request.Method.POST,
                url,
                RetMenuList.class, header, reqParams,
                succListener, failListener

        );

        // request queue!
        ApiBase.getInstance(context).addToRequestQueue(gsObjRequest);

    }
    // apiOrderMenu
    public void apiOrderMenu(Context context, String userid, String storeid, String arriveTime, List<Menu> listMenu, boolean reOrder , Response.Listener<RetOrderMenu> succListener, Response.ErrorListener failListener) {

        // flurry S
        Map<String, String> flurryParams = new HashMap<String, String>();

        flurryParams.put(Define.LOG_KEY_STORE, storeid);

        long arrTime = Long.parseLong(arriveTime) - (System.currentTimeMillis()/1000 );
        arrTime = arrTime / 60;

        flurryParams.put(Define.LOG_KEY_ARRIVAL_TIME, Long.toString(arrTime));

        if( reOrder )
            FlurryAgent.logEvent(Define.LOG_VIEW_REORDER, flurryParams);
        else
            FlurryAgent.logEvent(Define.LOG_VIEW_ORDER, flurryParams);
        // flurry E
        String url = URL_DOMAIN + URL_USER_ORDER_MENU;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();

        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
            header.put("auth", auth);

        header.put("mdn", DeviceInfo.getMDN(context));

        // set add header E

        // set params S
        JSONObject jsonParams = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonSubParams = null;

        JSONArray jsonArrayOption = new JSONArray();
        JSONObject jsonSubOptionParams = null;

        try {


            jsonParams.put("store", storeid);
            jsonParams.put("arrtime", arriveTime);

            if (listMenu != null) {
                for (int i = 0; i < listMenu.size(); i++) {

                   jsonSubParams = new JSONObject();

                    Menu menu = listMenu.get(i);

                    if (menu != null) {

                        int totalPrice = 0;
                        int totalCnt = 0;
                        int salePrice = menu.saleprice;

                        if (menu.option != null && menu.option.size() > 0) {


                            jsonArrayOption = new JSONArray();

                            for (int j = 0; j < menu.option.size(); j++) {

                                jsonSubOptionParams = new JSONObject();


                                int cnt = menu.option.get(j).count;

                                if(cnt != 0) {
                                    jsonSubOptionParams.put("code", menu.option.get(j).code);
                                    jsonSubOptionParams.put("count", cnt);

                                    jsonArrayOption.put(jsonSubOptionParams);

                                    totalPrice += (salePrice + menu.option.get(j).addprice) * cnt;

                                    totalCnt += cnt;
                                }
                            }

                            jsonSubParams.put("option", jsonArrayOption);
                        }


                        if( totalCnt != 0) {
                            jsonSubParams.put("code", menu.code);

                            jsonSubParams.put("totalprice", totalPrice);
                            jsonSubParams.put("totalcount", totalCnt);

                            jsonArray.put(jsonSubParams);
                        }


                    }
                }
                jsonParams.put("order", jsonArray);

            }
            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiOrderMenu error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : " + url + " reqParams : " + reqParams);


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
    public void apiGetOrderList(Context context, String source, String userid, String storeid, int flag, Response.Listener<RetOrderList> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_USER_GET_ORDERLIST;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();

        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
            header.put("auth", auth);

        header.put("mdn", DeviceInfo.getMDN(context));

        // set add header E

        // set params S
        JSONObject jsonParams = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonSubParams = null;

        if (!TextUtils.isEmpty(userid)) {
            url = URL_DOMAIN + URL_USER_GET_ORDERLIST;
        }

        if (!TextUtils.isEmpty(storeid)) {
            url = URL_DOMAIN + URL_STORE_GET_ORDERLIST;
            try {

                jsonParams.put("store", storeid);
                jsonParams.put("flag", flag);

            } catch (Exception e) {
                LOG.d("apiGetOrderList error:" + e.getMessage());
                jsonParams = null;
            }
        }


        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : " + url + " reqParams : " + reqParams);


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


    // apiSetPushKeyUser
    public void apiSetPushKeyUser(Context context, String nick, String pushKey, Response.Listener<RetCode> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_USER_REG_UESERKEY;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();

        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
            header.put("auth", auth);

        header.put("mdn", DeviceInfo.getMDN(context));

        // set add header E

        // set params S
        JSONObject jsonParams = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonSubParams = null;

        try {

            // param info
            // os  android : 1
            jsonParams.put("os", 1);
            jsonParams.put("pushkey", pushKey);

            // user
            if (!TextUtils.isEmpty(nick)) {
                jsonParams.put("nick", nick);
            }


        } catch (Exception e) {
            LOG.d("apiSetPushKey error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : " + url + " reqParams : " + reqParams);


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

    // apiSetPushKeyUser
    public void apiSetPushKeyStore(Context context, String store, String pushKey, Response.Listener<RetCode> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_STORE_SET_STOREON;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();

        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
            header.put("auth", auth);

        header.put("mdn", DeviceInfo.getMDN(context));

        // set add header E

        // set params S
        JSONObject jsonParams = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonSubParams = null;

        try {

            // param info
            // os  android : 1

            if( TextUtils.isEmpty(pushKey))
            {
                jsonParams.put("os", 0);
            }
            else
            {
                jsonParams.put("os", 1);
            }

            jsonParams.put("pushkey", pushKey);

            // user
            if (!TextUtils.isEmpty(store)) {
                jsonParams.put("store", store);
            }

        } catch (Exception e) {
            LOG.d("apiSetPushKey error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : " + url + " reqParams : " + reqParams);


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
    public void apiChgOrderMenu(Context context, String orderKey, int status, Response.Listener<RetCode> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_USER_CHG_ORDERSTATUS;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();

        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
            header.put("auth", auth);

        header.put("mdn", DeviceInfo.getMDN(context));

        // set add header E

        // set params S
        JSONObject jsonParams = new JSONObject();
        try {

            jsonParams.put("orderkey", orderKey);

            jsonParams.put("status", status);

            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiChgOrderMenu error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : " + url + " reqParams : " + reqParams);


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
    public void apiChgOrderMenu(Context context, String storeID, String orderKey, int status, Response.Listener<RetCode> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_STORE_CHG_ORDERSTATUS;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();

        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
            header.put("auth", auth);

        header.put("mdn", DeviceInfo.getMDN(context));

        // set add header E

        // set params S
        JSONObject jsonParams = new JSONObject();
        try {


            jsonParams.put("store", storeID);

            jsonParams.put("orderkey", orderKey);

            jsonParams.put("status", status);

            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiChgOrderMenu error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : " + url + " reqParams : " + reqParams);


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

    public void apiLogout(Context context, String userid, String storeid, String pushKey, Response.Listener<RetCode> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_USER_LOGOUT;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();

        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
            header.put("auth", auth);

        header.put("mdn", DeviceInfo.getMDN(context));

        // set add header E

        // set params S
        JSONObject jsonParams = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonSubParams = null;

        try {

            // param info
            // os  android : 1
            //jsonParams.put("os",1);
            //jsonParams.put("pushkey",pushKey);

            // user
            if (!TextUtils.isEmpty(userid)) {
                jsonParams.put("nick", userid);
            }

            if (!TextUtils.isEmpty(storeid)) {
                url = URL_DOMAIN + URL_STORE_LOGOUT;
                jsonParams.put("store", storeid);
            }

            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiSetPushKey error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : " + url + " reqParams : " + reqParams);


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
    public void apiGetPushMsgStatus(Context context, String storeID, Response.Listener<RetPushMsgStatus> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_STORE_GET_STATUS;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();

        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
            header.put("auth", auth);

        header.put("mdn", DeviceInfo.getMDN(context));

        // set add header E

        // set params S
        JSONObject jsonParams = new JSONObject();
        try {


            jsonParams.put("store", storeID);

            String pushKey = "";
            SharedPreferences prefs = GcmClient.getGcmPreferences(context);
            String registrationId = prefs.getString(GcmClient.PROPERTY_REG_ID, "");
            if (!registrationId.isEmpty()) {
                pushKey = registrationId;
            }
            jsonParams.put("pushkey", pushKey);

            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiGetPushMsgStatus error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : " + url + " reqParams : " + reqParams);


        // set params E

        // request!
        JsonGsonRequest<RetPushMsgStatus> gsObjRequest = new JsonGsonRequest<RetPushMsgStatus>(
                Request.Method.POST,
                url,
                RetPushMsgStatus.class, header, reqParams,
                succListener, failListener

        );

        // request queue!
        ApiBase.getInstance(context).addToRequestQueue(gsObjRequest);

    }

    // apiRegMenuEvent
    public void apiRegMenuEvent(Context context, String store, String code, String name,
                                long evtstime, long evtetime, int evtcount, int price, int saleprice, Response.Listener<RetPushMsgStatus> succListener, Response.ErrorListener failListener) {

        String url = URL_DOMAIN + URL_STORE_REG_MENU_EVENT;

        // set add header S
        HashMap<String, String> header = new HashMap<String, String>();

        String auth = DeviceInfo.getAuth(context);

        if (!TextUtils.isEmpty(auth))
            header.put("auth", auth);

        header.put("mdn", DeviceInfo.getMDN(context));

        // set add header E

        // set params S
        JSONObject jsonParams = new JSONObject();
        try {


            jsonParams.put("store", store);
            jsonParams.put("code", code);
            jsonParams.put("name", name);
            jsonParams.put("evtstime", evtstime);
            jsonParams.put("evtetime", evtetime);
            jsonParams.put("evtcount", evtcount);
            jsonParams.put("price", price);
            jsonParams.put("saleprice", saleprice);

            //jsonParams = CommonParams.getCommonParams(context, jsonParams);

        } catch (Exception e) {
            LOG.d("apiGetPushMsgStatus error:" + e.getMessage());
            jsonParams = null;
        }

        String reqParams = null;

        if (jsonParams != null)
            reqParams = jsonParams.toString();

        LOG.d("url : " + url + " reqParams : " + reqParams);


        // set params E

        // request!
        JsonGsonRequest<RetPushMsgStatus> gsObjRequest = new JsonGsonRequest<RetPushMsgStatus>(
                Request.Method.POST,
                url,
                RetPushMsgStatus.class, header, reqParams,
                succListener, failListener

        );

        // request queue!
        ApiBase.getInstance(context).addToRequestQueue(gsObjRequest);

    }



    // ui Api E



}
