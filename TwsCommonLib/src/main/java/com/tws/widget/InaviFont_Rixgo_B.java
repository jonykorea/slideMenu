package com.tws.widget;

import android.content.Context;
import android.graphics.Typeface;

public class InaviFont_Rixgo_B {

	private static Typeface mTypeface;	
	private static InaviFont_Rixgo_B mInaviFont;
	
	public static InaviFont_Rixgo_B getInstance(Context context){
		
		if(mInaviFont==null){
			mInaviFont = new InaviFont_Rixgo_B(context);
		}
		return mInaviFont;
	}
	
	public InaviFont_Rixgo_B(Context context){
		mTypeface = Typeface.createFromAsset(context.getAssets(),"fonts/inavi_rixgo_b.ttf");
	}
	
	public Typeface getInaviFont(){
		return mTypeface;
	}
}
