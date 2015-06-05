package com.brewbrew.activities.demo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.brewbrew.R;
import com.brewbrew.views.widgets.CuzSwitch;
import com.brewbrew.views.widgets.LoadingDialog;

public class DmWidgetMainActivity extends ActionBarActivity {


    CuzSwitch mCuzSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dm_widget_main);

        mCuzSwitch = (CuzSwitch)findViewById(R.id.switchView);

    }

    public void onBtnSwitchOn(View v)
    {
        if(!mCuzSwitch.isCheck())
        {
            mCuzSwitch.setChecked(true);
        }
    }

    public void onBtnSwitchOff(View v)
    {
        if(mCuzSwitch.isCheck())
        {
            mCuzSwitch.setChecked(false);
        }
    }



    public void onBtnShowProgress(View v)
    {
        LoadingDialog loadingDialog = new LoadingDialog(DmWidgetMainActivity.this);

        loadingDialog.show();

        Toast.makeText(this,"Back Key click dismiss!",Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dm_widget_main, menu);
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
