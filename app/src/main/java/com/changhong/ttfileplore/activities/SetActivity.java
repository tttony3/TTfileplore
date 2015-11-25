package com.changhong.ttfileplore.activities;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.base.BaseActivity;
import com.changhong.ttfileplore.view.ToggleButton;

import android.animation.Animator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class SetActivity extends BaseActivity implements ToggleButton.OnToggleChanged {
    ToggleButton tb_setnight;
    ToggleButton tb_setshare;
    ToggleButton tb_sethide;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set);
        MyApp myapp = (MyApp) getApplication();
        myapp.setContext(this);
        findView();
        initView();
        initToolBar();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initView() {
        sharedPreferences = getSharedPreferences("set", Context.MODE_PRIVATE); //私有数据
        int mode = sharedPreferences.getInt("Theme", R.style.DayTheme);
        boolean hide = sharedPreferences.getBoolean("showhidefile", false);
        boolean share = sharedPreferences.getBoolean("share", true);
        if (mode == R.style.DayTheme)
            tb_setnight.setToggleOff();
        else if (mode == R.style.NightTheme)
            tb_setnight.setToggleOn();
        if (hide) {
            tb_sethide.setToggleOn();
        } else {
            tb_sethide.setToggleOff();
        }
        if (share) {
            tb_setshare.setToggleOn();
        } else {
            tb_setshare.setToggleOff();
        }


        tb_setnight.setOnToggleChanged(this);
        tb_sethide.setOnToggleChanged(this);
        tb_setshare.setOnToggleChanged(this);
    }

    @Override
    protected void findView() {

        tb_setnight = findView(R.id.tb_setnight);
        tb_sethide = findView(R.id.tb_showhide);
        tb_setshare = findView(R.id.tb_setshare);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onToggle(ToggleButton v, boolean on) {
        Editor editor = sharedPreferences.edit();//获取编辑器
        switch (v.getId()) {
            case R.id.tb_setnight:
                if (!on)
                    editor.putInt("Theme", R.style.DayTheme);
                else
                    editor.putInt("Theme", R.style.NightTheme);
                editor.commit();
                final View rootView = getWindow().getDecorView();
                rootView.setDrawingCacheEnabled(true);
                rootView.buildDrawingCache(true);
                final Bitmap localBitmap = Bitmap.createBitmap(rootView.getDrawingCache());
                rootView.setDrawingCacheEnabled(false);

                if (!on) {
                    setTheme(R.style.DayTheme);
                } else {
                    setTheme(R.style.NightTheme);
                }
                if (null != localBitmap && rootView instanceof ViewGroup) {
                    final View localView2 = new View(getApplicationContext());
                    //   localView2.setBackgroundColor(getResources().getColor(R.color.dark_model));
                    localView2.setBackground(new BitmapDrawable(getResources(), localBitmap));
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    ((ViewGroup) rootView).addView(localView2, params);
                    localView2.animate().alpha(0).setDuration(500).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            setContentView(R.layout.activity_set);
                            findView();
                            initView();
                            initToolBar();

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            ((ViewGroup) rootView).removeView(localView2);

                            localBitmap.recycle();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
                }

                break;
            case R.id.tb_showhide:
                editor.putBoolean("showhidefile", on);
                editor.commit();//提交修改
                break;
            case R.id.tb_setshare:
                editor.putBoolean("share", on);
                editor.commit();//提交修改
                break;

            default:
                break;
        }
        if (on)
            v.setToggleOn();
        else
            v.setToggleOff();

    }
}
