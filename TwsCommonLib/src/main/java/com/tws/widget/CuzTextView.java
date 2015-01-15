/**
 * 
 */
package com.tws.widget;

/**
 * @author jonychoi
 *
 */

import android.content.Context;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;

public class CuzTextView extends TextView {
	
	private Context mContext;
	private AttributeSet mAttrs;
	
	public CuzTextView(Context context) {
		super(context);
		mContext = context;
		setIncludeFontPadding(false);
	}

	public CuzTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mAttrs = attrs;
		setCustomFont(context, attrs);
		setIncludeFontPadding(false);
	}

	public CuzTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		mAttrs = attrs;
		setCustomFont(context, attrs);
		setIncludeFontPadding(false);
	}

	private void setCustomFont(Context ctx, AttributeSet attrs) {

		String strSC = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "shadowColor");

		if (strSC != null) {
			setShadowLayer(1, 0, 1, Color.parseColor(strSC));
		}
		
		setTypeface(InaviFont_Rixgo_B.getInstance(ctx).getInaviFont());
	}
	
	public void setRixgoMFont(){				
		setTypeface(InaviFont_Rixgo_M.getInstance(mContext).getInaviFont());
	}
	
	public void setScrolling(){
		setMovementMethod(ScrollingMovementMethod.getInstance());
	}
}
