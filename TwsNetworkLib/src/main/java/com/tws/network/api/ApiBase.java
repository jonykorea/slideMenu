package com.tws.network.api;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by jonychoi on 15. 1. 29..
 */
public class ApiBase {

    public static final String TAG = ApiBase.class
            .getSimpleName();

    private volatile static ApiBase mApi;
    private final RequestQueue mRequestQueue;

    protected ApiBase(Context context) {
        this.mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }
    public static ApiBase getInstance(Context context)
    {
        if( mApi == null )
        {
            synchronized (ApiBase.class)
            {
                if( mApi == null )
                {
                    mApi = new ApiBase(context);
                }
            }
        }
        return mApi;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        this.mRequestQueue.add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        this.mRequestQueue.add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        }

        return null;
    }
}