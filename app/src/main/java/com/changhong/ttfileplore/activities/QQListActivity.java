package com.changhong.ttfileplore.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.adapter.AppListAdapter;
import com.changhong.ttfileplore.adapter.PloreListAdapter;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.base.BaseActivity;
import com.changhong.ttfileplore.data.AppInfo;
import com.changhong.ttfileplore.data.PloreData;
import com.changhong.ttfileplore.fragment.MoreDialogFragment;
import com.changhong.ttfileplore.utils.Utils;
import com.changhong.ttfileplore.view.CircleProgress;
import com.changhong.ttfileplore.view.RefreshListView;
import com.chobit.corestorage.CoreApp;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class QQListActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener,
		MoreDialogFragment.UpDate ,PloreListAdapter.ImgOnClick{
	private RefreshDataAsynTask mRefreshAsynTask;
	LayoutInflater inflater;
	AlertDialog alertDialog;
	AlertDialog.Builder builder;
	CircleProgress mProgressView;
	View layout;
	private RefreshListView lv_classify;
	private TextView tv_dir;
	private TextView tv_count;
	private List<File> files = new ArrayList<>();
	private LinkedList<File> father = new LinkedList<>();
	private PloreListAdapter qqAdapter;
	private AppListAdapter appAdapter;
	private ArrayList<AppInfo> appList = new ArrayList<>();
	private int flag;
	File file;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_classify_list);
		inflater = LayoutInflater.from(this);
		findView();
		initView();
		initToolBar();
	}
	private void initToolBar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
		setSupportActionBar(toolbar);
	}
	private void initView() {
		flag = getIntent().getIntExtra("key", 0);
		
		File file;
		File sdfile;
		if (flag == R.id.img_app) {
			layout = inflater.inflate(R.layout.circle_progress, (ViewGroup) findViewById(R.id.rl_progress));
			builder = new AlertDialog.Builder(QQListActivity.this).setView(layout);
			alertDialog = builder.create();
			mProgressView = (CircleProgress) layout.findViewById(R.id.progress);
			// 用来存储获取的应用信息数据
			alertDialog.show();
			mProgressView.startAnim();
			mRefreshAsynTask = new RefreshDataAsynTask();
			mRefreshAsynTask.execute();

		} else {
			switch (flag) {

			case R.id.img_wechat:

				ArrayList<File> wcfiles = new ArrayList<File>();
				tv_dir.setText("微信小视频");
				file = new File("/storage/sdcard0/tencent/MicroMsg");
				sdfile = new File("/storage/sdcard1/tencent/MicroMsg");
				if (file.exists()) {
					File[] mfils1 = file.listFiles();
					for (int i = 0; i < mfils1.length; i++) {
						if (mfils1[i].getName().length() > 25 && mfils1[i].isDirectory())
							wcfiles.add(mfils1[i]);

					}
				}
				if (sdfile.exists()) {
					File[] mfils2 = sdfile.listFiles();
					for (int i = 0; i < mfils2.length; i++) {
						if (mfils2[i].getName().length() > 25 && mfils2[i].isDirectory())
							wcfiles.add(mfils2[i]);

					}
				}
				for (int i = 0; i < wcfiles.size(); i++) {

					file = new File(wcfiles.get(i).getPath() + "/video");
					PloreData mPloreData = new PloreData(file,true);
					files.addAll(mPloreData.getfiles());
				}
				for (int i = 0; i < files.size(); i++) {
					if (files.get(i).getName().startsWith(".") || !Utils.getMIMEType(files.get(i)).equals("video/*")) {
						files.remove(i);
						i--;
					}

				}
				break;
			case R.id.img_qq:
				tv_dir.setText("QQ接收文件");
				file = new File("/storage/sdcard0/tencent/QQfile_recv");
				sdfile = new File("/storage/sdcard1/tencent/QQfile_recv");
				

				if (file.exists() && file.isDirectory()) {
					PloreData mPloreData = new PloreData(file,false);
					files.addAll(mPloreData.getfiles());
				}
				if (sdfile.exists() && sdfile.isDirectory()) {
					PloreData mPloreData = new PloreData(sdfile,false);
					files.addAll(mPloreData.getfiles());
				}

				break;
			default:
				break;
			}

			tv_count.setText(files.size() + "项");
			qqAdapter = new PloreListAdapter(QQListActivity.this, files, true, ImageLoader.getInstance());
			lv_classify.setAdapter(qqAdapter);
			lv_classify.setOnItemClickListener(this);
			lv_classify.setOnItemLongClickListener(this);
		}
	}

	@Override
	protected void findView() {
		getLayoutInflater();
		lv_classify = (RefreshListView) findViewById(R.id.file_list);
		tv_dir = (TextView) findViewById(R.id.path);
		tv_count = (TextView) findViewById(R.id.item_count);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		File file = (File) parent.getItemAtPosition(position);
		if (file.isDirectory()) {
			PloreData mPloreData = new PloreData(file,true);
			files.clear();
			files.addAll(mPloreData.getfiles());
			for (int i = 0; i < files.size(); i++) {
				if (files.get(i).getName().startsWith(".")) {
					files.remove(i);
					i--;
				}
			}
			qqAdapter.updateList(files);
			father.add(file.getParentFile());

		} else {
			startActivity(Utils.openFile(file));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			File file = father.pollLast();
			if (file != null) {
				PloreData mPloreData = new PloreData(file,true);
				files.clear();
				files.addAll(mPloreData.getfiles());
				for (int i = 0; i < files.size(); i++) {
					if (files.get(i).getName().startsWith(".")) {
						files.remove(i);
						i--;
					}
				}
				qqAdapter.updateList(files);
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}



	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (flag != R.id.img_app) {

			  file = (File) parent.getItemAtPosition(position);
			MoreDialogFragment moreDialog = new MoreDialogFragment();
			Bundle bundle = new Bundle();
			bundle.putString("filePath", file.getPath());
			moreDialog.setArguments(bundle);
			moreDialog.show(getFragmentManager(), "detailDialog");

		}
		return true;
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
	public void update() {
		files.remove(file);
		qqAdapter.updateList(files);
	}

	@Override
	public void onClick(View v, File file) {
		if(file.isDirectory()) {
			PloreData mPloreData = new PloreData(file, false);
			files.clear();
			files.addAll(mPloreData.getfiles());
			for (int i = 0; i < files.size(); i++) {
				if (files.get(i).getName().startsWith(".")) {
					files.remove(i);
					i--;
				}
			}
			qqAdapter.updateList(files);
			father.add(file.getParentFile());
		}else{
			startActivity(Utils.openFile(file));
		}
	}

	class RefreshDataAsynTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {

			List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

			for (int i = 0; i < packages.size(); i++) {
				PackageInfo packageInfo = packages.get(i);
				AppInfo tmpInfo = new AppInfo();
				tmpInfo.appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
				tmpInfo.packageName = packageInfo.packageName;
				tmpInfo.versionName = packageInfo.versionName;
				tmpInfo.versionCode = packageInfo.versionCode;
				tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
				// Only display the non-system app info
				if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					appList.add(tmpInfo);// 如果非系统应用，则添加至appList
				}

			}
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			if (alertDialog.isShowing()) {
				mProgressView.stopAnim();
				alertDialog.dismiss();
			}
			appAdapter = new AppListAdapter(QQListActivity.this, appList);
			tv_count.setText(appList.size() + "项");
			lv_classify.setAdapter(appAdapter);
		}

	}
}
