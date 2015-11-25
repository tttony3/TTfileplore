package com.changhong.ttfileplore.activities;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.changhong.ttfileplore.base.BaseActivity;
import com.changhong.ttfileplore.fragment.MoreDialogFragment;
import com.changhong.ttfileplore.utils.Utils;
import com.changhong.ttfileplore.view.CircleProgress;
import com.changhong.ttfileplore.view.PopupMoreDialog;
import com.chobit.corestorage.CoreApp;
import com.changhong.ttfileplore.adapter.*;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.utils.Content;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ClassifyGridActivity extends BaseActivity implements MoreDialogFragment.UpDate {
    GridView gv_classify;
    TextView tv_dir;
    int flg;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    CircleProgress mProgressView;
    ClassifyGridAdapter gridAdapter;
    View layout;
    LayoutInflater inflater;
    ArrayList<Content> results;
    MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_classify_grid);
        MyApp myapp = (MyApp) getApplication();
        MyApp.setContext(this);
        flg = getIntent().getIntExtra("key", 0);
        findView();
        initView(flg);
        initToolBar();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void findView() {
        inflater = getLayoutInflater();
        gv_classify = (GridView) findViewById(R.id.gv_classify);
        tv_dir = (TextView) findViewById(R.id.tv_classify_dir);
        handler = new MyHandler(this);
        layout = inflater.inflate(R.layout.circle_progress, (ViewGroup) findViewById(R.id.rl_progress));
        builder = new AlertDialog.Builder(ClassifyGridActivity.this).setView(layout);
        alertDialog = builder.create();
        mProgressView = (CircleProgress) layout.findViewById(R.id.progress);
    }

    void initView(int flg) {
        mProgressView.startAnim();
        alertDialog.show();
        switch (flg) {

            case R.id.img_movie:
                new Thread(new GridRunnable(R.id.img_movie)).start();
                gv_classify.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Content content = (Content) parent.getItemAtPosition(position);
                        File file = new File(content.getDir());
                        Intent intent = Utils.openFile(file);
                        startActivity(intent);

                    }
                });
                gv_classify.setOnItemLongClickListener(new OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        final Content content = (Content) parent.getItemAtPosition(position);
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 0.5f;
                        getWindow().setAttributes(lp);
                        PopupMoreDialog p = new PopupMoreDialog(ClassifyGridActivity.this, Utils.dpTopx(150, ClassifyGridActivity.this), ViewGroup.LayoutParams.WRAP_CONTENT,
                                true, content.getDir());
                        int[] viewLocation = new int[2];
                        view.getLocationInWindow(viewLocation);
                        int viewX = viewLocation[0]; // x 坐标
                        int viewY = viewLocation[1]; // y 坐标
                        Point point = new Point();
                        getWindow().getWindowManager().getDefaultDisplay().getSize(point);
                        if (gridAdapter.x != 0 && gridAdapter.y != 0) {
                            if (point.x - viewX - gridAdapter.x > Utils.dpTopx(150, ClassifyGridActivity.this)) {
                                if (point.y - viewY - gridAdapter.y > Utils.dpTopx(240, ClassifyGridActivity.this)) {
                                    p.setAnimationStyle(R.style.PopupAnimationTop);
                                    p.showAtLocation(view, Gravity.NO_GRAVITY, viewX + gridAdapter.x, viewY + gridAdapter.y);
                                } else {
                                    p.setAnimationStyle(R.style.PopupAnimationBottom);
                                    p.showAtLocation(view, Gravity.NO_GRAVITY, viewX + gridAdapter.x, viewY - Utils.dpTopx(240, ClassifyGridActivity.this) + gridAdapter.y);
                                }
                            } else {
                                if (point.y - viewY - gridAdapter.y > Utils.dpTopx(240, ClassifyGridActivity.this)) {
                                    p.setAnimationStyle(R.style.PopupAnimationTopRight);
                                    p.showAtLocation(view, Gravity.NO_GRAVITY, viewX + gridAdapter.x - Utils.dpTopx(150, ClassifyGridActivity.this), viewY + gridAdapter.y);
                                } else {
                                    p.setAnimationStyle(R.style.PopupAnimationBottomRight);
                                    p.showAtLocation(view, Gravity.NO_GRAVITY, viewX + gridAdapter.x - Utils.dpTopx(150, ClassifyGridActivity.this), viewY - Utils.dpTopx(240, ClassifyGridActivity.this) + gridAdapter.y);
                                }
                            }
                        } else {
                            if (point.x - viewX > Utils.dpTopx(150, ClassifyGridActivity.this)) {
                                if (point.y - viewY > Utils.dpTopx(240, ClassifyGridActivity.this)) {
                                    p.setAnimationStyle(R.style.PopupAnimationTop);
                                    p.showAsDropDown(view, Utils.dpTopx(80, ClassifyGridActivity.this), -Utils.dpTopx(80, ClassifyGridActivity.this));
                                } else {
                                    p.setAnimationStyle(R.style.PopupAnimationBottom);
                                    p.showAtLocation(view, Gravity.NO_GRAVITY, viewX + Utils.dpTopx(80, ClassifyGridActivity.this),
                                            viewY - Utils.dpTopx(240, ClassifyGridActivity.this) + Utils.dpTopx(80, ClassifyGridActivity.this));
                                }
                            } else {
                                if (point.y - viewY > Utils.dpTopx(240, ClassifyGridActivity.this)) {
                                    p.setAnimationStyle(R.style.PopupAnimationTopRight);
                                    p.showAsDropDown(view, -Utils.dpTopx(80, ClassifyGridActivity.this), -Utils.dpTopx(80, ClassifyGridActivity.this));
                                } else {
                                    p.setAnimationStyle(R.style.PopupAnimationBottomRight);
                                    p.showAtLocation(view, Gravity.NO_GRAVITY, viewX - Utils.dpTopx(80, ClassifyGridActivity.this),
                                            viewY - Utils.dpTopx(240, ClassifyGridActivity.this) + Utils.dpTopx(80, ClassifyGridActivity.this));
                                }
                            }
                        }
                        p.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                WindowManager.LayoutParams lp = getWindow().getAttributes();
                                lp.alpha = 1f;
                                getWindow().setAttributes(lp);
                            }
                        });

                        return true;
                    }
                });
                break;
            case R.id.img_photo:
                // CircleProgress cp = new CircleProgress(this);
                new Thread(new GridRunnable(R.id.img_photo)).start();
//			ArrayList<Content> photos = Utils.getPhotoCata(this);
//			gridAdapter = new ClassifyGridAdapter(photos, this, R.id.img_photo);
//			gv_classify.setAdapter(gridAdapter);
                gv_classify.setOnItemClickListener(new OnPhotoItemClickListener());
                break;

            default:
                break;
        }
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
        new Thread(new GridRunnable(R.id.img_movie)).start();
    }

    class OnPhotoItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Content content = (Content) parent.getItemAtPosition(position);
            Intent intent = new Intent();
            String[] s = new String[]{content.getDir(), content.getTitle()};
            intent.putExtra("content", s);
            //    intent.setClass(ClassifyGridActivity.this, PhotoGridActivity.class);
            intent.setClass(ClassifyGridActivity.this, PhotoActivity.class);
            startActivity(intent);

        }
    }

    class GridRunnable implements Runnable {
        int id;

        public GridRunnable(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            Message msg = new Message();
            Bundle data = new Bundle();
            switch (id) {
                case R.id.img_movie:
                    results = Utils.getVideo(ClassifyGridActivity.this);
                    data.putSerializable("data", results);
                    data.putInt("tag", R.id.img_movie);
                    break;
                case R.id.img_photo:
                    results = Utils.getPhotoCata(ClassifyGridActivity.this);
                    data.putSerializable("data", results);
                    data.putInt("tag", R.id.img_photo);
                    break;
                default:
                    break;
            }
            msg.setData(data);
            handler.sendMessage(msg);
        }

    }

    static class MyHandler extends Handler {
        WeakReference<ClassifyGridActivity> mActivity;

        MyHandler(ClassifyGridActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            ClassifyGridActivity theActivity = mActivity.get();
            if (theActivity != null) {
                super.handleMessage(msg);
                ArrayList<Content> data;
                switch (msg.getData().getInt("tag")) {
                    case R.id.img_movie:
                        data = (ArrayList<Content>) msg.getData().get("data");
                        theActivity.gridAdapter = new ClassifyGridAdapter(data, theActivity, R.id.img_movie);
                        theActivity.gv_classify.setAdapter(theActivity.gridAdapter);
                        theActivity.gridAdapter.notifyDataSetChanged();
                        break;
                    case R.id.img_photo:
                        data = (ArrayList<Content>) msg.getData().get("data");
                        theActivity.gridAdapter = new ClassifyGridAdapter(data, theActivity, R.id.img_movie);
                        theActivity.gv_classify.setAdapter(theActivity.gridAdapter);
                        theActivity.gridAdapter.notifyDataSetChanged();
                        break;

                    default:
                        break;
                }
                if (theActivity.alertDialog.isShowing()) {
                    theActivity.mProgressView.stopAnim();
                    theActivity.alertDialog.dismiss();
                }
            }

        }
    }
}
