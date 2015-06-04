package com.brewbrew.views.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.brewbrew.R;
import com.brewbrew.views.layout.SlidingLayer;

/**
 * Created by jony on 15. 4. 21..
 */
public class CuzSlidingDialog extends Dialog {


    private SlidingLayer mSlidingLayer;
    private ImageView mIvSlidingLayer;

    private final int COLOR_MAX = 183;
    private final int COLOR_RED = 0;
    private final int COLOR_GREEN = 0;
    private final int COLOR_BLUE = 0;

    private int mHeight = 0;

    private String mImageUrl;


    public CuzSlidingDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_sliding);

        initSlidingState();

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    closeSliding();
                }
                return true;
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                closeSliding();
            }
        });

    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        openSliding(this.mImageUrl);


    }

    private void openSliding(String url) {
        if (mSlidingLayer.isClosed() && mClosedSlidingState) {


            if (TextUtils.isEmpty(url)) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIvSlidingLayer.setImageResource(R.drawable.dummy_cafeimg);
                        backgroundFadeIn();
                        mSlidingLayer.openLayer(true);
                    }
                }, 20);
            } else {

                // network


            /*

            Glide.with(this).load(url)
                    //.bitmapTransform(new RoundedCornersTransformation(Glide.get(this).getBitmapPool(), 30, 0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {

                            Log.i("jony","openSliding end");

                            if( mNavigationDrawerFragment.isDrawerOpen())
                                return;

                            mIvSlidingLayer.setImageDrawable(resource);

                            //backgroundFadeIn();

                            mSlidingLayer.openLayer(true);

                        }
                    });

            */

            }


        }
    }


    private void closeSliding() {
        if (mSlidingLayer.isOpened() && mOpenedSlidingState) {


            mSlidingLayer.closeLayer(true);
        }
    }

    boolean mClosedSlidingState = true;
    boolean mOpenedSlidingState = false;

    private void initSlidingState() {

        //mllScreenBackground = (LinearLayout) findViewById(R.id.screen_background);

        mSlidingLayer = (SlidingLayer) findViewById(R.id.slidinglayer);
        mIvSlidingLayer = (ImageView) findViewById(R.id.slidinglayer_image);

        mSlidingLayer.setStickTo(SlidingLayer.STICK_TO_BOTTOM);

        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mSlidingLayer.getLayoutParams();
        rlp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        rlp.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        mSlidingLayer.setLayoutParams(rlp);

        mSlidingLayer.setShadowSize(0);
        mSlidingLayer.setShadowDrawable(null);

        mSlidingLayer.setOffsetDistance(0);
        mSlidingLayer.setPreviewOffsetDistance(-1);

        mSlidingLayer.setOnInteractListener(new SlidingLayer.OnInteractListener() {
            @Override
            public void onOpen() {


            }

            @Override
            public void onShowPreview() {

            }

            @Override
            public void onClose() {

                backgroundFadeOut();

            }

            @Override
            public void onOpened() {

                mClosedSlidingState = false;
                mOpenedSlidingState = true;

            }

            @Override
            public void onPreviewShowed() {

            }

            @Override
            public void onClosed() {

                mClosedSlidingState = true;
                mOpenedSlidingState = false;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 20);


            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        mHeight = displayMetrics.heightPixels;

        mSlidingLayer.setOnScrollListener(new SlidingLayer.OnScrollListener() {
            @Override
            public void onScroll(int absoluteScroll) {

                int colorValue = COLOR_MAX * absoluteScroll;

                colorValue = colorValue / mHeight;

                getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(colorValue, COLOR_RED, COLOR_GREEN, COLOR_BLUE)));


            }
        });

    }




    //LinearLayout mllScreenBackground;
    private void backgroundFadeIn() {




        /*
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(mllScreenBackground, "backgroundColor", new ArgbEvaluator(), Color.argb(0,255,255,255), 0x99000000);
        objectAnimator.setDuration(500);
        objectAnimator.start();
        */
/*
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mllScreenBackground.setBackgroundColor(Color.argb(153, 0, 0, 0));
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f,1.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);

        getWindow().getAttributes().anit.startAnimation(alphaAnimation);
        */

    }

    private void backgroundFadeOut() {
        /*
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(mllScreenBackground, "backgroundColor", new ArgbEvaluator(), Color.argb(153,0,0,0), 0x00000000);
        objectAnimator.setDuratin(500);
        objectAnimator.start();
        */
/*
        mllScreenBackground.setBackgroundColor(Color.argb(153,0,0,0));
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);

        mllScreenBackground.startAnimation(alphaAnimation);
        */
    }

}
