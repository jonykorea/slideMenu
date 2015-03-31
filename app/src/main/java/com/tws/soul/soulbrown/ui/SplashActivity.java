package com.tws.soul.soulbrown.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.AppController;
import com.app.define.LOG;
import com.flurry.android.FlurryAgent;
import com.tws.common.lib.dialog.CuzDialog;
import com.tws.common.lib.views.CuzToast;
import com.tws.network.data.ExtraType;
import com.tws.network.data.RetUserChecker;
import com.tws.network.lib.ApiAgent;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.base.BaseActivity;
import com.tws.soul.soulbrown.data.Const;
import com.tws.soul.soulbrown.pref.PrefUserInfo;

/**
 * Created by jonychoi on 15. 1. 14..
 */
public class SplashActivity extends BaseActivity implements TextView.OnEditorActionListener {

    private static int ACT_RESULT_CODE = 100;
    private Context context;
    private LinearLayout llLoginLayout;
    private EditText etLoginInput;
    private Button btLoginID;

    private CuzToast mCuzToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        context = getApplicationContext();

        mCuzToast = new CuzToast(this);

        //CuzToast cuzToast = new CuzToast(context);
        //cuzToast.showToast("Test",Toast.LENGTH_SHORT);

        llLoginLayout = (LinearLayout) findViewById(R.id.splash_login_layout);
        etLoginInput = (EditText) findViewById(R.id.splash_login_edit);
        etLoginInput.setOnEditorActionListener(this);

        btLoginID = (Button) findViewById(R.id.splash_login_btn);

        btLoginID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (etLoginInput.getText().toString().length() == 0) {
                    showHideLogin(true);
                    //Toast.makeText(SplashActivity.this, getString(R.string.check_nickname), Toast.LENGTH_SHORT).show();
                    mCuzToast.showToast( getString(R.string.check_nickname),Toast.LENGTH_SHORT);
                } else {
                    showHideLogin(false);
                    String userID = etLoginInput.getText().toString();

                    apiUserChecker(userID);
                }


            }
        });

        initData();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //오버라이드한 onEditorAction() 메소드

        if(v.getId()==R.id.splash_login_edit && actionId== EditorInfo.IME_ACTION_DONE){

            if (etLoginInput.getText().toString().length() == 0) {
                showHideLogin(true);
                //Toast.makeText(SplashActivity.this, getString(R.string.check_nickname), Toast.LENGTH_SHORT).show();
                mCuzToast.showToast( getString(R.string.check_nickname),Toast.LENGTH_SHORT);
            } else {
                showHideLogin(false);
                String userID = etLoginInput.getText().toString();

                apiUserChecker(userID);
            }

        }

        return false;
    }


    private void initData() {
        // pref 확인 > 로그인 처리.

        PrefUserInfo prefUserInfo = new PrefUserInfo(context);

        String userID = prefUserInfo.getUserID();

        if (TextUtils.isEmpty(userID)) {
            // 없으면 입력창 노출.
            showHideLogin(true);

        } else {
            showHideLogin(false);


            // 있다면 api 호출.
            apiUserChecker(userID);

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        initData();
    }

    // apiUserChecker
    public void apiUserChecker(final String userID) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiUserChecker");

        if (api != null) {

            api.apiUserChecker(this, userID, new Response.Listener<RetUserChecker>() {

                @Override
                public void onResponse(RetUserChecker retCode) {

                    LOG.d("retCode.result : " + retCode.ret);
                    LOG.d("retCode.errormsg : " + retCode.msg);
                    LOG.d("retCode.usertype : " + retCode.type);
                    LOG.d("retCode.appver : " + retCode.appver);
                    LOG.d("retCode.appurl : " + retCode.appurl);


                    if (retCode.ret == 1) {

                        // version check!
                        String version = getVersionName(context);

                        LOG.d("apiUserChecker version : "+ version);

                        if( version.equals(retCode.appver))
                        {
                            // 버젼정보 같을 경우
                            // success

                            if (retCode.type == Const.USER || retCode.type == Const.OWNER) {

                                LOG.d("apiUserChecker Succ");

                                PrefUserInfo prefUserInfo = new PrefUserInfo(SplashActivity.this);

                                prefUserInfo.setUserID(userID);

                                if (retCode.type == Const.USER) {
                                    prefUserInfo.setUserType(true);
                                } else {
                                    prefUserInfo.setUserType(false);
                                }

                                Intent intent = new Intent(context, SoulBrownMainActivity.class);
                                intent.putExtra(ExtraType.USER_TYPE, retCode.type);
                                startActivityForResult(intent, ACT_RESULT_CODE);
                            } else {
                                showHideLogin(true);
                                //Toast.makeText(SplashActivity.this, getString(R.string.check_user), Toast.LENGTH_SHORT).show();
                                mCuzToast.showToast( getString(R.string.check_user),Toast.LENGTH_SHORT);
                            }

                        }
                        else
                        {
                            // 버젼정보 다를 경우
                            final String url = retCode.appurl;

                            // 팝업 노출

                            if(mBaseDialog == null || !mBaseDialog.isShowing()) {


                                mBaseDialog = new CuzDialog(SplashActivity.this,
                                        getString(R.string.confirm), getString(R.string.new_version));

                                mBaseDialog.show();

                                mBaseDialog.setCancelable(false);

                                mBaseDialog.getButtonCancel().setVisibility(View.GONE);
                                mBaseDialog.getButtonAccept().setText(getString(R.string.confirm));

                                mBaseDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        mBaseDialog.dismiss();

                                        callBrowser(url);


                                    }
                                });
                            }

                        }




                    } else {
                        showHideLogin(true);
                        // fail
                        LOG.d("apiUserChecker Fail " + retCode.ret);

                        //Toast.makeText(SplashActivity.this, retCode.msg + "(" + retCode.ret + ")", Toast.LENGTH_SHORT).show();
                        mCuzToast.showToast(retCode.msg + "(" + retCode.ret + ")",Toast.LENGTH_SHORT);

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    showHideLogin(true);

                    LOG.d("apiUserChecker VolleyError " + volleyError.getMessage());
                    //Toast.makeText(SplashActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
                    mCuzToast.showToast( getString(R.string.network_fail),Toast.LENGTH_SHORT);
                }
            });
        }
    }

    private void callBrowser(String url)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);

        finish();

    }

    public static String getVersionName(Context context)
    {
        try {
            PackageInfo pi= context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LOG.d("onActivityResult requestCode " + requestCode);
        LOG.d("onActivityResult resultCode " + resultCode);
        if (requestCode == ACT_RESULT_CODE) {
            if (resultCode == NavigationDrawerFragment.ACT_RESULT_CODE_SETTING) {
                // logout 경우.//입력창 노출.
                showHideLogin(true);
            } else if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    private void showHideLogin(boolean isShow) {
        if (isShow) {
            llLoginLayout.setVisibility(View.VISIBLE);
        } else {
            llLoginLayout.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // flurry
    @Override
    protected void onStart() {
        super.onStart();
        //FlurryAgent.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
       // FlurryAgent.onEndSession(this);
    }
}
