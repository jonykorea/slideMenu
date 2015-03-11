package com.tws.soul.soulbrown.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.app.define.LOG;
import com.tws.common.lib.views.CuzToast;
import com.tws.network.data.ExtraType;
import com.tws.network.data.RetUserChecker;
import com.tws.network.lib.ApiAgent;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.base.BaseActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        context = getApplicationContext();

        CuzToast cuzToast = new CuzToast(context);
        cuzToast.showToast("Test",Toast.LENGTH_SHORT);

        llLoginLayout = (LinearLayout) findViewById(R.id.splash_login_layout);
        etLoginInput = (EditText) findViewById(R.id.splash_login_edit);
        etLoginInput.setOnEditorActionListener(this);

        btLoginID = (Button) findViewById(R.id.splash_login_btn);

        btLoginID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (etLoginInput.getText().toString().length() == 0) {
                    showHideLogin(true);
                    Toast.makeText(SplashActivity.this, "ID 를 확인해 주세요.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SplashActivity.this, "ID 를 확인해 주세요.", Toast.LENGTH_SHORT).show();
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

                    LOG.d("retCode.result : " + retCode.result);
                    LOG.d("retCode.errormsg : " + retCode.errormsg);
                    LOG.d("retCode.usertype : " + retCode.usertype);


                    if (retCode.result == 1) {

                        // success

                        if (retCode.usertype.equals("user") || retCode.usertype.equals("owner")) {

                            LOG.d("apiSetUserLoc Succ");

                            PrefUserInfo prefUserInfo = new PrefUserInfo(SplashActivity.this);

                            prefUserInfo.setUserID(userID);

                            if (retCode.usertype.equals("user")) {
                                prefUserInfo.setUserType(true);
                            } else {
                                prefUserInfo.setUserType(false);
                            }

                            Intent intent = new Intent(context, SoulBrownMainActivity.class);
                            intent.putExtra(ExtraType.USER_TYPE, retCode.usertype);
                            startActivityForResult(intent, ACT_RESULT_CODE);
                        } else {
                            showHideLogin(true);
                            Toast.makeText(SplashActivity.this, "등록되지 않는 ID 입니다. 확인 부탁드립니다.", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        showHideLogin(true);
                        // fail
                        LOG.d("apiSetUserLoc Fail " + retCode.result);

                        Toast.makeText(SplashActivity.this, retCode.errormsg + "(" + retCode.result + ")", Toast.LENGTH_SHORT).show();

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    showHideLogin(true);

                    LOG.d("apiSetUserLoc VolleyError " + volleyError.getMessage());
                    Toast.makeText(SplashActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();

                }
            });
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
}
