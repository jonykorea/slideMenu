package com.tws.common.lib.soulbrownlib;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;


public class SoundManager {

    private SoundPool mSoundPool;
    private HashMap<Integer, Integer> mSoundPoolMap;
    private AudioManager mAudioManager;
    private Context mContext;


    // 커스텀 리스너Custom
    private OnLoadCompleteListener customListener;

    // 커스텀 리스너의 인터페이스
    public interface OnLoadCompleteListener{
        public void onLoadComplete(SoundPool soundPool, int sampleId, int status);
    }
    // 데이터를 전달할 커스텀 리스너를 연결

    public SoundManager() {

    }

    public void initSounds(Context theContext, OnLoadCompleteListener listener) {
        mContext = theContext;
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mSoundPoolMap = new HashMap<Integer, Integer>();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        this.customListener = listener;

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

                customListener.onLoadComplete(soundPool,sampleId,status);
            }

        });
    }


    public void addSound(int Index, int SoundID) {
        mSoundPoolMap.put(Index, mSoundPool.load(mContext, SoundID, Index));
    }

    public void playSound(int index) {

        int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f);
    }

    public void playLoopedSound(int index) {

        int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1f);
    }

}