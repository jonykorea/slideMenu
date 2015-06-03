package com.brewbrew.activities.demo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.brewbrew.R;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.kakao.exception.KakaoException;
import com.kakao.helper.Logger;

public class DmKakaoLinkMainActivity extends ActionBarActivity {

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dm_kakao_link_main);

        editText = (EditText)findViewById(R.id.edt_message);

    }

    public void onBtnSend(View v)
    {

        String message = editText.getText().toString();

        if(!TextUtils.isEmpty(message)) {
            sendMessage(message);
        }
    }

    private void sendMessage(String msg)
    {
        try {

            Log.i("jony", "msg "+ msg.length());

            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            kakaoTalkLinkMessageBuilder.addText(msg);

            final String linkContents = kakaoTalkLinkMessageBuilder.build();
            Log.i("jony", "linkContents "+ linkContents.length());

            kakaoLink.sendMessage(linkContents, this);

        }
        catch (Exception e)
        {
            Logger.getInstance().d(e);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dm_kakao_link_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
