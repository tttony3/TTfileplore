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
import com.changhong.ttfileplore.utils.Content;
import com.changhong.ttfileplore.utils.Utils;
import com.changhong.ttfileplore.view.CircleProgress;
import com.changhong.ttfileplore.view.RefreshListView;
import com.chobit.corestorage.CoreApp;
import com.konifar.fab_transformation.FabTransformation;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class QQListActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener,
        MoreDialogFragment.UpDate, PloreListAdapter.ImgOnClick, View.OnClickListener {
    private RefreshDataAsynTask mRefreshAsynTask;
    LayoutInflater inflater;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    CircleProgress mProgressView;
    View layout;
    private FloatingActionButton fab;
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
    private LinearLayout ll_btn;
    Button btn_push;
    Button btn_delete;
    Button btn_share;
    Button btn_select;
    Button btn_qr;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_classify_list);
        sharedPreferences = getSharedPreferences("set", Context.MODE_PRIVATE);
        inflater = LayoutInflater.from(this);
        findView();
        initView();
        initToolBar();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

                    ArrayList<File> wcfiles = new ArrayList<>();
                    tv_dir.setText("微信小视频");
                    file = new File("/storage/sdcard0/tencent/MicroMsg");
                    sdfile = new File("/storage/sdcard1/tencent/MicroMsg");
                    if (file.exists()) {
                        File[] mfils1 = file.listFiles();
                        for (File tmpfile : mfils1) {
                            if (tmpfile.getName().length() > 25 && tmpfile.isDirectory())
                                wcfiles.add(tmpfile);

                        }
                    }
                    if (sdfile.exists()) {
                        File[] mfils2 = sdfile.listFiles();
                        for (File tmpfile : mfils2) {
                            if (tmpfile.getName().length() > 25 && tmpfile.isDirectory())
                                wcfiles.add(tmpfile);

                        }
                    }
                    for (int i = 0; i < wcfiles.size(); i++) {

                        file = new File(wcfiles.get(i).getPath() + "/video");
                        PloreData mPloreData = new PloreData(file, true);
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
                        PloreData mPloreData = new PloreData(file, false);
                        files.addAll(mPloreData.getfiles());
                    }
                    if (sdfile.exists() && sdfile.isDirectory()) {
                        PloreData mPloreData = new PloreData(sdfile, false);
                        files.addAll(mPloreData.getfiles());
                    }

                    break;
                default:
                    break;
            }

            tv_count.setText(files.size() + "项");
            fab.setOnClickListener(this);
            btn_delete.setOnClickListener(this);
            btn_qr.setOnClickListener(this);
            btn_select.setOnClickListener(this);
            btn_share.setOnClickListener(this);
            btn_push.setOnClickListener(this);
            qqAdapter = new PloreListAdapter(QQListActivity.this, files, true, ImageLoader.getInstance());
            lv_classify.setAdapter(qqAdapter);
            lv_classify.setOnItemClickListener(this);
            lv_classify.setOnItemLongClickListener(this);
        }
    }

    @Override
    protected void findView() {
        getLayoutInflater();
        fab = findView(R.id.fab);
        lv_classify = (RefreshListView) findViewById(R.id.file_list);
        tv_dir = (TextView) findViewById(R.id.path);
        tv_count = (TextView) findViewById(R.id.item_count);
        ll_btn = findView(R.id.ll_btn);
        btn_push = findView(R.id.classify_btn_push);
        btn_delete = findView(R.id.classify_btn_delete);
        btn_qr = findView(R.id.classify_btn_qr);
        btn_select = findView(R.id.classify_btn_selectall);
        btn_share = findView(R.id.classify_btn_share);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = (File) parent.getItemAtPosition(position);
        if (file.isDirectory()) {
            PloreData mPloreData = new PloreData(file, true);
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
            if (flag == R.id.img_app) {
                return super.onKeyDown(keyCode, event);
            } else if (qqAdapter.isShow_cb()) {
                qqAdapter.setShow_cb(false);
                qqAdapter.notifyDataSetChanged();
                FabTransformation.with(fab)
                        .duration(350)
                        .transformFrom(ll_btn);
                return true;
            } else {
                File file = father.pollLast();
                if (file != null) {
                    PloreData mPloreData = new PloreData(file, true);
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
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (flag != R.id.img_app) {

            file = (File) parent.getItemAtPosition(position);
            MoreDialogFragment moreDialog = new MoreDialogFragment();
            moreDialog.setSource(view);
            Bundle bundle = new Bundle();
            bundle.putString("filePath", file.getPath());
            bundle.putInt("x", qqAdapter.x);
            bundle.putInt("y", qqAdapter.y);
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
        if (file.isDirectory()) {
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
        } else {
            startActivity(Utils.openFile(file));
        }
    }

    @Override
    public void onClick(View v) {
        Boolean[] mlist;
        switch (v.getId()) {
            case R.id.fab:
                if (flag == R.id.img_app) {
                    break;
                }
                if (qqAdapter.isShow_cb()) {
                    qqAdapter.setShow_cb(false);
                    FabTransformation.with(fab)
                            .transformFrom(ll_btn);

                } else {
                    qqAdapter.setShow_cb(true);
                    qqAdapter.notifyDataSetChanged();
                    FabTransformation.with(fab)
                            .duration(350)
                            .transformTo(ll_btn);
                }
                break;
            case R.id.classify_btn_selectall:
                qqAdapter.setAllSelect();
                qqAdapter.notifyDataSetChanged();
                break;
            case R.id.classify_btn_push:

                if (!sharedPreferences.getBoolean("share", true)) {
                    Toast.makeText(QQListActivity.this, "未开启共享", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    ArrayList<String> pushList = new ArrayList<>();
                    mlist = qqAdapter.getCheckBox_List();
                    for (int i = 0; i < mlist.length; i++) {
                        if (mlist[i]) {
                            File file = (File) qqAdapter.getItem(i);
                            if (!file.isDirectory()) {

                                MyApp myapp = (MyApp) getApplication();
                                String ip = myapp.getIp();
                                int port = myapp.getPort();
                                pushList.add("http://" + ip + ":" + port + file.getPath());
                            } else {
                                Toast.makeText(QQListActivity.this, "文件夹暂不支持推送", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (pushList.size() > 0) {
                        Intent intent = new Intent();
                        Bundle b = new Bundle();
                        b.putStringArrayList("pushList", pushList);
                        intent.putExtra("pushList", b);
                        intent.setClass(QQListActivity.this, ShowNetDevActivity.class);
                        startActivity(intent);
                    }

                }

                break;
            case R.id.classify_btn_share:
                if (!sharedPreferences.getBoolean("share", true)) {
                    Toast.makeText(QQListActivity.this, "未开启共享", Toast.LENGTH_SHORT).show();
                    break;
                }

                mlist = qqAdapter.getCheckBox_List();
                for (int i = 0; i < mlist.length; i++) {
                    if (mlist[i]) {
                        File file = (File) qqAdapter.getItem(i);

                        String s = CoreApp.mBinder.AddShareFile(file.getPath());
                        Toast.makeText(QQListActivity.this, "AddShareFile  " + s, Toast.LENGTH_SHORT).show();
                    }
                }


                break;
            case R.id.classify_btn_qr:
                break;
            case R.id.classify_btn_delete:
                mlist = qqAdapter.getCheckBox_List();
                for (int i = 0; i < mlist.length; i++) {
                    if (mlist[i]) {
                        File file = (File) qqAdapter.getItem(i);
                        if (file.delete()) {

                            Toast.makeText(QQListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(QQListActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                update();
                break;
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
