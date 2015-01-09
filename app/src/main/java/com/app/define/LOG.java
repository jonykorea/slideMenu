package com.app.define;

/**
 * LOG info setting
 * DEBUG : true / false;
 * @author jonychoi
 *
 */
public class LOG {

    public static final String TAG = "SoulBrown";
    public static final boolean DEBUG = true;

    public static void d(String msg) {
    	if( DEBUG )
    		android.util.Log.d(TAG, msg);
    }

}
