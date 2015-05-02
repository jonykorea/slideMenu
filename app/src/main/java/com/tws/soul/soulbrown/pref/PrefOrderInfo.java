package com.tws.soul.soulbrown.pref;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefOrderInfo {

	private final String PREF_ORDER_INFO = "ORDER_INFO";

	private final String PREF_KEY_ARRIVE_TIME = "ARRIVE_TIME"; // arrive time

    private final String PREF_KEY_SETTING_TIME = "SETTING_TIME"; // setting time

    private final String PREF_KEY_ORDER_STORE = "ORDER_STORE"; // order store

    private Context mContext = null;
	private SharedPreferences mPrefOrderInfo = null;
	private SharedPreferences.Editor mEditor = null;

	public PrefOrderInfo(Context context) {
		mContext = context;
        mPrefOrderInfo = mContext.getSharedPreferences(PREF_ORDER_INFO,
				Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
		mEditor = mPrefOrderInfo.edit();
	}

	public void removeAll() { mEditor.clear(); mEditor.commit(); }


	public void setArriveTime(long time) {

		mEditor.putLong(PREF_KEY_ARRIVE_TIME, time);

		mEditor.commit();
	}

	public long getArriveTime() {
		return mPrefOrderInfo.getLong(PREF_KEY_ARRIVE_TIME, -1);
	}

    public void setSettingTime(String time) {

        mEditor.putString(PREF_KEY_SETTING_TIME, time);

        mEditor.commit();
    }

    public String getSettingTime() {
        return mPrefOrderInfo.getString(PREF_KEY_SETTING_TIME, "10분 후");
    }

    public void setOrderStore(String storeID) {

        mEditor.putString(PREF_KEY_ORDER_STORE, storeID);

        mEditor.commit();
    }

    public String getOrderStore() {
        return mPrefOrderInfo.getString(PREF_KEY_ORDER_STORE, "");
    }

}
