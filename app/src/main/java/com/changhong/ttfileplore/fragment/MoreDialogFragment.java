package com.changhong.ttfileplore.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.activities.MainActivity;
import com.changhong.ttfileplore.activities.PhotoActivity;
import com.changhong.ttfileplore.activities.ShowNetDevActivity;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.utils.HPaConnector;
import com.changhong.ttfileplore.utils.Utils;
import com.chobit.corestorage.ConnectedService;
import com.chobit.corestorage.CoreApp;
import com.chobit.corestorage.CoreService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MoreDialogFragment extends DialogFragment implements View.OnClickListener {
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
    /**
     *  刷新界面，由activity实现
     */
    public interface UpDate{
        void update();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        baseContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_moredialog, container);
        findView(view,container);
        sharedPreferences = getActivity().getSharedPreferences("set", Context.MODE_PRIVATE); //私有数据
        Bundle bundle = getArguments();
        filePath = bundle.getString("filePath");
        file = new File(filePath);
        isshare = sharedPreferences.getBoolean("share", true);
        initView();

        return view;
    }

    private void initView() {
        rl_delete.setOnClickListener(this);
        rl_qr.setOnClickListener(this);
        rl_detail.setOnClickListener(this);
        rl_share.setOnClickListener(this);
        rl_push.setOnClickListener(this);
        rl_shareto.setOnClickListener(this);

    }

    private void findView(View view,ViewGroup container) {
        rl_delete = (RelativeLayout) view.findViewById(R.id.rl_moreoption_delete);
        rl_qr = (RelativeLayout) view.findViewById(R.id.rl_moreoption_qr);
        rl_detail = (RelativeLayout) view.findViewById(R.id.rl_moreoption_detail);
        rl_share = (RelativeLayout) view.findViewById(R.id.rl_moreoption_share);
        rl_push = (RelativeLayout) view.findViewById(R.id.rl_moreoption_push);
        rl_shareto = (RelativeLayout) view.findViewById(R.id.rl_moreoption_shareto);

        View layout_qr = getActivity().getLayoutInflater().inflate(R.layout.dialog_qr, container);
        AlertDialog.Builder builder_qr = new AlertDialog.Builder(baseContext).setView(layout_qr);
        alertDialog_qr = builder_qr.create();
        iv_qr = (ImageView) layout_qr.findViewById(R.id.iv_qr);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_moreoption_delete:
                if (file.exists()) {
                    if (file.delete()) {
                        ((UpDate) baseContext).update();
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
                            final MyApp app = (MyApp) getActivity().getApplicationContext();
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
                        final MyApp app = (MyApp) getActivity().getApplicationContext();
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
                    MyApp myapp = (MyApp) getActivity().getApplication();
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
                detailDialog.show(getActivity().getFragmentManager(), "detailDialog");

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
                    MyApp myapp = (MyApp) getActivity().getApplication();
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
                startActivity(intent);
                break;
            case R.id.rl_moreoption_shareto:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                shareIntent.setType(Utils.getMIMEType(file));
                startActivity(shareIntent);
                break;
        }
    }
}
