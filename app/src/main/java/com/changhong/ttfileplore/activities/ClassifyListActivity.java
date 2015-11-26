package com.changhong.ttfileplore.activities;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.changhong.ttfileplore.adapter.ClassifyListAdapter;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.base.BaseActivity;
import com.changhong.ttfileplore.fragment.MoreDialogFragment;
import com.changhong.ttfileplore.utils.Content;
import com.changhong.ttfileplore.utils.Utils;
import com.changhong.ttfileplore.view.CircleProgress;
import com.changhong.ttfileplore.view.RefreshListView;
import com.chobit.corestorage.CoreApp;
import com.changhong.ttfileplore.R;
import com.konifar.fab_transformation.FabTransformation;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ClassifyListActivity extends BaseActivity implements RefreshListView.IOnRefreshListener, MoreDialogFragment.UpDate, View.OnClickListener {
    static private final int APK = 1;
    static private final int DOC = 2;
    static private final int ZIP = 3;
    static private final int MUSIC = 4;
    private RefreshDataAsynTask mRefreshAsynTask;
    ArrayList<Content> results;
    LayoutInflater inflater;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    CircleProgress mProgressView;
    View layout;
    RefreshListView lv_classify;
    TextView tv_dir;
    TextView tv_count;
    int flg;
    MyHandler handler;
    ClassifyListAdapter listAdapter;
    FloatingActionButton fab;
    LinearLayout ll_btn;
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
        flg = getIntent().getIntExtra("key", 0);
        MyApp myapp = (MyApp) getApplication();
        MyApp.setContext(this);
        handler = new MyHandler(this);
        sharedPreferences = getSharedPreferences("set", Context.MODE_PRIVATE);
        findView();
        initView(flg);
        initToolBar();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initView(final int flg) {

        switch (flg) {

            case R.id.img_music:
                mProgressView.startAnim();
                alertDialog.show();
                new Thread(new GetRunnable(MUSIC, false)).start();

                break;
            case R.id.img_txt:
                mProgressView.startAnim();
                alertDialog.show();
                new Thread(new GetRunnable(DOC, false)).start();

                break;
            case R.id.img_zip:
                mProgressView.startAnim();
                alertDialog.show();
                new Thread(new GetRunnable(ZIP, false)).start();

                break;
            case R.id.img_apk:
                mProgressView.startAnim();
                alertDialog.show();
                new Thread(new GetRunnable(APK, false)).start();

                break;
            case R.id.img_app:


                break;

            case R.id.img_wechat:


                break;

            default:
                break;
        }
        fab.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_qr.setOnClickListener(this);
        btn_select.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        btn_push.setOnClickListener(this);
        lv_classify.setOnRefreshListener(this);
        lv_classify.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Content content = (Content) parent.getItemAtPosition(position);
                File file = new File(content.getDir());
                Intent intent = Utils.openFile(file);
                startActivity(intent);

            }

        });

        lv_classify.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Content content = (Content) parent.getItemAtPosition(position);
                if (content.getDir() == null)
                    return true;
                MoreDialogFragment moreDialog = new MoreDialogFragment();
                moreDialog.setSource(view);
                Bundle bundle = new Bundle();
                bundle.putString("filePath", content.getDir());
                bundle.putInt("x", listAdapter.x);
                bundle.putInt("y", listAdapter.y);
                moreDialog.setArguments(bundle);
                moreDialog.show(getFragmentManager(), "moreDialog");

                return true;
            }
        });

    }

    @Override
    public void findView() {
        ll_btn = findView(R.id.ll_btn);
        inflater = getLayoutInflater();
        fab = findView(R.id.fab);
        lv_classify = (RefreshListView) findViewById(R.id.file_list);
        tv_dir = (TextView) findViewById(R.id.path);
        tv_count = (TextView) findViewById(R.id.item_count);
        layout = inflater.inflate(R.layout.circle_progress, (ViewGroup) findViewById(R.id.rl_progress));
        builder = new AlertDialog.Builder(ClassifyListActivity.this).setView(layout);
        alertDialog = builder.create();
        mProgressView = (CircleProgress) layout.findViewById(R.id.progress);
        btn_push = findView(R.id.classify_btn_push);
        btn_delete = findView(R.id.classify_btn_delete);
        btn_qr = findView(R.id.classify_btn_qr);
        btn_select = findView(R.id.classify_btn_selectall);
        btn_share = findView(R.id.classify_btn_share);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (listAdapter.isShow_cb()) {
                listAdapter.setShow_cb(false);
                FabTransformation.with(fab)
                        .duration(350)
                        .transformFrom(ll_btn);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    class GetRunnable implements Runnable {
        int type;
        boolean reseach;

        GetRunnable(int type, boolean reseach) {
            this.type = type;
            this.reseach = reseach;
        }

        @Override
        public void run() {

            Message msg = new Message();
            Bundle data = new Bundle();

            if (type == APK) {
                results = Utils.getApk(ClassifyListActivity.this, reseach);
                data.putSerializable("data", results);
                data.putInt("tag", APK);
            } else if (type == DOC) {
                results = Utils.getDoc(ClassifyListActivity.this, reseach);
                data.putSerializable("data", results);
                data.putInt("tag", DOC);
            } else if (type == ZIP) {
                results = Utils.getZip(ClassifyListActivity.this, reseach);
                data.putSerializable("data", results);
                data.putInt("tag", ZIP);
            } else if (type == MUSIC) {
                results = Utils.getMusic(ClassifyListActivity.this);
                data.putSerializable("data", results);
                data.putInt("tag", MUSIC);
            }

            msg.setData(data);
            handler.sendMessage(msg);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reseach, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_reseach) {

            switch (flg) {
                case R.id.img_music:
                    ArrayList<Content> musics = Utils.getMusic(this);
                    listAdapter.updateList(musics);
                    tv_count.setText(musics.size() + " 项");
                    break;
                case R.id.img_txt:
                    mProgressView.startAnim();
                    alertDialog.show();
                    new Thread(new GetRunnable(DOC, true)).start();

                    break;
                case R.id.img_zip:
                    mProgressView.startAnim();
                    alertDialog.show();
                    new Thread(new GetRunnable(ZIP, true)).start();

                    break;
                case R.id.img_apk:
                    mProgressView.startAnim();
                    alertDialog.show();
                    new Thread(new GetRunnable(APK, true)).start();

                    break;

                default:
                    break;
            }


        } else if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void update() {

        switch (flg) {
            case R.id.img_music:
                ArrayList<Content> musics = Utils.getMusic(this);
                listAdapter.updateList(musics);
                tv_count.setText(musics.size() + " 项");
                break;
            case R.id.img_txt:
                mProgressView.startAnim();
                alertDialog.show();
                new Thread(new GetRunnable(DOC, true)).start();

                break;
            case R.id.img_zip:
                mProgressView.startAnim();
                alertDialog.show();
                new Thread(new GetRunnable(ZIP, true)).start();

                break;
            case R.id.img_apk:
                mProgressView.startAnim();
                alertDialog.show();
                new Thread(new GetRunnable(APK, true)).start();

                break;

            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        Boolean[] mlist;
        switch (v.getId()) {
            case R.id.fab:
                if (listAdapter.isShow_cb()) {
                    listAdapter.setShow_cb(false);
                    FabTransformation.with(fab)
                            .transformFrom(ll_btn);

                } else {
                    listAdapter.setShow_cb(true);
                    FabTransformation.with(fab)
                            .duration(350)
                            .transformTo(ll_btn);
                }
                break;
            case R.id.classify_btn_selectall:
                listAdapter.setAllSelect();
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.classify_btn_push:

                if (!sharedPreferences.getBoolean("share", true)) {
                    Toast.makeText(ClassifyListActivity.this, "未开启共享", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    ArrayList<String> pushList = new ArrayList<>();
                    mlist = listAdapter.getCheckBox_List();
                    for (int i = 0; i < mlist.length; i++) {
                        if (mlist[i]) {
                            Content file = (Content) listAdapter.getItem(i);
                            if (!file.isDirectory()) {

                                MyApp myapp = (MyApp) getApplication();
                                String ip = myapp.getIp();
                                int port = myapp.getPort();
                                pushList.add("http://" + ip + ":" + port + file.getPath());
                            } else {
                                Toast.makeText(ClassifyListActivity.this, "文件夹暂不支持推送", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (pushList.size() > 0) {
                        Intent intent = new Intent();
                        Bundle b = new Bundle();
                        b.putStringArrayList("pushList", pushList);
                        intent.putExtra("pushList", b);
                        intent.setClass(ClassifyListActivity.this, ShowNetDevActivity.class);
                        startActivity(intent);
                    }

                }

                break;
            case R.id.classify_btn_share:
                if (!sharedPreferences.getBoolean("share", true)) {
                    Toast.makeText(ClassifyListActivity.this, "未开启共享", Toast.LENGTH_SHORT).show();
                    break;
                }

                mlist = listAdapter.getCheckBox_List();
                for (int i = 0; i < mlist.length; i++) {
                    if (mlist[i]) {
                        Content file = (Content) listAdapter.getItem(i);

                        String s = CoreApp.mBinder.AddShareFile(file.getPath());
                        Toast.makeText(ClassifyListActivity.this, "AddShareFile  " + s, Toast.LENGTH_SHORT).show();
                    }
                }


                break;
            case R.id.classify_btn_qr:
                break;
            case R.id.classify_btn_delete:
                mlist = listAdapter.getCheckBox_List();
                for (int i = 0; i < mlist.length; i++) {
                    if (mlist[i]) {
                        Content file = (Content) listAdapter.getItem(i);
                        if (file.delete()) {

                            Toast.makeText(ClassifyListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ClassifyListActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                update();
                break;
        }
    }

    class MyHandler extends Handler {
        WeakReference<ClassifyListActivity> mActivity;

        MyHandler(ClassifyListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            ClassifyListActivity theActivity = mActivity.get();
            if (theActivity != null) {
                super.handleMessage(msg);
                ArrayList<Content> data = null;
                switch (msg.getData().getInt("tag")) {
                    case APK:
                        data = (ArrayList<Content>) msg.getData().get("data");
                        theActivity.listAdapter = new ClassifyListAdapter(data, theActivity, R.id.img_apk);
                        theActivity.lv_classify.setAdapter(theActivity.listAdapter);
                        listAdapter.notifyDataSetChanged();
                        break;
                    case DOC:
                        data = (ArrayList<Content>) msg.getData().get("data");
                        theActivity.listAdapter = new ClassifyListAdapter(data, theActivity, R.id.img_txt);
                        theActivity.lv_classify.setAdapter(theActivity.listAdapter);
                        listAdapter.notifyDataSetChanged();
                        break;
                    case ZIP:
                        data = (ArrayList<Content>) msg.getData().get("data");
                        theActivity.listAdapter = new ClassifyListAdapter(data, theActivity, R.id.img_zip);
                        theActivity.lv_classify.setAdapter(theActivity.listAdapter);
                        listAdapter.notifyDataSetChanged();
                        break;
                    case MUSIC:
                        data = (ArrayList<Content>) msg.getData().get("data");
                        theActivity.listAdapter = new ClassifyListAdapter(data, theActivity, R.id.img_music);
                        theActivity.lv_classify.setAdapter(theActivity.listAdapter);
                        listAdapter.notifyDataSetChanged();
                        break;

                    default:
                        break;
                }

                if (alertDialog.isShowing()) {
                    mProgressView.stopAnim();
                    alertDialog.dismiss();
                }
                //	lv_classify.setAdapter(listAdapter);
                if(data!=null)
                  tv_count.setText(data.size() + " 项");
            }

        }
    }

    class RefreshDataAsynTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            switch (flg) {

                case R.id.img_txt:

                    new GetRunnable(DOC, true).run();

                    break;
                case R.id.img_zip:

                    new GetRunnable(ZIP, true).run();

                    break;
                case R.id.img_apk:

                    new GetRunnable(APK, true).run();

                    break;
                default:
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (flg == R.id.img_music) {
                ArrayList<Content> musics = Utils.getMusic(ClassifyListActivity.this);
                listAdapter = new ClassifyListAdapter(musics, ClassifyListActivity.this, R.id.img_music);
                listAdapter.updateList(musics);
                tv_count.setText(musics.size() + " 项");
            }

            lv_classify.onRefreshComplete();
        }

    }

    @Override
    public void OnRefresh() {
        mRefreshAsynTask = new RefreshDataAsynTask();
        mRefreshAsynTask.execute();

    }

}
