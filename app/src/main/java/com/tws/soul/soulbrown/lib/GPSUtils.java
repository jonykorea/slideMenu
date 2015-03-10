package com.tws.soul.soulbrown.lib;

import android.location.Location;

/**
 * Created by Jony on 2015-03-10.
 */
public class GPSUtils {

    public static float getDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude)
    {
        float[] results = new float[1];

        Location.distanceBetween(startLatitude, startLongitude, endLatitude , endLongitude ,results);
        float distance =results[0];

         return distance;
    }

    public static String getDistanceStr(float distance)
    {

        String retDistance = "정보없음";
        if( distance > 0 && distance <= 100)
        {
            // 100m 이내
            retDistance = "100m 이내";
        }
        else if( distance > 100 && distance <= 300)
        {
            // 300m 이내
            retDistance = "300m 이내";
        }
        else if( distance > 300 && distance <= 500)
        {
            // 500m 이내
            retDistance = "500m 이내";
        }
        else if( distance > 600 && distance <= 1000)
        {
            // 1km 이내
            retDistance = "1km 이내";
        }

        return retDistance;
    }
}
