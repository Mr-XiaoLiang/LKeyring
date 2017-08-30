package com.liang.lollipop.lkeyring.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by Lollipop on 2017/08/28.
 * 获取AndroidId用来标志用户
 */
public class DeviceIdUtil {

    private static UUID uuid;

    public static UUID getId(Context context){
        if(uuid==null){
            synchronized (DeviceIdUtil.class) {
                if (uuid == null) {
                    final String id = Settings.getId(context);
                    if (!TextUtils.isEmpty(id)) {
                        // Use the ids previously computed and stored in the
                        // prefs file
                        uuid = UUID.fromString(id);
                    } else {
                        final String androidId = android.provider.Settings.Secure.getString(
                                context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                        // Use the Android ID unless it's broken, in which case
                        // fallback on deviceId,
                        // unless it's not available, then fallback on a random
                        // number which we store to a prefs file
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId
                                        .getBytes("utf8"));
                            } else {
                                final String deviceId = ((TelephonyManager)
                                        context.getSystemService(
                                                Context.TELEPHONY_SERVICE))
                                                .getDeviceId();
                                uuid = deviceId != null ? UUID
                                        .nameUUIDFromBytes(deviceId
                                                .getBytes("utf8")) : UUID
                                        .randomUUID();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        // Write the value out to the prefs file
                        Settings.putId(context,uuid.toString());
                    }
                }
                return uuid;
            }
        }else{
            return uuid;
        }
    }

    public static String getIdStr(Context context){
        String dId;
        UUID uuid = DeviceIdUtil.getId(context);
        if(uuid==null){
            dId = "unknown";
        }else {
            dId = uuid.toString();
        }
        return dId;
    }

}
