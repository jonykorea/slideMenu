/**
 * 
 */
package com.tws.widget;

/**
 * @author jonychoi
 *
 */

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public class CuzImageButton extends ImageButton {

	private boolean mClick = false;

	public CuzImageButton(Context context) {
		super(context);
	}

	public CuzImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public CuzImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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
					mClick = true;
					l.onClick(v);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mClick = false;
						}
					}, 500);
				}
			}
		});
	}

}
