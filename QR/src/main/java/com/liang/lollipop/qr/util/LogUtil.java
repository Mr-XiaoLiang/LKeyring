package com.liang.lollipop.qr.util;

import android.util.Log;

/**
 * Created by Lollipop on 2017/08/24.
 * Log输出的工具箱
 */
public class LogUtil {

    private static boolean DEBUG = false;

    public static void logE(String name,String msg){
        if(DEBUG)
            Log.e(name,msg);
    }

    public static void logD(String name,String msg){
        if(DEBUG)
            Log.d(name,msg);
    }

    public static void logE(String name,String msg,Throwable tr){
        if(DEBUG)
            Log.e(name,msg,tr);
    }

    public static void logD(String name,String msg,Throwable tr){
        if(DEBUG)
            Log.d(name,msg,tr);
    }

    public static boolean isDebug(){
        return DEBUG;
    }

}
