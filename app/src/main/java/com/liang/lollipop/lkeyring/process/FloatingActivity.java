package com.liang.lollipop.lkeyring.process;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.liang.lollipop.lkeyring.R;
import com.liang.lollipop.lkeyring.activity.BaseActivity;

/**
 * Created by Lollipop on 2017/08/26.
 * 浮动的Activity
 * 用于其他地方打开的浮动临时窗口
 */
public class FloatingActivity extends BaseActivity{

    private View foregroundView;
    private View backgroundView;
    private View clearBtn;
    private String processText = "";
    private boolean readOnly = false;

    @Override
    public void onClick(View v) {
        if(v==backgroundView||v==clearBtn){
            callDismiss();
            return;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Intent intent = getIntent();
            CharSequence charSequence = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
            if(charSequence!=null)
                processText = charSequence.toString();
            else
                processText = "";
            readOnly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY,false);
        }
    }

    protected void callDismiss(){
        callDismiss(clearBtn);
    }

    protected void callDismiss(View view){
        if(foregroundView==null){
            finish();
            return;
        }
        final int width = foregroundView.getWidth();
        final int height = foregroundView.getHeight();
        int x;
        int y;
        if(view!=null){
            Rect rect = new Rect();
            if(view.getLocalVisibleRect(rect)){
                int[] loc = new int[2];
                int[] foreLoc = new int[2];
                view.getLocationOnScreen(loc);
                foregroundView.getLocationOnScreen(foreLoc);
                x = (loc[0]-foreLoc[0])+rect.centerX();
                y = (loc[1]-foreLoc[1])+rect.centerY();
            }else{
                x = width;
                y = 0;
            }
        }else{
            x = width;
            y = 0;
        }
        float radius = (float) Math.sqrt(width*width + height*height);
        //关闭动画，操作对象为前景的CardView，中心点为关闭按钮（如果没有，则以CardView的右上角）
        Animator animator = ViewAnimationUtils.createCircularReveal(foregroundView,x,y,radius,0);
        //设置动画时间
        animator.setDuration(getResources().getInteger(R.integer.animation_duration));
        //监听动画状态，当动画结束时，销毁当前页面
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                foregroundView.setVisibility(View.GONE);
                backgroundView.setVisibility(View.GONE);
                finish();
            }
        });
        animator.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //如果前景View并没有被关联，那么就不执行启动动画了
        if(foregroundView==null){
            return;
        }
        foregroundView.post(new Runnable() {
            @Override
            public void run() {
                final int width = foregroundView.getWidth();
                final int height = foregroundView.getHeight();
                int x;
                int y;
                if(clearBtn!=null){
                    Rect rect = new Rect();
                    if(clearBtn.getLocalVisibleRect(rect)){
                        int[] loc = new int[2];
                        int[] foreLoc = new int[2];
                        clearBtn.getLocationOnScreen(loc);
                        foregroundView.getLocationOnScreen(foreLoc);
                        x = (loc[0]-foreLoc[0])+rect.centerX();
                        y = (loc[1]-foreLoc[1])+rect.centerY();
                    }else{
                        x = width;
                        y = 0;
                    }
                }else{
                    x = width;
                    y = 0;
                }
                float radius = (float) Math.sqrt(width*width + height*height);
                //开启动画，操作对象为前景的CardView，中心点为关闭按钮（如果没有，则以CardView的右上角）
                Animator animator = ViewAnimationUtils.createCircularReveal(foregroundView,x,y,0,radius);
                //设置动画时间
                animator.setDuration(getResources().getInteger(R.integer.animation_duration));
                animator.start();
            }
        });
    }

    protected void setClearBtn(int id){
        if(id==0)
            return;
        View view = findViewById(id);
        if(view!=null){
            view.setOnClickListener(this);
            clearBtn = view;
        }
    }

    protected void withForegroundView(int id){
        if(id==0)
            return;
        View view = findViewById(id);
        if(view!=null){
            view.setOnClickListener(this);
            foregroundView = view;
        }
    }

    protected void withBackgroundView(int id){
        if(id==0)
            return;
        View view = findViewById(id);
        if(view!=null){
            view.setOnClickListener(this);
            backgroundView = view;
        }
    }

    protected String getProcessText() {
        return processText;
    }

    protected boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void onBackPressed() {
        callDismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
