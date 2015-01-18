package com.tws.soul.soulbrown.base;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tws.soul.soulbrown.R;
import android.support.v4.app.Fragment;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    public SweetAlertDialog mBaseProgressDialog;

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
        mBaseProgressDialog.setTitleText("데이터를 불러오고 있습니다.");
        mBaseProgressDialog.setCancelable(false);

    }

}
