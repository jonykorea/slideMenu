package com.brewbrew.views.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;

import com.brewbrew.R;

/**
 * Created by jony on 15. 6. 3..
 */
public class LoadingDialog extends Dialog
{
    private OnEventListener mEvtListener   = null;

    public static final int EVENT_KEY_BACK = 1;

    public LoadingDialog(Context context)
    {
        super(context, R.style.Dialog);

        mEvtListener = null;
        setContentView(R.layout.dialog_loading);
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (mEvtListener != null)
            {
                mEvtListener.onEvent(EVENT_KEY_BACK, null);
            }
            dismiss();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void setEventListener(OnEventListener evtListener)
    {
        mEvtListener = evtListener;
    }

    public interface OnEventListener
    {
        public void onEvent(int evtType, Object o);
    }
}
