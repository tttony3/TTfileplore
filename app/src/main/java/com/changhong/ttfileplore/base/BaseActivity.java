package com.changhong.ttfileplore.base;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.changhong.ttfileplore.R;

public abstract class BaseActivity   extends Activity {
	SharedPreferences sharedPreferences;
	/**
	 * 加载布局中的控件
	 */	
	protected abstract void findView();


	@SuppressWarnings("unchecked")
	public <T extends View> T findView(int id) {
	        return (T) findViewById(id);
	   
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 sharedPreferences = getSharedPreferences("set", Context.MODE_PRIVATE); //私有数据
		if(!sharedPreferences.getBoolean("night",false)){
			setTheme(R.style.DayTheme);
		}else
			setTheme(R.style.NightTheme);
		ActionBar actionBar = getActionBar();
		if(actionBar!=null){
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}
}
