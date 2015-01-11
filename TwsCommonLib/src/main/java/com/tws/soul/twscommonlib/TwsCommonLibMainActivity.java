package com.tws.soul.twscommonlib;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tws.common.lib.dialog.MyCustomDialog;
import com.tws.common.lib.dialog.MyCustomDialog.onSubmitListener;

public class TwsCommonLibMainActivity extends Activity implements onSubmitListener {
    TextView mTextView;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tws_common_lib_main);
        mTextView = (TextView) findViewById(R.id.textView1);
        mButton = (Button) findViewById(R.id.button1);
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyCustomDialog fragment1 = new MyCustomDialog();
                fragment1.mListener = TwsCommonLibMainActivity.this;
                fragment1.text = mTextView.getText().toString();
                fragment1.show(getFragmentManager(),"");

            }
        });


    }


    @Override
    public void setOnSubmitListener(String arg) {
        mTextView.setText(arg);
    }
}
