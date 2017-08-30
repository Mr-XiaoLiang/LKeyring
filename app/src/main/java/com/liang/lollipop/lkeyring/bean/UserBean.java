package com.liang.lollipop.lkeyring.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by Lollipop on 2017/08/28.
 * 用户的Bean
 */
public class UserBean extends BmobUser {
    //签名
    private String signature;
    //设备ID
    private String driveId;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }
}
