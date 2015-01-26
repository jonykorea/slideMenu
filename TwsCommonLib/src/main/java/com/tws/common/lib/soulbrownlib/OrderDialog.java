package com.tws.common.lib.soulbrownlib;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tws.common.lib.views.ButtonFlat;
import com.tws.soul.twscommonlib.R;

/**
 * Created by jonychoi on 15. 1. 13..
 */
public class OrderDialog
        extends android.app.Dialog {

    Context context;
    View view;
    View backView;
    String message;
    TextView messageTextView;
    String title;
    TextView titleTextView;
    boolean isTouchClose = false;

    ButtonFlat buttonAccept;
    ButtonFlat buttonCancel;

    TextView tvArriveTime;
    LinearLayout llArriveTimeUp;
    LinearLayout llArriveTimeDown;

    View.OnClickListener onAcceptButtonClickListener;
    View.OnClickListener onCancelButtonClickListener;


    public OrderDialog(Context context, String title, String message) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;// init Context
        this.message = message;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.soul_order_dialog);

        view = (RelativeLayout)findViewById(R.id.contentDialog);
        backView = (RelativeLayout)findViewById(R.id.dialog_rootView);
        if(isTouchClose) {
            backView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getX() < view.getLeft()
                            || event.getX() > view.getRight()
                            || event.getY() > view.getBottom()
                            || event.getY() < view.getTop()) {

                        dismiss();
                    }
                    return false;
                }
            });
        }
        this.titleTextView = (TextView) findViewById(R.id.title);
        setTitle(title);

        this.messageTextView = (TextView) findViewById(R.id.message);
        setMessage(message);

        this.buttonAccept = (ButtonFlat) findViewById(R.id.button_accept);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(onAcceptButtonClickListener != null)
                    onAcceptButtonClickListener.onClick(v);
            }
        });
        this.buttonCancel = (ButtonFlat) findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if(onCancelButtonClickListener != null)
                    onCancelButtonClickListener.onClick(v);
            }
        });

        this.tvArriveTime =  (TextView) findViewById(R.id.order_arrive_time_txt);
        this.llArriveTimeUp = (LinearLayout) findViewById(R.id.order_arrive_time_up);

        llArriveTimeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upArriveTime();
            }
        });

        this.llArriveTimeDown = (LinearLayout) findViewById(R.id.order_arrive_time_down);

        llArriveTimeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downArriveTime();
            }
        });
    }

    private void upArriveTime() {
        String textTime = (String) tvArriveTime.getText();

        textTime = textTime.replace("분","");

        int time = Integer.parseInt(textTime);

        if( time == 5)
        {
            time += 5;

        }
        else if (time < 60) {

            time += 10;

        } else
        {

        }

        tvArriveTime.setText(time+"분");

    }

    private void downArriveTime()
    {
        String textTime = (String) tvArriveTime.getText();

        textTime = textTime.replace("분","");

        int time = Integer.parseInt(textTime);

        if( time == 10 )
        {
            time -= 5;
        }
        else if (time > 10) {

            time -= 10;

        } else
        {

        }

        tvArriveTime.setText(time+"분");
    }

    public TextView getArriveTime() {
        return tvArriveTime;
    }

    public void setTvArriveTime(String time){
        tvArriveTime.setText(time);
    }

    @Override
    public void show() {

        super.show();
        // set dialog enter animations
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_main_show_amination));
        backView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_root_show_amin));
    }

    // GETERS & SETTERS

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        messageTextView.setText(message);
    }

    public TextView getMessageTextView() {
        return messageTextView;
    }

    public void setMessageTextView(TextView messageTextView) {
        this.messageTextView = messageTextView;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if(title == null)
            titleTextView.setVisibility(View.GONE);
        else{
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        }
    }

    public void setTouchClose(boolean isClose)
    {
        isTouchClose = isClose;
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }

    public ButtonFlat getButtonAccept() {
        return buttonAccept;
    }

    public void setButtonAccept(ButtonFlat buttonAccept) {
        this.buttonAccept = buttonAccept;
    }

    public ButtonFlat getButtonCancel() {
        return buttonCancel;
    }

    public void setButtonCancel(ButtonFlat buttonCancel) {
        this.buttonCancel = buttonCancel;
    }

    public void setOnAcceptButtonClickListener(
            View.OnClickListener onAcceptButtonClickListener) {
        this.onAcceptButtonClickListener = onAcceptButtonClickListener;
        if(buttonAccept != null)
            buttonAccept.setOnClickListener(onAcceptButtonClickListener);
    }

    public void setOnCancelButtonClickListener(
            View.OnClickListener onCancelButtonClickListener) {
        this.onCancelButtonClickListener = onCancelButtonClickListener;
        if(buttonCancel != null)
            buttonCancel.setOnClickListener(onAcceptButtonClickListener);
    }

    @Override
    public void dismiss() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                OrderDialog.super.dismiss();
            }
        }, 200);


    }



}


