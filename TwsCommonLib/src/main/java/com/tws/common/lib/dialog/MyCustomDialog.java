package com.tws.common.lib.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.tws.soul.twscommonlib.R;

public class MyCustomDialog extends DialogFragment {
    private Button mButton;
    private EditText mEditText;
    public onSubmitListener mListener;
    public String text = "";

    public static MyCustomDialog newInstance(int num) {
        MyCustomDialog f = new MyCustomDialog();

        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }
    int mNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        mButton = (Button) dialog.findViewById(R.id.button1);
        mEditText = (EditText) dialog.findViewById(R.id.editText1);
        mEditText.setText(text);
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.setOnSubmitListener(mEditText.getText().toString());
                dismiss();
            }
        });
        return dialog;
    }

    public interface onSubmitListener {
        void setOnSubmitListener(String arg);
    }
}