package com.liang.lollipop.lkeyring.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.liang.lollipop.lkeyring.R;
import com.liang.lollipop.lsnackbar.LSnackBar;

/**
 * Created by Lollipop on 2017/08/26.
 * 基础的Activity
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean showBack = true;

    private View rootView = null;

    protected Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            onHandler(msg);
            super.handleMessage(msg);
        }
    };

    protected void onHandler(Message msg){

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        findRootView();
    }

    protected void setFullScreen(boolean isFull){
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        View decorView = getWindow().getDecorView();
        if(isFull){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); //清除非全屏的flag
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //设置全屏的flag
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //清除全屏的flag
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); //非全屏
            int uiOptions = 0;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    protected void setScreenOrientation(boolean isLandscape){
        if(isLandscape){//横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{//竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        findRootView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        findRootView();
    }

    protected void setToolBar(int id){
        Toolbar toolbar = (Toolbar) findViewById(id);
        if(toolbar!=null)
            setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null&&showBack)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void setToolBar(int id,int icon){
        Toolbar toolbar = (Toolbar) findViewById(id);
        if(toolbar!=null){
            if(icon!=0)
                toolbar.setNavigationIcon(icon);
            else
                showBack = false;
            setSupportActionBar(toolbar);
        }
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
    }

    protected void setShowBack(boolean showBack) {
        this.showBack = showBack;
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void S(View view,String msg, String btnName, View.OnClickListener btnClick){
        if(view==null){
            Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
            if(btnClick!=null)
                btnClick.onClick(null);
            return;
        }
        int d;
        if(TextUtils.isEmpty(btnName)||btnClick==null){
            d = LSnackBar.LENGTH_SHORT;
        }else{
            d = LSnackBar.LENGTH_LONG;
        }
        LSnackBar.make(view,msg,d).setAction(btnName,btnClick)
                .setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setMessageTextColor(Color.WHITE)
                .setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))
                .setLogo(R.mipmap.ic_launcher_round).show();
    }

    protected void S(String msg, String btnName, View.OnClickListener btnClick){
        S(rootView,msg,btnName,btnClick);
    }

    protected void S(View view,String msg){
        S(view,msg, "", null);
    }

    protected void S(String msg){
        S(msg, "", null);
    }

    protected void startActivity(Intent intent, Pair<View,String>... pair) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,pair);
        super.startActivity(intent,optionsCompat.toBundle());
    }

    private void findRootView(){
        //获取根节点View，用于弹出SnackBar
        ViewGroup contentParent = (ViewGroup) findViewById(android.R.id.content);
        rootView = contentParent.getChildCount()>0?contentParent.getChildAt(0):contentParent;
    }

}
