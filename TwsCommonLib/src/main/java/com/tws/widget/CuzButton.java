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
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
public class CuzButton extends Button {
	
	private boolean mClick = false;
	private int delayTime = 500;
	
	public CuzButton(Context context) {
		super(context);

		setIncludeFontPadding(false);
	}

	public CuzButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomFont(context, attrs);
		setIncludeFontPadding(false);
		
	}

	public CuzButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCustomFont(context, attrs);
		setIncludeFontPadding(false);
	}

	private void setCustomFont(Context ctx, AttributeSet attrs) {

		String strSC = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "shadowColor");

		if (strSC != null) {
			setShadowLayer(1, 0, 1, Color.parseColor(strSC));
		}
		
		super.setTypeface(InaviFont_Rixgo_B.getInstance(ctx).getInaviFont());
	}
		
	// 버튼 연속적으로 클릭시 두번 실행되는 문제.
	@Override
	public void setOnClickListener(final OnClickListener l) {
		// TODO Auto-generated method stub
		super.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub			
				if( !mClick ) {		
//					LOG.d("[CuzButton] onClick() : " + delayTime);
					l.onClick(v);
					if(delayTime>0){
//						LOG.d("[CuzButton] onClick() delay...");
						mClick = true;
						new Handler().postDelayed(new Runnable() {						
							@Override
							public void run() {
								// TODO Auto-generated method stub
								
								mClick = false;
							}
						}, delayTime);
					}
				}
			}
		});
	}

	/**
	 * @param delayTime the delayTime to set
	 */
	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}	
}
