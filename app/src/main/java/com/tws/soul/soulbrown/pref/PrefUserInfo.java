package com.tws.soul.soulbrown.pref;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUserInfo {

	private final String PREF_USER_INFO = "USER_INFO";

	private final String PREF_KEY_USER_ID = "USER_ID"; // user id

    private final String PREF_KEY_USER_TYPE = "USER_TYPE"; // user type 사용자/점주

    private final String PREF_KEY_USER_NAME = "USER_NAME"; // store name

	private Context mContext = null;
	private SharedPreferences mPrefUserInfo = null;
	private SharedPreferences.Editor mEditor = null;

	public PrefUserInfo(Context context) {
		mContext = context;
        mPrefUserInfo = mContext.getSharedPreferences(PREF_USER_INFO,
				Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
		mEditor = mPrefUserInfo.edit();
	}

	public void removeAll() { mEditor.clear(); mEditor.commit(); }
	

	public void setUserID(String userID) {

		mEditor.putString(PREF_KEY_USER_ID, userID);

		mEditor.commit();
	}

	public String getUserID() {
		return mPrefUserInfo.getString(PREF_KEY_USER_ID, "");
	}

    public void setUserType(boolean userType) {

        mEditor.putBoolean(PREF_KEY_USER_TYPE, userType);

        mEditor.commit();
    }

    public boolean getUserType() {
        return mPrefUserInfo.getBoolean(PREF_KEY_USER_TYPE, true);
    }

    public void setUserName(String userName) {

        mEditor.putString(PREF_KEY_USER_NAME, userName);

        mEditor.commit();
    }

    public String getUserName() {
        return mPrefUserInfo.getString(PREF_KEY_USER_NAME, "");
    }

}
