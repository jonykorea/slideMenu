package com.brewbrew.activities.demo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.brewbrew.R;
import com.brewbrew.activities.BrewbrewMainActivity;
import com.brewbrew.activities.IntroActivity;
import com.brewbrew.activities.TutorialActivity;

public class DmModuleActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dm_module);
    }

    public void onBtnIntroActivity(View v)
    {
        startActivity(new Intent(this, IntroActivity.class));
    }

    public void onBtnBrewbrewMainActivity(View v)
    {
        startActivity(new Intent(this, BrewbrewMainActivity.class));
    }

    public void onBtnDmWidgetMainActivity(View v)
    {
        startActivity(new Intent(this, DmWidgetMainActivity.class));
    }

    public void onBtnDmDaumMapMainActivity(View v)
    {
        startActivity(new Intent(this,DmDaumMapMainActivity.class));
    }

    public void onBtnDmGridLayoutMainActivity(View v)
    {
        startActivity(new Intent(this,DmGridLayoutMainActivity.class));
    }

    public void onBtnDmKakaoLinkMainActivity(View v)
    {
        startActivity(new Intent(this,DmKakaoLinkMainActivity.class));
    }

    public void onBtnDmSlidingDialogMainActivity(View v)
    {
        startActivity(new Intent(this,DmSlidingDialogMainActivity.class));
    }

    public void onBtnDmSlidnigUpPanelActivity(View v)
    {
        startActivity(new Intent(this,DmSlidnigUpPanelActivity.class));
    }

    public void onBtnTutorialActivity(View v)
    {
        startActivity(new Intent(this,TutorialActivity.class));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dm_module, menu);
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
