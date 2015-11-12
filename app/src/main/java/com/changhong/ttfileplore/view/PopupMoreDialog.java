package com.changhong.ttfileplore.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.activities.ShowNetDevActivity;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.fragment.DetailDialogFragment;
import com.changhong.ttfileplore.fragment.MoreDialogFragment;
import com.changhong.ttfileplore.utils.HPaConnector;
import com.changhong.ttfileplore.utils.Utils;
import com.chobit.corestorage.ConnectedService;
import com.chobit.corestorage.CoreApp;
import com.chobit.corestorage.CoreService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by tangli on 2015/11/12。
 */
public class PopupMoreDialog  extends PopupWindow implements View.OnClickListener {
    String filePath;
    File file;
    RelativeLayout rl_delete;
    RelativeLayout rl_qr;
    RelativeLayout rl_detail;
    RelativeLayout rl_share;
    RelativeLayout rl_push;
    RelativeLayout rl_shareto;
    Context baseContext;
    private AlertDialog alertDialog_qr;
    private ImageView iv_qr;
    SharedPreferences sharedPreferences;
    boolean isshare = true;

    public PopupMoreDialog(Context context, int width, int height, boolean focusable,String path){
        super(LayoutInflater.from(context).inflate(R.layout.fragment_moredialog, null), width, height, focusable);
        super.setBackgroundDrawable(new ColorDrawable(0x00000000));
        super.setTouchable(true);
        baseContext  =context;
        sharedPreferences = context.getSharedPreferences("set", Context.MODE_PRIVATE); //私有数据
        isshare = sharedPreferences.getBoolean("share", true);
        filePath = path;
        file  = new File(filePath);
        setAnimationStyle(R.style.PopupAnimation);
        findView();
        initView();
    }


    private void findView() {
        rl_delete = (RelativeLayout) getContentView().findViewById(R.id.rl_moreoption_delete);
        rl_qr = (RelativeLayout) getContentView().findViewById(R.id.rl_moreoption_qr);
        rl_detail = (RelativeLayout) getContentView().findViewById(R.id.rl_moreoption_detail);
        rl_share = (RelativeLayout) getContentView().findViewById(R.id.rl_moreoption_share);
        rl_push = (RelativeLayout) getContentView().findViewById(R.id.rl_moreoption_push);
        rl_shareto = (RelativeLayout) getContentView().findViewById(R.id.rl_moreoption_shareto);

        View layout_qr =LayoutInflater.from(baseContext).inflate(R.layout.dialog_qr, null);
        AlertDialog.Builder builder_qr = new AlertDialog.Builder(baseContext).setView(layout_qr);
        alertDialog_qr = builder_qr.create();
        iv_qr = (ImageView) layout_qr.findViewById(R.id.iv_qr);
    }
    private void initView() {
        rl_delete.setOnClickListener(this);
        rl_qr.setOnClickListener(this);
        rl_detail.setOnClickListener(this);
        rl_share.setOnClickListener(this);
        rl_push.setOnClickListener(this);
        rl_shareto.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_moreoption_delete:
                if (file.exists()) {
                    if (file.delete()) {
                        ((MoreDialogFragment.UpDate) baseContext).update();
                        Toast.makeText(baseContext, "删除成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(baseContext, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(baseContext, "文件不存在", Toast.LENGTH_SHORT).show();
                }
                this.dismiss();
                break;
            case R.id.rl_moreoption_qr:
                if(!isshare){
                    Toast.makeText(baseContext, "未开启共享", Toast.LENGTH_SHORT).show();
                    break;
                }
                String ssid = "~";
                WifiManager wifiManager = (WifiManager) baseContext.getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()) {
                    WifiInfo info = wifiManager.getConnectionInfo();
                    if (info != null) {
                        ssid = info.getSSID();
                    } else {
                        HPaConnector hpc = HPaConnector.getInstance(baseContext);
                        try {
                            WifiConfiguration wificonf = hpc.setupWifiAp("fileplore", "12345678");
                            ssid = wificonf.SSID;
                            Thread.sleep(500);
                            final MyApp app = (MyApp)((Activity)baseContext).getApplicationContext();
                            app.setConnectedService(new ConnectedService() {

                                @Override
                                public void onConnected(Binder b) {
                                    CoreService.CoreServiceBinder binder = (CoreService.CoreServiceBinder) b;
                                    binder.init();
                                    binder.setCoreHttpServerCBFunction(app.httpServerCB);
                                    binder.StartHttpServer("/", MyApp.mainContext);
                                }
                            });
                        } catch (Exception e) {
                            Log.e("eee22", e.getMessage());
                            e.printStackTrace();
                            Toast.makeText(baseContext, "开启wifi热点失败", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                } else {
                    HPaConnector hpc = HPaConnector.getInstance(baseContext);

                    try {
                        WifiConfiguration wificonf = hpc.setupWifiAp("fileplore", "12345678");
                        ssid = wificonf.SSID;
                        Thread.sleep(500);
                        final MyApp app = (MyApp) ((Activity)baseContext).getApplicationContext();
                        app.setConnectedService(new ConnectedService() {

                            @Override
                            public void onConnected(Binder b) {
                                CoreService.CoreServiceBinder binder = (CoreService.CoreServiceBinder) b;
                                binder.init();
                                binder.setCoreHttpServerCBFunction(app.httpServerCB);
                                binder.StartHttpServer("/", MyApp.mainContext);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(baseContext, "开启wifi热点失败", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                StringBuilder sb = new StringBuilder();
                sb.append("fileplore|")
                        .append(ssid)
                        .append("|");
                if (!file.isDirectory()) {
                    MyApp myapp = (MyApp) ((Activity)baseContext).getApplication();
                    String ip = myapp.getIp();
                    int port = myapp.getPort();
                    sb.append("http://")
                            .append(ip)
                            .append(":")
                            .append(port)
                            .append(file.getPath())
                            .append("|");
                } else {
                    Toast.makeText(baseContext, "暂不支持文件夹", Toast.LENGTH_SHORT).show();
                }


                float scale = baseContext.getResources().getDisplayMetrics().density;
                iv_qr.setImageBitmap(
                        Utils.createImage(sb.toString(), (int) (200 * scale + 0.5f), (int) (200 * scale + 0.5f)));
                alertDialog_qr.show();
                alertDialog_qr.getWindow().setLayout((int) (210 * scale + 0.5f), (int) (200 * scale + 0.5f));
                break;
            case R.id.rl_moreoption_detail:

                File detailfile = file;
                String space = Formatter.formatFileSize(baseContext, detailfile.getTotalSpace());
                String path = detailfile.getPath();

                String time = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(detailfile.lastModified());
                String name = detailfile.getName();

                DetailDialogFragment detailDialog = new DetailDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("path", path);
                bundle.putString("time", time);
                bundle.putString("space", space);
                detailDialog.setArguments(bundle);
                detailDialog.show(((Activity)baseContext).getFragmentManager(), "detailDialog");

                break;
            case R.id.rl_moreoption_share:
                if(!isshare){
                    Toast.makeText(baseContext, "未开启共享", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (CoreApp.mBinder.isBinderAlive()) {
                    String s = CoreApp.mBinder.AddShareFile(file.getPath());
                    Toast.makeText(baseContext, "AddShareFile  " + s, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(baseContext, "服务未开启", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_moreoption_push:
                if(!isshare){
                    Toast.makeText(baseContext, "未开启共享", Toast.LENGTH_SHORT).show();
                    break;
                }
                ArrayList<String> pushList = new ArrayList<>();
                if (!file.isDirectory()) {
                    MyApp myapp = (MyApp) ((Activity)baseContext).getApplication();
                    String ip = myapp.getIp();
                    int port = myapp.getPort();
                    pushList.add("http://" + ip + ":" + port + file.getPath());
                } else {
                    Toast.makeText(baseContext, "文件夹暂不支持推送", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putStringArrayList("pushList", pushList);
                intent.putExtra("pushList", b);
                intent.setClass(baseContext, ShowNetDevActivity.class);
                baseContext.startActivity(intent);
                break;
            case R.id.rl_moreoption_shareto:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                shareIntent.setType(Utils.getMIMEType(file));
                baseContext.startActivity(shareIntent);
                break;
        }
    }
}
