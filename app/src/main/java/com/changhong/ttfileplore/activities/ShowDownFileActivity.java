package com.changhong.ttfileplore.activities;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.changhong.alljoyn.simpleservice.FC_GetShareFile;
import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.adapter.DownFileListAdapter;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.base.BaseActivity;
import com.changhong.ttfileplore.data.DownData;
import com.changhong.ttfileplore.service.DownLoadService;
import com.changhong.ttfileplore.service.DownLoadService.DownLoadBinder;
import com.changhong.ttfileplore.utils.Utils;
import com.changhong.ttfileplore.view.AlertView;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowDownFileActivity extends BaseActivity implements OnItemLongClickListener, OnItemClickListener {
    static final private int UPDATE_LIST = 1;
    static final private int UPDATE_LIST_DELETE = 2;
    private TextView tv_num;
    private ListView lv_downlist;
    private DownLoadService downLoadService;
    private ArrayList<DownData> downList;
    private ArrayList<DownData> alreadydownList;
    private DownFileListAdapter mAdapter;
    private MyDownHandler mHandler;
    private SharedPreferences sharedPreferences;
    AlertView alertView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downlist);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences("set", Context.MODE_PRIVATE); //私有数据
        MyApp myapp = (MyApp) getApplication();
        myapp.setContext(this);
        findView();

        try {
            alreadydownList = Utils.getDownDataObject("alreadydownlist");
        } catch (Exception e) {
            alreadydownList = new ArrayList<>();
        }
        downList = new ArrayList<>();
        mAdapter = new DownFileListAdapter(downList, alreadydownList, this, downLoadService);
        lv_downlist.setAdapter(mAdapter);
        mHandler = new MyDownHandler(this);

        lv_downlist.setOnItemClickListener(this);
        lv_downlist.setOnItemLongClickListener(this);
    }

    @Override
    protected void onStart() {

        if (sharedPreferences.getBoolean("share", true))
            this.bindService(new Intent("com.changhong.fileplore.service.DownLoadService"), conn, BIND_AUTO_CREATE);
        else
            downLoadService = new DownLoadService();
        try {
            alreadydownList = Utils.getDownDataObject("alreadydownlist");
        } catch (Exception e) {
            alreadydownList = new ArrayList<>();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (sharedPreferences.getBoolean("share", true))
            unbindService(conn);
        super.onStop();
    }

    @Override
    protected void findView() {
        tv_num = findView(R.id.item_count);
        lv_downlist = findView(R.id.lv_downlist);
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
    public boolean onItemLongClick( AdapterView<?> parent, View view, int fileposition, long id) {
        final DownData tmp = (DownData) parent.getItemAtPosition(fileposition);
         alertView =new AlertView("选择操作", null, "取消", null,
                new String[]{"打开", "删除"},
                this, AlertView.Style.ActionSheet, new com.bigkoo.alertview.OnItemClickListener() {
            public void onItemClick(Object o, int position) {

                switch(position){
                    case 0:
                        String download_Path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        String appname = FC_GetShareFile.getApplicationName(ShowDownFileActivity.this);
                        startActivity(Utils
                                .openFile(new File(download_Path + "/" + appname + "/download/" + tmp.getName())));
                        break;
                    case 1:
                        if (downLoadService.cancelDownload(tmp.getUri())) {
                            break;
                        } else {

                            try {
                                alreadydownList = Utils.getDownDataObject("alreadydownlist");
                            } catch (Exception e) {

                                alreadydownList = new ArrayList<>();
                            }
                            for (int i = 0; i < alreadydownList.size(); i++) {
                                if (alreadydownList.get(i).getUri().equals(tmp.getUri())) {
                                    alreadydownList.remove(i);
                                }
                            }

                            Utils.saveObject("alreadydownlist", alreadydownList);
                        }
                        try {
                            alreadydownList = Utils.getDownDataObject("alreadydownlist");
                        } catch (Exception e) {

                            alreadydownList = new ArrayList<DownData>();
                        }
                        HashMap<String, DownData> map = downLoadService.getAllDownStatus();
                        if (map != null) {
                            Iterator<String> tmp = map.keySet().iterator();
                            while (tmp.hasNext()) {
                                String uri = tmp.next();
                                DownData data = map.get(uri);
                                if (data.isDone()) {
                                }
                                downList.add(data);
                            }

                        }

                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putInt("key", UPDATE_LIST_DELETE);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                        break;
                }

            }
        });
        alertView.setCancelable(true);
        alertView.show();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(alertView != null)
           if(alertView.isShowing()){
               alertView.dismiss();
               return true;
           }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final DownData tmp = (DownData) parent.getItemAtPosition(position);
        alertView =new AlertView("选择操作", null, "取消", null,
                new String[]{"打开", "删除"},
                this, AlertView.Style.ActionSheet, new com.bigkoo.alertview.OnItemClickListener() {
            public void onItemClick(Object o, int position) {

                switch(position){
                    case 0:
                        String download_Path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        String appname = FC_GetShareFile.getApplicationName(ShowDownFileActivity.this);
                        startActivity(Utils
                                .openFile(new File(download_Path + "/" + appname + "/download/" + tmp.getName())));
                        break;
                    case 1:
                        if (downLoadService.cancelDownload(tmp.getUri())) {
                            break;
                        } else {

                            try {
                                alreadydownList = Utils.getDownDataObject("alreadydownlist");
                            } catch (Exception e) {

                                alreadydownList = new ArrayList<DownData>();
                            }
                            for (int i = 0; i < alreadydownList.size(); i++) {
                                if (alreadydownList.get(i).getUri().equals(tmp.getUri())) {
                                    alreadydownList.remove(i);
                                }
                            }

                            Utils.saveObject("alreadydownlist", alreadydownList);
                        }
                        try {
                            alreadydownList = Utils.getDownDataObject("alreadydownlist");
                        } catch (Exception e) {

                            alreadydownList = new ArrayList<DownData>();
                        }
                        HashMap<String, DownData> map = downLoadService.getAllDownStatus();
                        if (map != null) {
                            Iterator<String> tmp = map.keySet().iterator();
                            while (tmp.hasNext()) {
                                String uri = tmp.next();
                                DownData data = map.get(uri);
                                if (data.isDone()) {
                                }
                                downList.add(data);
                            }

                        }

                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putInt("key", UPDATE_LIST_DELETE);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                        break;
                }

            }
        });
        alertView.setCancelable(true);
        alertView.show();
    }

    private ServiceConnection conn = new ServiceConnection() {
        /** 获取服务对象时的操作 */
        public void onServiceConnected(ComponentName name, IBinder service) {
            downLoadService = ((DownLoadBinder) service).getService();
            new Thread(new DownloadProcessRunnable()).start();

        }

        /** 无法获取到服务对象时的操作 */
        public void onServiceDisconnected(ComponentName name) {
            downList.clear();
            downLoadService = null;
        }

    };

    class DownloadProcessRunnable implements Runnable {
        public void run() {
            int i = 0;
            int j = 0;
            do {
                downList.clear();
                i = 0;
                j = 0;
                try {
                    HashMap<String, DownData> map = downLoadService.getAllDownStatus();
                    if (map != null) {
                        Iterator<String> tmp = map.keySet().iterator();
                        while (tmp.hasNext()) {
                            j++;
                            String uri = tmp.next();
                            DownData data = map.get(uri);
                            if (data.isDone()) {
                                i++;
                            }
                            downList.add(data);
                        }

                    }
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("key", UPDATE_LIST);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

            } while (i < j);

        }
    }

    class MyDownHandler extends Handler {
        private WeakReference<ShowDownFileActivity> mActivity;

        public MyDownHandler(ShowDownFileActivity activity) {
            mActivity = new WeakReference<ShowDownFileActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ShowDownFileActivity theActivity = mActivity.get();
            if (theActivity != null) {
                super.handleMessage(msg);
                int key = msg.getData().getInt("key");
                switch (key) {
                    case UPDATE_LIST:
                        mAdapter.update(downList, downLoadService);
                        tv_num.setText(alreadydownList.size() + downList.size() + "项");
                        break;
                    case UPDATE_LIST_DELETE:
                        mAdapter.updatedelete(downList, alreadydownList, downLoadService);
                        tv_num.setText(alreadydownList.size() + downList.size() + "项");
                        break;
                }

            }
        }
    }
}
