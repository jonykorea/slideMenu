package com.tws.common.lib.mgr;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.PowerManager;

/**
 * LOG info setting
 * DEBUG : true / false;
 *
 * @author
 */
public class WakeupMgr {

    private Context context;
    private PowerManager.WakeLock mWL;
    private WifiManager.WifiLock mWifi;
    private int cntWackLoc = 0;

    public WakeupMgr(Context context) {
        this.context = context;
    }

    public void releaseWifiManager() {

        cntWackLoc--;

        if (cntWackLoc > 0) return;

        if (mWL != null) {
            if (mWifi != null) {
                mWifi.release();
                mWifi = null;
            }
            mWL.release();
            mWL = null;
        }
    }

    public void setPowerWakeUp(int nFlag) {
        cntWackLoc++;

        if (mWL == null) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

            switch (nFlag) {
                case 1:
                    // (Cpu o / wifi on)
                    mWL = pm.newWakeLock(
                            PowerManager.PARTIAL_WAKE_LOCK, "tws_service");
                    mWL.setReferenceCounted(true);
                    mWL.acquire();
                    setWifiWakeUp();
                    break;
                case 2:
                    // (Cpu o / wifi off)
                    mWL = pm.newWakeLock(
                            PowerManager.PARTIAL_WAKE_LOCK, "tws_service");
                    mWL.setReferenceCounted(true);
                    mWL.acquire();

                    break;
                case 3:
                    // ( Cpu O / Screen O / wifi on)
                    mWL = pm.newWakeLock(
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tws_service");
                    mWL.setReferenceCounted(true);
                    mWL.acquire();
                    setWifiWakeUp();
                    break;
                case 4:
                    // ( Cpu O / Screen O / wifi off)
                    mWL = pm.newWakeLock(
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tws_service");
                    mWL.setReferenceCounted(true);
                    mWL.acquire();

                    break;
                case 5:
                    // ( Cpu O / Screen O / keyboard O / wifi on)
                    mWL = pm.newWakeLock(
                            PowerManager.ACQUIRE_CAUSES_WAKEUP |
                                    PowerManager.FULL_WAKE_LOCK |
                                    PowerManager.ON_AFTER_RELEASE, "tws_service");
                    mWL.setReferenceCounted(true);
                    mWL.acquire();
                    setWifiWakeUp();

                    break;

                default:
                    break;
            }
        }
    }

    public void setWifiWakeUp() {
        if (mWifi == null) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            mWifi = wifiManager.createWifiLock("tws_service_wifi");
            mWifi.setReferenceCounted(true);
            mWifi.acquire();
        }
    }
}
