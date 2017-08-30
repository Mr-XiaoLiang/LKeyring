package com.liang.lollipop.lkeyring;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import cn.bmob.v3.Bmob;

/**
 * Created by Lollipop on 2017/08/24.
 * 应用上下文
 */
public class LApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //第一：默认初始化
        String appId = "";
        try {
            ApplicationInfo appInfo = getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            appId = appInfo.metaData.getString("Bmob_AppId");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bmob.initialize(this, appId,"CoolApk");
    }
}
