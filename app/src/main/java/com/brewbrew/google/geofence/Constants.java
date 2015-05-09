package com.brewbrew.google.geofence;

/**
 * Created by jonychoi on 15. 1. 20..
 */
/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Constants used in this sample.
 */
public final class Constants {

    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.tws.soul.soulbrown";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 1;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 1000; // 1 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    public static final HashMap<String, LatLng> GEOFENCE_AREA = new HashMap<String, LatLng>();

    static {
        // thinkware
        GEOFENCE_AREA.put("Thinkware", new LatLng(37.402104, 127.110381));

        // pangyo
        //GEOFENCE_AREA.put("Pangyo", new LatLng(37.394850, 127.11111));

        //GEOFENCE_AREA.put("Pangyo", new LatLng(37.390776, 111095));




        //BAY_AREA_LANDMARKS.put("SFO", new LatLng(37.350079, 127.108916));

        // Googleplex.
        //BAY_AREA_LANDMARKS.put("GOOGLE", new LatLng(37.422611, -122.0840577));
    }
}