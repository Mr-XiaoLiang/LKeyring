package com.liang.lollipop.lkeyring.utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import com.liang.lollipop.lkeyring.R;
import com.liang.lollipop.lkeyring.broadcast.RemoveNotificationBroadcast;

import java.util.Random;

/**
 * Created by Lollipop on 2017/08/30.
 * 通知的工具类
 */
public class NotificationUtil {

    private static final int DEFAULT_ID = 0x78;

    private int duration = 2*1000;

    private NotificationCompat.Builder builder;
    private Context context;

    private NotificationUtil(NotificationCompat.Builder builder, Context context) {
        this.builder = builder;
        this.context = context;
    }

    public static NotificationUtil createNotification(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_round));
        builder.setSmallIcon(R.drawable.ic_notification_small);
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        // 设置通知的优先级
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        // 设置通知的提示音
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        return new NotificationUtil(builder,context);
    }

    public NotificationUtil setContentTitle(CharSequence title){
        builder.setContentTitle(title);
        builder.setTicker(title);
        return this;
    }

    public NotificationUtil setContentText(CharSequence text){
        builder.setContentText(text);
        return this;
    }

    public NotificationUtil setAutoCancel(boolean b){
        builder.setAutoCancel(b);
        return this;
    }

    public void show(int id){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    public static void simpleAlert(Context context,String title,String msg){
        createNotification(context)
                .setContentTitle(title)
                .setContentText(msg)
                .autoCancel(DEFAULT_ID)
                .show(DEFAULT_ID);
    }

    public NotificationUtil autoCancel(int id){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(RemoveNotificationBroadcast.ACTION);
        intent.putExtra(RemoveNotificationBroadcast.ARG_NOTIFICATION_ID,id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,Intent.FILL_IN_DATA);
        alarmManager.set(AlarmManager.RTC,System.currentTimeMillis()+duration,pendingIntent);
        return this;
    }

    public static void remove(Context context,int id){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
