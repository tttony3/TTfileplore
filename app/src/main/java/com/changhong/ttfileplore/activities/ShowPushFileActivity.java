package com.changhong.ttfileplore.activities;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.changhong.synergystorage.javadata.JavaFile;
import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.adapter.NetPushFileListAdapter;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.base.BaseActivity;
import com.changhong.ttfileplore.thread.SetMediaProgressBarThread;
import com.changhong.ttfileplore.utils.DownloadImageTask;
import com.changhong.ttfileplore.utils.MyCoreDownloadProgressCB;
import com.changhong.ttfileplore.utils.Utils;
import com.changhong.ttfileplore.view.AlertView;
import com.chobit.corestorage.CoreApp;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ShowPushFileActivity extends BaseActivity implements OnItemClickListener, AdapterView.OnItemLongClickListener {
    static private final int SHOW_PREVIEW_DIALOG = 0;
    static private final int RESET_BAR = 6;
    static private final int SET_TOTALTIME = 8;
    private int curtime = 0;
    SetMediaProgressBarThread thread;
    int time;
    private MediaPlayer mp = new MediaPlayer();
    private ListView lv_pushfile;
    private TextView tv_pushfilenum;
    private List<String> pushList;
    private NetPushFileListAdapter netPushFileListAdapter;
    private Handler handler;
    private View layout_download;
    private AlertDialog.Builder builder_download;
    private LayoutInflater inflater;
    private View layout_progress;
    private Builder builder_progress;
    private View layout_preview;
    private Builder builder_preview;
    private AlertDialog alertDialog_preview;
    private ImageView iv_preview;
    private View layout_mediaplayer;
    private Builder builder_mediaplayer;
    private AlertDialog alertDialog_mediaplayer;
    private ImageButton ib_start;
    private ImageButton ib_stop;
    private ProgressBar pb_media;
    private TextView tv_curtime;
    private TextView tv_totaltime;
    private boolean hasJson;
    private AlertView alertView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp myapp = (MyApp) getApplication();
        myapp.setContext(this);

        setContentView(R.layout.activity_push_file);
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        findView();
        Intent intent = getIntent();
        pushList = intent.getStringArrayListExtra("pushList");
        hasJson = intent.getBooleanExtra("hasJson", false); //pushList是否已经去掉头部json
        if (hasJson) {
            pushList.remove(0);
        }
        tv_pushfilenum.setText(pushList.size() + "项");
        netPushFileListAdapter = new NetPushFileListAdapter(pushList, this);
        lv_pushfile.setAdapter(netPushFileListAdapter);
        lv_pushfile.setOnItemClickListener(this);
        lv_pushfile.setOnItemLongClickListener(this);
        CoreApp.mBinder.setDownloadCBInterface(new MyCoreDownloadProgressCB(handler, this));

    }

    @Override
    protected void findView() {
        lv_pushfile = findView(R.id.lv_pushfile);
        findView(R.id.tv_pushfilepath);
        tv_pushfilenum = findView(R.id.tv_pushfilenum);
        handler = new MyPushHandler(this);
        findAlertDialog();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String fileLocation = (String) parent.getItemAtPosition(position);

        alertView = new AlertView("选择操作", null, "取消", null,
                new String[]{"打开", "下载"},
                ShowPushFileActivity.this, AlertView.Style.ActionSheet, new com.bigkoo.alertview.OnItemClickListener() {
            public void onItemClick(Object o, int position) {

                switch (position) {
                    case 0:
                        if (Utils.getMIMEType(fileLocation).equals("audio")) {
                            alertDialog_mediaplayer.show();
                            MediaButtonListener mediaButtonListener = new MediaButtonListener(fileLocation);
                            ib_stop.setOnClickListener(mediaButtonListener);
                            ib_start.setOnClickListener(mediaButtonListener);
                        } else if (Utils.getMIMEType(fileLocation).equals("video")) {
                            Intent intent = new Intent();
                            intent.putExtra("uri", fileLocation);
                            intent.setClass(ShowPushFileActivity.this, VideoActivity.class);
                            startActivity(intent);

                        } else if (Utils.getMIMEType(fileLocation).equals("image")) {
                            showPreviewDialog(fileLocation);
                        } else {
                            Toast.makeText(ShowPushFileActivity.this, "所选文件暂不支持在线打开", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        ArrayList<String> downlist = new ArrayList<>();
                        downlist.add(fileLocation);
                        Intent intent = new Intent("com.changhong.fileplore.service.DownLoadService");
                        intent.putStringArrayListExtra("downloadlist", downlist);
                        startService(intent);
                        Toast.makeText(ShowPushFileActivity.this, "已加入下载列表", Toast.LENGTH_SHORT).show();

                        break;
                }

            }
        });
        alertView.setCancelable(true);
        alertView.show();


    }

    protected void showPreviewDialog(String fileLocation) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("key", SHOW_PREVIEW_DIALOG);
        bundle.putString("path", fileLocation);
        msg.setData(bundle);
        handler.sendMessage(msg);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (alertView != null) {
                if (alertView.isShowing()) {
                    alertView.dismiss();
                    return true;
                }
            }
            if (netPushFileListAdapter.isshowcb()) {
                netPushFileListAdapter.setIsshowcb(false);
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
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
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ((NetPushFileListAdapter) parent.getAdapter()).setIsshowcb(true);
        return true;
    }

    class MyPushHandler extends Handler {
        private WeakReference<ShowPushFileActivity> mActivity;

        public MyPushHandler(ShowPushFileActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ShowPushFileActivity theActivity = mActivity.get();
            if (theActivity != null) {
                super.handleMessage(msg);
                int key = msg.getData().getInt("key");
                switch (key) {

                    case SHOW_PREVIEW_DIALOG:
                        iv_preview.setImageResource(R.drawable.picload);
                        String path = msg.getData().getString("path");
                        new DownloadImageTask(iv_preview, 4).execute(path);
                        alertDialog_preview.show();
                        break;
                    case SetMediaProgressBarThread.UPDATE_BAR:

                        pb_media.setProgress(msg.getData().getInt("value"));
                        curtime = curtime + 1;
                        tv_curtime.setText(curtime / 60 + ":" + (curtime - (curtime / 60) * 60));
                        break;
                    case RESET_BAR:

                        pb_media.setProgress(0);
                        tv_curtime.setText("00:00");
                        tv_totaltime.setText("00:00");
                        curtime = 0;
                        break;
                    case SET_TOTALTIME:
                        tv_totaltime.setText(time / 60 + ":" + (time - (time / 60) * 60));
                        break;
                    default:
                        break;
                }

            }
        }
    }


    class MediaButtonListener implements OnClickListener {
        String file;

        MediaButtonListener(String file) {
            this.file = file;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.media_play:
                    try {
                        if (thread != null)
                            thread.setIsvalid(false);
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putInt("key", RESET_BAR);
                        msg.setData(bundle);
                        handler.sendMessage(msg);

                        mp.reset();
                        mp.setDataSource(file);
                        mp.prepare();
                        mp.start();
                        thread = new SetMediaProgressBarThread(handler, pb_media, time);
                        thread.start();

                    } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                        e.printStackTrace();
                    }
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        public void onPrepared(MediaPlayer mp) {
                            time = mp.getDuration() / 1000;
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putInt("key", SET_TOTALTIME);
                            bundle.putInt("value", time);
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        }
                    });
                    mp.setOnCompletionListener(new OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                            thread.setIsvalid(false);
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putInt("key", RESET_BAR);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    });

                    break;
                case R.id.media_stop:

                    if (mp.isPlaying())
                        mp.stop();
                    thread.setIsvalid(false);
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("key", RESET_BAR);
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                    break;
                default:
                    break;
            }

        }

    }

    private void findAlertDialog() {
        inflater = getLayoutInflater();
        layout_progress = inflater.inflate(R.layout.circle_progress, (ViewGroup) findViewById(R.id.rl_progress));
        builder_progress = new AlertDialog.Builder(this).setView(layout_progress);
        builder_progress.create();


        layout_preview = inflater.inflate(R.layout.dialog_preview, (ViewGroup) findViewById(R.id.rl_preview));
        builder_preview = new AlertDialog.Builder(this).setView(layout_preview);
        alertDialog_preview = builder_preview.create();
        iv_preview = (ImageView) layout_preview.findViewById(R.id.iv_preview);

        layout_mediaplayer = inflater.inflate(R.layout.media_player, (ViewGroup) findViewById(R.id.rl_mediaplayer));
        builder_mediaplayer = new AlertDialog.Builder(this).setView(layout_mediaplayer);
        alertDialog_mediaplayer = builder_mediaplayer.create();
        ib_start = (ImageButton) layout_mediaplayer.findViewById(R.id.media_play);
        ib_stop = (ImageButton) layout_mediaplayer.findViewById(R.id.media_stop);
        pb_media = (ProgressBar) layout_mediaplayer.findViewById(R.id.media_bar);
        tv_curtime = (TextView) layout_mediaplayer.findViewById(R.id.tv_curtime);
        tv_totaltime = (TextView) layout_mediaplayer.findViewById(R.id.tv_totaltime);

        layout_download = inflater.inflate(R.layout.dialog_download, null);
        builder_download = new AlertDialog.Builder(this).setView(layout_download);
        builder_download.create();


    }
}
