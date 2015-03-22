package com.tws.widget;

import android.content.Context;
import android.graphics.Typeface;

public class InaviFont_Rixgo_M {

	private static Typeface mTypeface;	
	private static InaviFont_Rixgo_M mInaviFont;
	
	public static InaviFont_Rixgo_M getInstance(Context context){
		
		if(mInaviFont==null){
			mInaviFont = new InaviFont_Rixgo_M(context);
		}
		return mInaviFont;
	}
	
	public InaviFont_Rixgo_M(Context context){
		//mTypeface = Typeface.createFromAsset(context.getAssets(),"fonts/inavi_rixgo_m.ttf");
        mTypeface = Typeface.createFromAsset(context.getAssets(),"fonts/BM-JUA.ttf");
	}
	
	public Typeface getInaviFont(){
		return mTypeface;
	}
}
