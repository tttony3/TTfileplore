package com.changhong.ttfileplore.activities;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.changhong.alljoyn.simpleclient.DeviceInfo;
import com.changhong.alljoyn.simpleservice.FC_GetShareFile;
import com.changhong.ttfileplore.R;

import com.changhong.ttfileplore.adapter.NetShareFileListAdapter;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.base.BaseActivity;
import com.changhong.ttfileplore.data.DownData;
import com.changhong.ttfileplore.service.DownLoadService;
import com.changhong.ttfileplore.service.DownLoadService.DownLoadBinder;
import com.changhong.ttfileplore.thread.SetMediaProgressBarThread;
import com.changhong.ttfileplore.utils.DownloadImageTask;
import com.changhong.ttfileplore.utils.MyCoreDownloadProgressCB;
import com.changhong.ttfileplore.utils.Utils;
import com.changhong.ttfileplore.view.AlertView;
import com.changhong.ttfileplore.view.CircleProgress;
import com.changhong.synergystorage.javadata.JavaFile;
import com.changhong.synergystorage.javadata.JavaFolder;
import com.chobit.corestorage.CoreApp;
import com.chobit.corestorage.CoreShareFileListener;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toolbar;

public class ShowNetFileActivity extends BaseActivity implements AdapterView.OnItemLongClickListener {

    static private final int UPDATE_LIST = 1;
    static private final int SHOW_DIALOG = 2;
    static private final int DISMISS_DIALOG = 3;
    static private final int SHOW_PREVIEW_DIALOG = 4;
    static private final int RESET_BAR = 6;
    static private final int SET_TOTALTIME = 8;

    private JavaFile file;
    private JavaFolder curfile = null;
    private List<JavaFile> shareFileList;
    private List<JavaFolder> shareFolderList;
    private ListView lv_sharepath;
    private TextView tv_path;
    private NetShareFileListAdapter netShareFileListAdapter;
    private MyOnItemClickListener myOnItemClickListener;
    private Handler handler;
    private TextView filenum;
    private AlertDialog alertDialog_progress;
    private AlertDialog.Builder builder_progress;
    private CircleProgress mProgressView;
    private View layout_progress;
    private LinkedList<JavaFolder> fatherList = new LinkedList<JavaFolder>();
    private View layout_preview;
    private AlertDialog alertDialog_preview;
    private AlertDialog.Builder builder_preview;
    private ImageView iv_preview;
    private AlertView alertView;
    private View layout_mediaplayer;
    private AlertDialog alertDialog_mediaplayer;
    private AlertDialog.Builder builder_mediaplayer;
    private ImageButton ib_start;
    private ImageButton ib_stop;
    private ProgressBar pb_media;
    private TextView tv_curtime;
    private TextView tv_totaltime;
    private int curtime = 0;
    private LayoutInflater inflater;
    private DeviceInfo devInfo;
    private MediaPlayer mp = new MediaPlayer();

    private View layout_download;
    private AlertDialog alertDialog_download;
    private AlertDialog.Builder builder_download;
    private ProgressBar pb_download;
    private TextView tv_download;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_file);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MyApp myapp = (MyApp) getApplication();
        myapp.setContext(this);
        findView();


        devInfo = ((MyApp) getApplicationContext()).devinfo;

        CoreApp.mBinder.setShareFileListener(shareListener);
        CoreApp.mBinder.ConnectDeivce(devInfo);

        netShareFileListAdapter = new NetShareFileListAdapter(shareFileList, shareFolderList, devInfo, this);
        myOnItemClickListener = new MyOnItemClickListener();

        lv_sharepath.setAdapter(netShareFileListAdapter);
        lv_sharepath.setOnItemClickListener(myOnItemClickListener);
        lv_sharepath.setOnItemLongClickListener(this);
    }

    @Override
    protected void findView() {
        handler = new MyNetHandler(this);
        tv_path = (TextView) findViewById(R.id.path);
        filenum = (TextView) findViewById(R.id.netfile_num);
        lv_sharepath = (ListView) findViewById(R.id.lv_netsharepath);
        findAlertDialog();

    }

    private void findAlertDialog() {
        inflater = getLayoutInflater();
        layout_progress = inflater.inflate(R.layout.circle_progress, (ViewGroup) findViewById(R.id.rl_progress));
        builder_progress = new AlertDialog.Builder(this).setView(layout_progress);
        alertDialog_progress = builder_progress.create();
        mProgressView = (CircleProgress) layout_progress.findViewById(R.id.progress);

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
        alertDialog_download = builder_download.create();

        pb_download = (ProgressBar) layout_download.findViewById(R.id.download_bar);
        tv_download = (TextView) layout_download.findViewById(R.id.tv_process);

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
            if (netShareFileListAdapter.isshowcb()) {
                netShareFileListAdapter.setIsshowcb(false);
                return true;
            } else {
                if (fatherList.size() >= 1) {
                    JavaFolder folder = fatherList.pollLast();

                    showDialog();
                    curfile = null;
                    CoreApp.mBinder.getFolderChildren(devInfo, folder, folder);
                } else {
                    CoreApp.mBinder.DisSession();
                    finish();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private CoreShareFileListener shareListener = new CoreShareFileListener() {

        @Override
        public void updateShareContent(JavaFolder folder) {
            if (folder != null) {

                List<JavaFile> shareFileList1 = folder.getSubFileList();
                List<JavaFolder> shareFolderList1 = folder.getSubFolderList();
                int n = 0;
                if (shareFileList1 != null) {
                    n = n + shareFileList1.size();
                }
                if (shareFolderList1 != null) {
                    n = n + shareFolderList1.size();
                }
                if (n == 0) {
                    Toast.makeText(ShowNetFileActivity.this, "空文件夹~", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    return;
                }
                shareFileList = shareFileList1;
                shareFolderList = shareFolderList1;
                tv_path.setText(folder.getLocation());

                if (curfile != null && !fatherList.contains(curfile)) {
                    fatherList.addLast(curfile);

                }
                curfile = folder;

                if (null != folder.getParent()) {

                }
                filenum.setText(n + "项");
                if (shareFileList != null || shareFolderList != null) {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("key", UPDATE_LIST);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            } else {
                Toast.makeText(ShowNetFileActivity.this, "空文件夹", Toast.LENGTH_SHORT).show();

            }

            dismissDialog();
        }

        @Override
        public void stopWaiting() {
            dismissDialog();

        }

        @Override
        public void startWaiting() {
            showDialog();

        }

        @Override
        public void updateImageThumbNails(Bitmap arg1) {

        }
    };

    int time = 0;
    SetMediaProgressBarThread thread;

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ((NetShareFileListAdapter) parent.getAdapter()).setIsshowcb(true);
        return false;
    }

    class MyOnItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (filenum.getText().equals("0项")) {
                Toast.makeText(ShowNetFileActivity.this, "空文件夹", Toast.LENGTH_SHORT).show();
                return;
            }
            JavaFolder folder;

            if (shareFolderList != null) {
                if (position < shareFolderList.size()) {
                    showDialog();
                    folder = (JavaFolder) parent.getItemAtPosition(position);
                    CoreApp.mBinder.getFolderChildren(devInfo, folder, folder);


                } else
                    setFileOnClick(parent, position);
            } else {
                setFileOnClick(parent, position);
            }

        }

        private void setFileOnClick(AdapterView<?> parent, int position) {
            if (filenum.getText().equals("0项")) {
                Toast.makeText(ShowNetFileActivity.this, "空文件夹", Toast.LENGTH_SHORT).show();
                return;
            }
            file = (JavaFile) parent.getItemAtPosition(position);

            alertView = new AlertView("选择操作", null, "取消", null,
                    new String[]{"打开", "下载"},
                    ShowNetFileActivity.this, AlertView.Style.ActionSheet, new com.bigkoo.alertview.OnItemClickListener() {
                public void onItemClick(Object o, int position) {

                    switch (position) {
                        case 0:
                            if (file.getFileType() == JavaFile.FileType.IMAGE) {
                                showPreviewDialog(file.getLocation());
                            } else if (file.getFileType() == JavaFile.FileType.AUDIO) {
                                Intent intent = new Intent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction(android.content.Intent.ACTION_VIEW);

                                String type = "audio/*";
                                intent.setDataAndType(Uri.parse(devInfo.getM_httpserverurl() + file.getLocation()), type);
                                startActivity(intent);
//                                alertDialog_mediaplayer.show();
//                                MediaButtonListener mediaButtonListener = new MediaButtonListener(file);
//                                ib_stop.setOnClickListener(mediaButtonListener);
//                                ib_start.setOnClickListener(mediaButtonListener);

                            } else if (file.getFileType() == JavaFile.FileType.VIDEO) {
                                Intent intent = new Intent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction(android.content.Intent.ACTION_VIEW);

                                String type = "video/*";
                                intent.setDataAndType(Uri.parse(devInfo.getM_httpserverurl() + file.getLocation()), type);
                                startActivity(intent);
//                                Intent intent = new Intent();
//                                intent.putExtra("uri", devInfo.getM_httpserverurl() + file.getLocation());
//                                intent.setClass(ShowNetFileActivity.this, VideoActivity.class);
//                                startActivity(intent);
                            } else {
                                Toast.makeText(ShowNetFileActivity.this, "所选文件暂不支持在线打开", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:

                            ArrayList<String> downlist = new ArrayList<>();
                            downlist.add(devInfo.getM_httpserverurl() + file.getLocation());
                            Intent intent = new Intent("com.changhong.fileplore.service.DownLoadService");
                            intent.setPackage(getPackageName());
                            intent.putStringArrayListExtra("downloadlist", downlist);
                            startService(intent);
                            Toast.makeText(ShowNetFileActivity.this, "已加入下载列表", Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
            });
            alertView.setCancelable(true);
            alertView.show();

        }
    }


    private void showPreviewDialog(String path) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("key", SHOW_PREVIEW_DIALOG);
        bundle.putString("path", path);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private void showDialog() {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("key", SHOW_DIALOG);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private void dismissDialog() {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("key", DISMISS_DIALOG);
        msg.setData(bundle);
        handler.sendMessage(msg);
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

    class MediaButtonListener implements OnClickListener {
        JavaFile file;

        MediaButtonListener(JavaFile file) {
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
                        mp.setDataSource(devInfo.getM_httpserverurl() + file.getLocation());
                        mp.prepare();
                        mp.start();
                        thread = new SetMediaProgressBarThread(handler, pb_media, time);
                        thread.start();

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
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

    class MyNetHandler extends Handler {
        private WeakReference<ShowNetFileActivity> mActivity;

        public MyNetHandler(ShowNetFileActivity activity) {
            mActivity = new WeakReference<ShowNetFileActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ShowNetFileActivity theActivity = mActivity.get();
            if (theActivity != null) {
                super.handleMessage(msg);
                int key = msg.getData().getInt("key");
                switch (key) {
                    case UPDATE_LIST:
                        netShareFileListAdapter.updatelistview(shareFileList, shareFolderList);
                        lv_sharepath.invalidate();
                        break;

                    case SHOW_DIALOG:
                        mProgressView.startAnim();
                        alertDialog_progress.show();
                        break;

                    case DISMISS_DIALOG:
                        if (alertDialog_progress.isShowing()) {
                            mProgressView.stopAnim();
                            alertDialog_progress.dismiss();
                        }
                        break;

                    case SHOW_PREVIEW_DIALOG:
                        iv_preview.setImageResource(R.drawable.picload);
                        String path = msg.getData().getString("path");
                        new DownloadImageTask(iv_preview, 4).execute(devInfo.getM_httpserverurl() + path);
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
                    case MyCoreDownloadProgressCB.UPDATE_DOWNLOAD:
                        if (!alertDialog_download.isShowing()) {
                            long total = msg.getData().getLong("total");
                            int max = (int) (total / 100);
                            pb_download.setMax(max);
                            tv_download.setText("0%");
                            alertDialog_download.show();
                        } else {
                            long part = msg.getData().getLong("part");
                            pb_download.setProgress((int) (part / 100));
                            int p = (int) (part / pb_download.getMax());
                            tv_download.setText(p + "%");
                        }
                        break;
                    case MyCoreDownloadProgressCB.DISMISS_DOWNLOAD:
                        if (alertDialog_download.isShowing())
                            alertDialog_download.dismiss();
                        break;
                    default:
                        break;
                }

            }
        }
    }

    ;
}
