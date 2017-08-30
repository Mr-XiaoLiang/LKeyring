package com.liang.lollipop.lkeyring.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Lollipop on 2017/08/28.
 * Key数据Bean
 */
public class KeyBean extends BmobObject {
    //钥匙的值
    private String key_value;
    //钥匙的名字
    private String key_name;
    //创建人ID
    private String create_id;

    public String getKey_value() {
        return key_value;
    }

    public void setKey_value(String key_value) {
        this.key_value = key_value;
    }

    public String getKey_name() {
        return key_name;
    }

    public void setKey_name(String key_name) {
        this.key_name = key_name;
    }

    public String getCreate_id() {
        return create_id;
    }

    public void setCreate_id(String create_id) {
        this.create_id = create_id;
    }

    public KeyBean() {
        this.setTableName("key_table");
    }
}
