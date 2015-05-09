package com.brewbrew.managers.pref;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefStoreInfo {

	private final String PREF_STORE_INFO= "STORE_INFO";

	private final String PREF_KEY_PUSH_STATUS = "PUSH_STATUS"; // push status

    private Context mContext = null;
	private SharedPreferences mPrefStoreInfo = null;
	private SharedPreferences.Editor mEditor = null;

	public PrefStoreInfo(Context context) {
		mContext = context;
        mPrefStoreInfo = mContext.getSharedPreferences(PREF_STORE_INFO,
				Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
		mEditor = mPrefStoreInfo.edit();
	}

	public void removeAll() { mEditor.clear(); mEditor.commit(); }


	public void setPushStatus(int status) {

		mEditor.putInt(PREF_KEY_PUSH_STATUS, status);

		mEditor.commit();
	}

	public int getPushStatus() {
		return mPrefStoreInfo.getInt(PREF_KEY_PUSH_STATUS, 0);
	}



}
