package com.tws.soul.soulbrown.base;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Jony on 2015-01-18.
 */
public class BaseActivity extends Activity {

    public SweetAlertDialog mBaseProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseInitProgresDialog();
    }

    private void baseInitProgresDialog()
    {
        mBaseProgressDialog  = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mBaseProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#2996CC"));
        mBaseProgressDialog.setTitleText("데이터를 불러오고 있습니다.");
        mBaseProgressDialog.setCancelable(false);

    }
}
