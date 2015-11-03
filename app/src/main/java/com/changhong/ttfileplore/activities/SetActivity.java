package com.changhong.ttfileplore.activities;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.base.BaseActivity;

import android.animation.Animator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class SetActivity extends BaseActivity implements OnCheckedChangeListener {
	ToggleButton tb_setnight;
	ToggleButton tb_setshare;
	ToggleButton tb_sethide;
	SharedPreferences sharedPreferences ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_set);
		MyApp myapp = (MyApp) getApplication();
		myapp.setContext(this);
		findView();
		initView();

	}

	private void initView() {
		sharedPreferences = getSharedPreferences("set", Context.MODE_PRIVATE); //私有数据
		int mode=sharedPreferences.getInt("Theme",R.style.DayTheme);
		boolean hide=sharedPreferences.getBoolean("showhidefile",false);
		boolean share=sharedPreferences.getBoolean("share",true);
		if(mode == R.style.DayTheme)
			tb_setnight.setChecked(false);
		else if(mode == R.style.NightTheme)
			tb_setnight.setChecked(true);
		tb_sethide.setChecked(hide);
		tb_setshare.setChecked(share);
		
		tb_setnight.setOnCheckedChangeListener(this);
		tb_sethide.setOnCheckedChangeListener(this);
		tb_setshare.setOnCheckedChangeListener(this);
	}

	@Override
	protected void findView() {

		tb_setnight = findView(R.id.tb_setnight);
		tb_sethide = findView(R.id.tb_showhide);
		tb_setshare = findView(R.id.tb_setshare);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	
		Editor editor = sharedPreferences.edit();//获取编辑器
		switch (buttonView.getId()) {		
		case R.id.tb_setnight:
			if(!isChecked)
				editor.putInt("Theme", R.style.DayTheme);
			else
				editor.putInt("Theme", R.style.NightTheme);
			editor.commit();
			final View rootView = getWindow().getDecorView();
				rootView.setDrawingCacheEnabled(true);
				rootView.buildDrawingCache(true);
				final Bitmap localBitmap = Bitmap.createBitmap(rootView.getDrawingCache());
				rootView.setDrawingCacheEnabled(false);

				if(!isChecked) {
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
			editor.putBoolean("showhidefile",isChecked);
			editor.commit();//提交修改
			break;
		case R.id.tb_setshare:
			editor.putBoolean("share",isChecked);
			editor.commit();//提交修改
			break;

		default:
			break;
		}
		buttonView.setChecked(isChecked);
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
}
