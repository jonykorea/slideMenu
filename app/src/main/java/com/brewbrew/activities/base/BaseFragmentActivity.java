package com.brewbrew.activities.base;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.tws.common.lib.dialog.CuzDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Jony on 2015-01-18.
 */
public class BaseFragmentActivity extends FragmentActivity {

    public SweetAlertDialog mBaseProgressDialog;

    public CuzDialog mBaseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseInitProgresDialog();
    }

    private void baseInitProgresDialog()
    {
        mBaseProgressDialog  = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mBaseProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#2996CC"));
        mBaseProgressDialog.setTitleText("Loading");
        mBaseProgressDialog.setCancelable(false);

    }
}
