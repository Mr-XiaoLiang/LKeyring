package com.liang.lollipop.lkeyring.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.liang.lollipop.lkeyring.utils.NotificationUtil;

/**
 * Created by Lollipop on 2017/08/30.
 * 这是一个用来移除消息的广播
 */
public class RemoveNotificationBroadcast extends BroadcastReceiver {

    public static final String ACTION = "ACTION_REMOVE_NOTIFICATION_BROADCAST";
    public static final String ARG_NOTIFICATION_ID = "ARG_NOTIFICATION_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(ARG_NOTIFICATION_ID,0);
        NotificationUtil.remove(context,id);
        Log.e("RemoveNotification","RemoveNotificationBroadcast："+id);
    }
}
