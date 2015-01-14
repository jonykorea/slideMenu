package com.tws.soul.soulbrown.pref;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUserInfo {

	private final String PREF_USER_INFO = "USER_INFO";

	private final String PREF_KEY_USER_ID = "USER_ID"; // user id

	private Context mContext = null;
	private SharedPreferences mPrefFrame = null;
	private SharedPreferences.Editor mEditor = null;

	public PrefUserInfo(Context context) {
		mContext = context;
		mPrefFrame = mContext.getSharedPreferences(PREF_USER_INFO,
				Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
		mEditor = mPrefFrame.edit();
	}

	public void removeAll() { mEditor.clear(); mEditor.commit(); }
	

	public void setUserID(String userID) {

		mEditor.putString(PREF_KEY_USER_ID, userID);

		mEditor.commit();
	}

	public String getUserID() {
		return mPrefFrame.getString(PREF_KEY_USER_ID, "");
	}

}
