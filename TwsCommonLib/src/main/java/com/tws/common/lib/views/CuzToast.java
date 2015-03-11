package com.tws.common.lib.views;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tws.soul.twscommonlib.R;

/**
 * Created by Jony on 2015-03-11.
 */

public class CuzToast extends Toast {
    Context mContext;
    public CuzToast(Context context) {
        super(context);
        mContext = context;
    }

    public void showToast(String body, int duration){

        LayoutInflater inflater;
        View view;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.cuz_toast, null);
        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(body);

        show(this,view,duration);
    }

    private void show(Toast toast, View v, int duration){
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(v);
        toast.show();
    }

}

