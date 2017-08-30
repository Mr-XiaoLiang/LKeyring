package com.liang.lollipop.lkeyring.activity;

import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.liang.lollipop.lkeyring.R;
import com.liang.lollipop.lkeyring.bean.UserBean;
import com.liang.lollipop.lkeyring.process.ProcessTextAddActivity;
import com.liang.lollipop.lkeyring.utils.BmobUtil;
import com.liang.lollipop.lkeyring.utils.DeviceIdUtil;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import cn.bmob.v3.a.a.This;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 主页的Activity
 * @author Lollipop on 2017-08-26
 */
public class MainActivity extends BaseActivity implements TextWatcher {

    private TextInputEditText keyNameView;
    private TextView keyValueView;
    private View copyBtn;

    public static final String ARG_SEARCH_VALUE = "ARG_SEARCH_VALUE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String searchValue = getIntent().getStringExtra(ARG_SEARCH_VALUE);
        setToolBar(R.id.activity_main_toolbar, TextUtils.isEmpty(searchValue)?0:R.drawable.ic_clear_white_24dp);
        initView(searchValue);
        initUserData();
    }

    private void initView(String searchValue){
        findViewById(R.id.activity_main_fab).setOnClickListener(this);
        keyNameView = (TextInputEditText) findViewById(R.id.content_main_key_name);
        keyNameView.setText(searchValue);
        keyValueView = (TextView) findViewById(R.id.content_main_key_value);
        keyValueView.setMovementMethod(new ScrollingMovementMethod());
        keyValueView.addTextChangedListener(this);

        copyBtn = findViewById(R.id.content_main_key_copy);
        copyBtn.setOnClickListener(this);
    }

    private void initUserData(){
        BmobUtil.tryGetUser(DeviceIdUtil.getIdStr(this), new SaveListener<UserBean>() {
            @Override
            public void done(UserBean bean, BmobException e) {
                if(e==null){
                    if(bean!=null){
                        initUserInfo();
                    } else {
                        connectError("");
                    }
                }else{
                    connectError("错误码："+e.getErrorCode()+e.getMessage());
                }
            }
        });
    }

    private void connectError(String str){
        S("连接服务器失败."+str, "重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        initUserData();
                    }
                });
            }
        });
    }

    private void initUserInfo(){
        S("欢迎使用"+getString(R.string.app_name));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.activity_main_fab:
                startActivity(new Intent(this, ProcessTextAddActivity.class));
                break;
            case R.id.content_main_key_copy:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text",keyValueView.getText());
                cm.setPrimaryClip(clipData);
                S("已复制");
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int textLength = s.length();
        if(textLength==0){
            if(copyBtn.getVisibility()!=View.INVISIBLE)
                copyBtn.setVisibility(View.INVISIBLE);
            return;
        }
        if(copyBtn.getVisibility()!=View.VISIBLE){
            copyBtn.setVisibility(View.VISIBLE);
        }
        final float fontScale  = getResources().getDisplayMetrics().scaledDensity;
        int minTextSize = (int) ((fontScale *18)+0.5f);
        int viewWidth = keyValueView.getWidth()-keyValueView.getPaddingLeft()-keyValueView.getPaddingRight();
        int maxHeight = keyValueView.getMinHeight()-keyValueView.getPaddingTop()-keyValueView.getPaddingBottom();
        int textSize = viewWidth/textLength;
        if(textSize<minTextSize)
            textSize = minTextSize;
        if(maxHeight<textSize)
            textSize = maxHeight;
        keyValueView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
