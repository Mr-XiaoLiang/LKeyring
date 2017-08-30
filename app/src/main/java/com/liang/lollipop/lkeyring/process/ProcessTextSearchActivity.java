package com.liang.lollipop.lkeyring.process;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.liang.lollipop.lkeyring.R;

/**
 * 用于系统上下文中，选词关联的Activity
 * 此Activity用于查询Key
 * @author Lollipop on 2017-08-26
 */
public class ProcessTextSearchActivity extends FloatingActivity {

    private TextView nameText;
    private TextView valueText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_text_search);
        initView();
    }

    private void initView(){
        setClearBtn(R.id.activity_process_text_search_clear);
        withBackgroundView(R.id.activity_process_text_search_back);
        withForegroundView(R.id.activity_process_text_search_fore);
        findViewById(R.id.activity_process_text_search_copy).setOnClickListener(this);
        nameText = (TextView) findViewById(R.id.activity_process_text_search_name);
        valueText = (TextView) findViewById(R.id.activity_process_text_search_value);
        nameText.setText(getProcessText());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.activity_process_text_search_copy:
                callDismiss(v);
                break;
        }
    }
}
