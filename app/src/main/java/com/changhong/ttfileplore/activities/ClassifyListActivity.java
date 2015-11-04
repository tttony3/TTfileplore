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

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ClassifyListActivity extends BaseActivity implements RefreshListView.IOnRefreshListener, MoreDialogFragment.UpDate {
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
                tv_count.setText(data.size() + " 项");
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_classify_list);
        flg = getIntent().getIntExtra("key", 0);
        MyApp myapp = (MyApp) getApplication();
        myapp.setContext(this);
        handler = new MyHandler(this);
        findView();
        initView(flg);
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
                MoreDialogFragment moreDialog = new MoreDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("filePath", content.getDir());
                moreDialog.setArguments(bundle);
                moreDialog.show(getFragmentManager(), "moreDialog");
                return true;
            }
        });

    }

    @Override
    public void findView() {
        inflater = getLayoutInflater();
        lv_classify = (RefreshListView) findViewById(R.id.file_list);
        tv_dir = (TextView) findViewById(R.id.path);
        tv_count = (TextView) findViewById(R.id.item_count);
        layout = inflater.inflate(R.layout.circle_progress, (ViewGroup) findViewById(R.id.rl_progress));
        builder = new AlertDialog.Builder(ClassifyListActivity.this).setView(layout);
        alertDialog = builder.create();
        mProgressView = (CircleProgress) layout.findViewById(R.id.progress);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
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
