package com.brewbrew.views.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ButtonFlat;

/**
 * Created by jony on 15. 6. 3..
 */
public class CuzView extends RelativeLayout {
    static final String MATERIALDESIGNXML = "http://schemas.android.com/apk/res-auto";
    static final String ANDROIDXML = "http://schemas.android.com/apk/res/android";
    final int disabledBackgroundColor = Color.parseColor("#E2E2E2");
    int beforeBackground;
    public boolean isLastTouch = false;
    boolean animation = false;

    public CuzView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(enabled) {
            this.setBackgroundColor(this.beforeBackground);
        } else {
            this.setBackgroundColor(this.disabledBackgroundColor);
        }

        this.invalidate();
    }

    protected void onAnimationStart() {
        super.onAnimationStart();
        this.animation = true;
    }

    protected void onAnimationEnd() {
        super.onAnimationEnd();
        this.animation = false;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(this.animation) {
            this.invalidate();
        }

    }
}
