package com.liang.lollipop.lkeyring.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Lollipop on 2017/08/28.
 * 偏好设置
 */
public class Settings {

    private static final String PREFS_DEVICE_ID = "DEVICE_ID";

    public static String getId(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return SharedPreferencesUtils.get(sharedPreferences,PREFS_DEVICE_ID,"");
    }

    public static void putId(Context context,String id){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferencesUtils.put(sharedPreferences,PREFS_DEVICE_ID,id);
    }

}
