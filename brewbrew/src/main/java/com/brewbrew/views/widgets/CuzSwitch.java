package com.brewbrew.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.gc.materialdesign.views.Switch;

/**
 * Created by jony on 15. 6. 3..
 */
public class CuzSwitch extends Switch {

    public CuzSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return false;
    }


}
