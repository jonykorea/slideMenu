package com.tws.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SlidingDrawer;

public class CuzSlidingDrawer extends SlidingDrawer 
{
        private boolean locked = false;
        
        public CuzSlidingDrawer(Context context) 
        {
                super(context, null, 0);
        }
        
        public CuzSlidingDrawer(Context context, AttributeSet attrs) 
        {
                super(context, attrs, 0);
        }
        
        public CuzSlidingDrawer(Context context, AttributeSet attrs, int defStyle) 
        {
                super(context, attrs, defStyle);
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event) 
        {
                if (this.locked)
                {
                        return false;
                }
                
                return super.onTouchEvent(event);
        }
        
        public void setLocked(boolean locked) 
        {
                this.locked = locked;
        }
        
        public boolean isLocked() 
        {
                return locked;
        }
}
