package com.changhong.ttfileplore.activities;

import java.util.ArrayList;
import java.util.List;

import com.changhong.alljoyn.simpleclient.ClientBusHandler;
import com.changhong.alljoyn.simpleclient.DeviceInfo;
import com.changhong.ttfileplore.adapter.NetDevListAdapter;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.base.BaseActivity;
import com.changhong.ttfileplore.fragment.PushDialogFragment;
import com.changhong.ttfileplore.view.CircleProgress;
import com.chobit.corestorage.CoreApp;
import com.chobit.corestorage.CoreDeviceListener;
import com.changhong.ttfileplore.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ShowNetDevActivity extends BaseActivity {
	Context context = ShowNetDevActivity.this;
	ProgressDialog dialog;
	ListView netList;
	NetDevListAdapter netListAdapter;
	AlertDialog alertDialog;
	AlertDialog.Builder builder;
	CircleProgress mProgressView;
	View layout;
	ArrayList<String> pushList;
	MyApp myapp;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Bundle b = intent.getBundleExtra("pushList");
		if (b != null) {
			pushList = b.getStringArrayList("pushList");
		}

		setContentView(R.layout.activity_net_dev);
		 myapp = (MyApp) getApplication();
		myapp.setContext(this);
		LayoutInflater inflater = getLayoutInflater();
		layout = inflater.inflate(R.layout.circle_progress, (ViewGroup) findViewById(R.id.rl_progress));
		builder = new AlertDialog.Builder(this).setView(layout);
		alertDialog = builder.create();
		mProgressView = (CircleProgress) layout.findViewById(R.id.progress);

		netList = (ListView) findViewById(R.id.lv_netactivity);
		netListAdapter = new NetDevListAdapter(null, context);
		netList.setAdapter(netListAdapter);

		if (CoreApp.mBinder != null) {
			CoreApp.mBinder.setDeviceListener(deviceListener);
			deviceListener.startWaiting();
			ArrayList<DeviceInfo> list =new ArrayList<>();
			list.addAll(ClientBusHandler.List_DeviceInfo);
			setUpdateList(list);
		}

		netList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (pushList != null) {
					final DeviceInfo info = (DeviceInfo) parent.getItemAtPosition(position);

					PushDialogFragment pushDialogFragment = new PushDialogFragment() {
						@Override
						public void onPushFragmentEnter(String message) {
							JSONObject pushJson = new JSONObject();
							try {
								TelephonyManager tm = (TelephonyManager) ShowNetDevActivity.this
										.getSystemService(Context.TELEPHONY_SERVICE);
								String DEVICE_ID = tm.getDeviceId();
								pushJson.put("device_id",DEVICE_ID);
								pushJson.put("message",message);
								pushJson.put("filenum",pushList.size());
								pushJson.put("http","http://"+myapp.getIp()+":"+myapp.getPort());
							} catch (JSONException e) {
								e.printStackTrace();
							}
							if (pushList != null && pushList.size() != 0) {
								ArrayList<String> tmp = new ArrayList<String>();
								tmp.addAll(pushList);
								tmp.add(0, pushJson.toString());
								CoreApp.mBinder.PushResourceToDevice(info, tmp);
								dismiss();
							} else {
								dismiss();
								Toast.makeText(ShowNetDevActivity.this, "未选择推送文件", Toast.LENGTH_SHORT).show();
							}
						}
					};
					pushDialogFragment.show(getFragmentManager(),"pushDialogFragment");

				} else {
					DeviceInfo info = (DeviceInfo) parent.getItemAtPosition(position);
					Intent intent = new Intent();
					intent.setClass(ShowNetDevActivity.this, ShowNetFileActivity.class);
					((MyApp) getApplicationContext()).devinfo = info;
					startActivity(intent);
				}
			}
		});

	}

	private void setUpdateList(List<DeviceInfo> list) {
		Log.e("DeviceInfo", list.toString());
		if (list.size() > 0) {
			deviceListener.stopWaiting();
		}
		netListAdapter.updatelistview(list);
//		netListAdapter = new NetDevListAdapter(list, context);
//		netList.setAdapter(netListAdapter);
	}

	private CoreDeviceListener deviceListener = new CoreDeviceListener() {

		@Override
		public void updateDeviceList(List<DeviceInfo> list) {
			setUpdateList(list);

		}

		@Override
		public void stopWaiting() {
			if (getTopActivity(ShowNetDevActivity.this).equals(".activities.ShowNetDevActivity")) {
				if (alertDialog.isShowing()) {
					mProgressView.stopAnim();
					alertDialog.dismiss();
				}
			}
		}

		@Override
		public void startWaiting() {
			if (getTopActivity(ShowNetDevActivity.this).equals(".activities.ShowNetDevActivity")) {

				mProgressView.startAnim();
				alertDialog.show();
			}

		}

		@Override
		public void showMessage(String arg0) {
			Log.e("showMessage", arg0);
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		MyApp myapp = (MyApp) getApplication();
		myapp.setContext(this);
		super.onResume();
	}

	String getTopActivity(Activity context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null)
			return (runningTaskInfos.get(0).topActivity).getShortClassName();
		else
			return null;
	}

	@Override
	protected void findView() {
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
