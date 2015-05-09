package com.brewbrew.activities.base;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tws.common.lib.dialog.CuzDialog;
import com.brewbrew.R;
import android.support.v4.app.Fragment;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    public SweetAlertDialog mBaseProgressDialog;

    public CuzDialog mBaseDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseInitProgresDialog();
    }

    public BaseFragment() {
        // Required empty public constructor
    }

    private void baseInitProgresDialog()
    {
        mBaseProgressDialog  = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        mBaseProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#2996CC"));
        mBaseProgressDialog.setTitleText("Loading");
        mBaseProgressDialog.setCancelable(false);

    }

}
