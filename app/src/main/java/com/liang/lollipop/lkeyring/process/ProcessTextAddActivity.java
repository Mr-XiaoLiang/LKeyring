package com.liang.lollipop.lkeyring.process;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.liang.lollipop.lkeyring.R;
import com.liang.lollipop.lkeyring.utils.BmobUtil;
import com.liang.lollipop.lkeyring.utils.NotificationUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 用于系统上下文中，选词关联的Activity
 * 此Activity用于添加Key
 * @author Lollipop on 2017-08-26
 */
public class ProcessTextAddActivity extends FloatingActivity {

    private TextInputEditText nameEdit;
    private TextInputEditText valueEdit;
    private View saveBtn;
    private TextView errorView;
    private ValueAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_text_add);
        initView();
        animator = new ValueAnimator();
    }

    private void initView(){
        setClearBtn(R.id.activity_process_text_add_clear);
        withBackgroundView(R.id.activity_process_text_add_back);
        withForegroundView(R.id.activity_process_text_add_fore);
        findViewById(R.id.activity_process_text_add_swap).setOnClickListener(this);
        saveBtn = findViewById(R.id.activity_process_text_add_done);
        saveBtn.setOnClickListener(this);

        errorView = (TextView) findViewById(R.id.activity_process_text_add_error);

        nameEdit = (TextInputEditText) findViewById(R.id.activity_process_text_add_name);
        valueEdit = (TextInputEditText) findViewById(R.id.activity_process_text_add_value);
        errorView.setText("");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.activity_process_text_add_swap:
                Editable name = nameEdit.getText();
                nameEdit.setText(valueEdit.getText());
                valueEdit.setText(name);
                break;
            case R.id.activity_process_text_add_done:
                errorView.setText("");
                save();
                break;
        }
    }

    private void save(){
        String name = nameEdit.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            nameEdit.setError("请输入钥匙名");
            nameEdit.findFocus();
            nameEdit.requestFocus();
            return;
        }
        String value = valueEdit.getText().toString().trim();
        if(TextUtils.isEmpty(value)){
            valueEdit.setError("请输入钥匙值");
            valueEdit.findFocus();
            valueEdit.requestFocus();
            return;
        }
        BmobUtil.saveKey(name, value, new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    onSave();
                }else{
                    errorView.setText("出现错误："+e.getErrorCode()+","+e.getMessage());
                }
            }
        });
    }

    private void onSave(){
        NotificationUtil.simpleAlert(this,"添加完成","新的共享钥匙已保存");
        callDismiss(saveBtn);
    }

}
