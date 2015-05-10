package com.brewbrew.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;

import com.app.define.LOG;
import com.tws.common.lib.mgr.WakeupMgr;
import com.tws.common.lib.soulbrownlib.SoundManager;
import com.brewbrew.R;

public class AlarmNotiService extends Service {


    /* 현재 오디오모드확인 */
/*
http://examples.javacodegeeks.com/android/android-soundpool-example/
    AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT : 사일런트 모드일 경우(값0)
    mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE : 진동모드일 경우(값1)
    mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL : 벨 모드일 경우(값2)
    //AudioManager.ACTION_AUDIO_BECOMING_NOISY : 이어잭을 꼽고 있다가 뺏을 경우.
    */
    public AlarmNotiService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private WakeupMgr wakeupMgr;

    private boolean mIsPlay = false;
    private boolean mIsLoad = false;
    SoundManager mSoundManager;
    Vibrator mVib;
    private CountDownTimer mCntTimer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LOG.d("AlarmNotiService onStartCommand ");

        wakeupMgr.setPowerWakeUp(1);

        callVibrator();

        if (mCntTimer != null) {
            mCntTimer.cancel();
            mIsPlay = false;
        }

        mCntTimer.start();


        return Service.START_NOT_STICKY;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        LOG.d("AlarmNotiService onCreate ");

        wakeupMgr = new WakeupMgr(this);

        initSound();
        initTimer();

    }

    private void initTimer() {
        mCntTimer = new CountDownTimer(5 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                callSound();
            }

            @Override
            public void onFinish() {

                mIsPlay = false;
                wakeupMgr.releaseWifiManager();

            }
        };
    }

    private void initSound() {
        // 점주라면 push msg에 따른 사운드 셋팅.
        mSoundManager = new SoundManager();

        mSoundManager.initSounds(this, new SoundManager.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

                LOG.d("mSoundManager onLoadComplete status : " + status);
                mIsLoad = true;
            }
        });

        mSoundManager.addSound(1, R.raw.beep);
    }

    private void callVibrator() {
        if (mVib != null)
            mVib.cancel();

        mVib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        long[] patten = {0, 1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000, 500};

        mVib.vibrate(patten, -1);
    }

    private void setMaxVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int max = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max, 0);
    }

    private void callSound() {
        if (mIsLoad && !mIsPlay) {
            setMaxVolume();

            mIsPlay = true;
            mSoundManager.playSound(1);
        }

    }

    @Override
    public void onDestroy() {
        LOG.d("AlarmNotiService onDestroy ");


        super.onDestroy();
    }
}
