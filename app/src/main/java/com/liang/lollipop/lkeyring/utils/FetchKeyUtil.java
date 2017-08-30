package com.liang.lollipop.lkeyring.utils;

import android.text.TextUtils;

import java.security.MessageDigest;

/**
 * Created by Lollipop on 2017/08/30.
 * 取键的工具类
 */
public class FetchKeyUtil {
    //二次摘要阈值
    private static final int DOUBLE_DIGEST = 1000;
    //三次摘要阈值
    private static final int TREBLE_DIGEST = 5000;
    //二次摘要长度
    private static final int DOUBLE_DIGEST_LENGTH = 200;
    //三次摘要长度
    private static final int TREBLE_DIGEST_LENGTH = 500;

    /**
     * 获取键值
     * 如果内容长度达到一定长度，将对数据多次摘要，减少重复的几率
     * @param msg
     * @return
     */
    public static String fetchKey(String msg){
        if(TextUtils.isEmpty(msg))
            return "";
        String key;
        //原始MD5
        key = getMD5(msg);
        //为了减少重复程度，当内容长度超过一定量时，对他进行多次摘要
        if(msg.length()>DOUBLE_DIGEST){
            key += getMD5(msg.substring(0,DOUBLE_DIGEST_LENGTH));
        }
        if(msg.length()>TREBLE_DIGEST)
            key += getMD5(msg.substring(msg.length()-TREBLE_DIGEST_LENGTH,msg.length()));
        return key;
    }

    /**
     * 获取MD5值
     * @param message
     * @return
     */
    private static String getMD5(String message) {
        String md5str = "";
        try {
            // 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 2 将消息变成byte数组
            byte[] input = message.getBytes();
            // 3 计算后获得字节数组,这就是那128位了
            byte[] buff = md.digest(input);
            // 4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            md5str = bytesToHex(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }

    /**
     * 二进制转十六进制
     *
     * @param bytes
     * @return
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        // 把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];
            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }
}
