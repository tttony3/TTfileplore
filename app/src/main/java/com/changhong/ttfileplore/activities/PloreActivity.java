package com.changhong.ttfileplore.activities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.changhong.ttfileplore.adapter.RecyclerViewAdapter;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.base.BaseActivity;
import com.changhong.ttfileplore.data.PloreData;
import com.changhong.ttfileplore.fragment.DetailDialogFragment;
import com.changhong.ttfileplore.fragment.NewfileDialogFragment;
import com.changhong.ttfileplore.fragment.SearchDialogFragment;
import com.changhong.ttfileplore.implement.PloreInterface;
import com.changhong.ttfileplore.view.RefreshListView;
import com.chobit.corestorage.ConnectedService;
import com.chobit.corestorage.CoreApp;
import com.chobit.corestorage.CoreService.CoreServiceBinder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.changhong.ttfileplore.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.ttfileplore.utils.*;

public class PloreActivity extends BaseActivity implements View.OnClickListener,
        PloreInterface, OnItemClickListener, OnItemLongClickListener,
        OnMenuItemClickListener, RecyclerViewAdapter.OnItemClickLitener, SearchDialogFragment.OnClickSearchDialog, NewfileDialogFragment.OnClickNewfileDialog {
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected static final String STATE_PAUSE_ON_SCROLL = "STATE_PAUSE_ON_SCROLL";
    protected static final String STATE_PAUSE_ON_FLING = "STATE_PAUSE_ON_FLING";
    protected boolean pauseOnScroll = false;
    protected boolean pauseOnFling = true;
    private ArrayList<File> fileList;
    private RecyclerView mListView;
    private TextView mPathView;
    private ImageView iv_back;
    private TextView mItemCount;
    private Button btn_newfile;
    private Button btn_paste;
    private Button btn_sort;
    private Button btn_seach;
    private Button btn_selectall;
    private Button btn_copy;
    private Button btn_cut;
    private Button btn_more;
    public RecyclerViewAdapter mFileAdpter;
    private LinearLayout ll_btn;
    private LinearLayout ll_btn_default;
    public AlertDialog.Builder builder;
    boolean isCopy = false;
    private AlertDialog alertDialog_qr;
    private ImageView iv_qr;
    private SharedPreferences sharedPreferences;
    public boolean showhidefile;
    private int sorttype = PloreData.NAME;
    private boolean isdefault_btn = true;
    int theme;
    MyApp myapp;

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        pauseOnScroll = savedInstanceState.getBoolean(STATE_PAUSE_ON_SCROLL, false);
        pauseOnFling = savedInstanceState.getBoolean(STATE_PAUSE_ON_FLING, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plore);
        myapp = (MyApp) getApplication();
        MyApp.setContext(myapp.getMainContext());
        fileList = myapp.getFileList();
        sharedPreferences = getSharedPreferences("set", Context.MODE_PRIVATE); // 私有数据
        showhidefile = sharedPreferences.getBoolean("showhidefile", false);
        theme = sharedPreferences.getInt("Theme", R.style.DayTheme);
        findView();
        initView();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        int tmptheme = sharedPreferences.getInt("Theme", theme);
        if (theme != tmptheme) {
            theme = tmptheme;
            setTheme(theme);
            setContentView(R.layout.activity_plore);
            findView();
            initView();
        }
    }

    @Override
    protected void findView() {
        mListView = findView(R.id.file_list);
        mPathView = findView(R.id.path);
        mItemCount = findView(R.id.item_count);
        iv_back = findView(R.id.iv_back);
        btn_selectall = findView(R.id.plore_btn_selectall);
        btn_copy = findView(R.id.plore_btn_cut);
        btn_cut = findView(R.id.plore_btn_copy);
        btn_more = findView(R.id.plore_btn_more);
        btn_newfile = findView(R.id.plore_btn_newfile);
        btn_paste = findView(R.id.plore_btn_paste);
        btn_sort = findView(R.id.plore_btn_sort);
        btn_seach = findView(R.id.plore_btn_seach);
        ll_btn = findView(R.id.ll_btn);
        ll_btn_default = findView(R.id.ll_btn_default);
        View layout_qr = this.getLayoutInflater().inflate(R.layout.dialog_qr, null);
        Builder builder_qr = new Builder(this).setView(layout_qr);
        alertDialog_qr = builder_qr.create();
        iv_qr = (ImageView) layout_qr.findViewById(R.id.iv_qr);
    }

    private void initView() {
        btn_newfile.setOnClickListener(this);
        btn_paste.setOnClickListener(this);
        btn_sort.setOnClickListener(this);
        btn_seach.setOnClickListener(this);
        btn_selectall.setOnClickListener(this);
        btn_copy.setOnClickListener(this);
        btn_cut.setOnClickListener(this);
        btn_more.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        mPathView.setOnClickListener(this);

        ImageView img = new ImageView(this);
        img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dpTopx(40, this)));
        //    mListView.addFooterView(img, null, false);
        File folder = new File("/storage");
        loadData(folder, sorttype);
    }

    @Override
    protected void onStart() {
        showhidefile = sharedPreferences.getBoolean("showhidefile", false);
        int tmptheme = sharedPreferences.getInt("Theme", theme);
        if (theme != tmptheme) {
            theme = tmptheme;
            setTheme(theme);
            setContentView(R.layout.activity_plore);
            findView();
            initView();
        }
        super.onStart();
    }

    @Override
    public void loadData(File folder, int sorttype) {
        setDefaultBtn();
        if (folder.canRead()) {
            String path = folder.getPath();
            mPathView.setText(path);
            PloreData mPloreData = new PloreData(folder, showhidefile, sorttype);
            List<File> files = mPloreData.getfiles();
            mItemCount.setText(files.size() + "项");
            mFileAdpter = new RecyclerViewAdapter(this, files, imageLoader);
            mListView.setLayoutManager(new LinearLayoutManager(mListView.getContext()));
            mFileAdpter.setOnItemClickLitener(this);
            mListView.setAdapter(mFileAdpter);
        }

    }

    private void setDefaultBtn() {
        ll_btn.clearAnimation();
        ll_btn.setVisibility(View.GONE);
        ll_btn_default.setVisibility(View.VISIBLE);
        isdefault_btn = true;
    }

    private void setSecondBtn() {
        ll_btn_default.clearAnimation();
        ll_btn_default.setVisibility(View.GONE);
        ll_btn.setVisibility(View.VISIBLE);
        isdefault_btn = false;
    }


    /**
     * 底部button监听
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.plore_btn_newfile:

                NewfileDialogFragment newFileDialogFragment = new NewfileDialogFragment();
                newFileDialogFragment.show(((MainActivity) myapp.getMainContext()).getFragmentManager(), "newfiledialog");

                break;

            case R.id.plore_btn_cut:
                if (!mFileAdpter.isShow_cb()) {
                    mFileAdpter.setShow_cb(true);
                    mFileAdpter.notifyDataSetChanged();
                } else {
                    Boolean[] mlist = mFileAdpter.getCheckBox_List();
                    fileList.clear();
                    if (mFileAdpter.isShow_cb()) {
                        for (int i = 0; i < mlist.length; i++) {
                            if (mlist[i]) {
                                File file = (File) mFileAdpter.getItem(i);
                                fileList.add(file);
                            }
                        }
                    }
                    mFileAdpter.setShow_cb(false);
                    mFileAdpter.notifyDataSetChanged();
                    isCopy = false;
                    setDefaultBtn();

                    Toast.makeText(PloreActivity.this, "剪切成功", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.plore_btn_paste:
                if (fileList.isEmpty())
                    Toast.makeText(PloreActivity.this, "没有选择文件", Toast.LENGTH_SHORT).show();
                else {
                    String path = mPathView.getText().toString();
                    if (!isCopy) {
                        for (File file : fileList) {
                            if (!file.renameTo(new File(path + "/" + file.getName()))) {
                                Toast.makeText(PloreActivity.this, file.getName() + "粘贴失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                        fileList.clear();
                        File folder = new File(mPathView.getText().toString());
                        loadData(folder, sorttype);
                    } else {
                        for (File file : fileList) {
                            File newFile = new File(path + "/" + file.getName());
                            if (!newFile.exists()) {
                                try {
                                    if (!newFile.createNewFile()) {
                                        Toast.makeText(PloreActivity.this, newFile.getName() + "创建失败", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (IOException e) {

                                    e.printStackTrace();
                                }

                                FileUtils.fileChannelCopy(file, newFile);
                            } else {
                                Toast.makeText(PloreActivity.this, "文件已存在", Toast.LENGTH_SHORT).show();
                            }
                        }
                        fileList.clear();
                        File folder = new File(mPathView.getText().toString());
                        loadData(folder, sorttype);
                    }
                }
                break;
            case R.id.plore_btn_sort:
                PopupMenu popupsort = new PopupMenu(PloreActivity.this, v);
                popupsort.getMenuInflater().inflate(R.menu.sort_menu, popupsort.getMenu());
                popupsort.setOnMenuItemClickListener(this);
                popupsort.show();
                break;
            case R.id.plore_btn_selectall:
                mFileAdpter.setShow_cb(true);
                mFileAdpter.setAllSelect();
                mFileAdpter.notifyDataSetChanged();
                break;
            case R.id.plore_btn_copy:
                if (!mFileAdpter.isShow_cb()) {
                    mFileAdpter.setShow_cb(true);
                    mFileAdpter.notifyDataSetChanged();
                } else {
                    Boolean[] mlist = mFileAdpter.getCheckBox_List();
                    fileList.clear();
                    if (mFileAdpter.isShow_cb()) {
                        for (int i = 0; i < mlist.length; i++) {
                            if (mlist[i]) {
                                File file = (File) mFileAdpter.getItem(i);
                                fileList.add(file);
                            }
                        }
                    }
                    mFileAdpter.setShow_cb(false);
                    mFileAdpter.notifyDataSetChanged();
                    isCopy = true;
                    setDefaultBtn();
                    Toast.makeText(PloreActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.plore_btn_more:
                PopupMenu popup = new PopupMenu(PloreActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.more_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(this);
                popup.show();
                break;
            case R.id.plore_btn_seach:
                SearchDialogFragment searchDialogFragment = new SearchDialogFragment();
                searchDialogFragment.show(((MainActivity) myapp.getMainContext()).getFragmentManager(), "searchdialog");
                break;
            case R.id.iv_back:
                if (mFileAdpter.isShow_cb()) {
                    mFileAdpter.setShow_cb(false);
                    setDefaultBtn();
                    mFileAdpter.notifyDataSetChanged();
                } else {
                    String str = (String) mPathView.getText();
                    if (str.lastIndexOf("/") == 0) {

                        loadData(new File("/storage"), sorttype);
                    } else {
                        loadData(new File((String) str.subSequence(0, str.lastIndexOf("/"))), sorttype);
                    }
                }
                break;

            case R.id.path:
                if (mFileAdpter.isShow_cb()) {
                    mFileAdpter.setShow_cb(false);
                    setDefaultBtn();
                    mFileAdpter.notifyDataSetChanged();
                } else {
                    String str = (String) mPathView.getText();
                    if (str.lastIndexOf("/") == 0) {

                        loadData(new File("/storage"), sorttype);
                    } else {
                        loadData(new File((String) str.subSequence(0, str.lastIndexOf("/"))), sorttype);
                    }
                }
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.file_list:
                if (!(view instanceof ImageView))
                    if (!mFileAdpter.isShow_cb()) {
                        mFileAdpter.setShow_cb(true);
                        setSecondBtn();
                        mFileAdpter.notifyDataSetChanged();
                    }
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.file_list:
                File file = (File) parent.getItemAtPosition(position);
                if (!file.canRead()) {
                    Toast.makeText(parent.getContext(), "打不开", Toast.LENGTH_SHORT).show();
                } else if (file.isDirectory()) {
                    loadData(file, sorttype);
                } else {
                    startActivity(Utils.openFile(file));
                }
                break;

            default:
                break;
        }

    }


//    @Override
//    public void OnRefresh() {
//        RefreshDataAsynTask mRefreshAsynTask = new RefreshDataAsynTask();
//        mRefreshAsynTask.execute();
//
//    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (mPathView.getText().toString().lastIndexOf("/") == 0) {

                if (mFileAdpter.isShow_cb()) {

                    mFileAdpter.setShow_cb(false);
                    setDefaultBtn();
                    mFileAdpter.notifyDataSetChanged();
                    return true;
                } else {

                    return false;
                }
            } else {
                return iv_back.callOnClick();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void applyScrollListener() {
        //  mListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling));
    }

    @Override
    public void onResume() {
        super.onResume();
        applyScrollListener();
    }


    @SuppressWarnings("deprecation")
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.it_meun_delete:
                if (!mFileAdpter.isShow_cb()) {
                    mFileAdpter.setShow_cb(true);
                    mFileAdpter.notifyDataSetChanged();
                } else {
                    Boolean[] mlist = mFileAdpter.getCheckBox_List();
                    for (int i = 0; i < mlist.length; i++) {
                        if (mlist[i]) {
                            File file = (File) mFileAdpter.getItem(i);
                            Log.e("file", file.getName());
                            if (file.delete()) {

                                Toast.makeText(PloreActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PloreActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    File folder = new File(mPathView.getText().toString());
                    loadData(folder, sorttype);
                }
                break;

            case R.id.it_meun_share:
                if (!sharedPreferences.getBoolean("share", true)) {
                    Toast.makeText(PloreActivity.this, "未开启共享", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!mFileAdpter.isShow_cb()) {
                    mFileAdpter.setShow_cb(true);
                    mFileAdpter.notifyDataSetChanged();
                } else {
                    Boolean[] mlist = mFileAdpter.getCheckBox_List();
                    for (int i = 0; i < mlist.length; i++) {
                        if (mlist[i]) {
                            File file = (File) mFileAdpter.getItem(i);

                            String s = CoreApp.mBinder.AddShareFile(file.getPath());
                            Toast.makeText(PloreActivity.this, "AddShareFile  " + s, Toast.LENGTH_SHORT).show();
                        }
                    }
                    File folder = new File(mPathView.getText().toString());
                    loadData(folder, sorttype);

                }

                break;
            case R.id.it_meun_push:
                if (!sharedPreferences.getBoolean("share", true)) {
                    Toast.makeText(PloreActivity.this, "未开启共享", Toast.LENGTH_SHORT).show();
                    break;
                }
                ArrayList<String> pushList = new ArrayList<>();
                if (!mFileAdpter.isShow_cb()) {
                    mFileAdpter.setShow_cb(true);
                    mFileAdpter.notifyDataSetChanged();
                } else {
                    Boolean[] mlist = mFileAdpter.getCheckBox_List();
                    for (int i = 0; i < mlist.length; i++) {
                        if (mlist[i]) {
                            File file = (File) mFileAdpter.getItem(i);
                            if (!file.isDirectory()) {

                                MyApp myapp = (MyApp) getApplication();
                                String ip = myapp.getIp();
                                int port = myapp.getPort();
                                pushList.add("http://" + ip + ":" + port + file.getPath());
                            } else {
                                Toast.makeText(PloreActivity.this, "文件夹暂不支持推送", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (pushList.size() > 0) {
                        Intent intent = new Intent();
                        Bundle b = new Bundle();
                        b.putStringArrayList("pushList", pushList);
                        intent.putExtra("pushList", b);
                        intent.setClass(PloreActivity.this, ShowNetDevActivity.class);
                        startActivity(intent);
                    }

                }
                break;

            case R.id.it_meun_qr:
                if (!sharedPreferences.getBoolean("share", true)) {
                    Toast.makeText(PloreActivity.this, "未开启共享", Toast.LENGTH_SHORT).show();
                    break;
                }
                String ssid;
                WifiManager wifiManager = (WifiManager) PloreActivity.this.getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()) {
                    WifiInfo info = wifiManager.getConnectionInfo();
                    if (info != null) {
                        ssid = info.getSSID();
                    } else {
                        HPaConnector hpc = HPaConnector.getInstance(PloreActivity.this);

                        try {
                            WifiConfiguration wificonf = hpc.setupWifiAp("fileplore", "12345678");
                            ssid = wificonf.SSID;
                            Thread.sleep(1000);
                            final MyApp app = (MyApp) PloreActivity.this.getApplicationContext();
                            app.setConnectedService(new ConnectedService() {

                                @Override
                                public void onConnected(Binder b) {
                                    CoreServiceBinder binder = (CoreServiceBinder) b;
                                    binder.init();
                                    binder.setCoreHttpServerCBFunction(app.httpServerCB);
                                    binder.StartHttpServer("/", MyApp.context.get());
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("eee11", e.getMessage());
                            Toast.makeText(PloreActivity.this, "开启wifi热点失败", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                } else {
                    HPaConnector hpc = HPaConnector.getInstance(PloreActivity.this);

                    try {

                        WifiConfiguration wificonf = hpc.setupWifiAp("fileplore", "12345678");
                        ssid = wificonf.SSID;
                        Thread.sleep(500);
                        final MyApp app = (MyApp) PloreActivity.this.getApplicationContext();

                        app.setConnectedService(new ConnectedService() {

                            @Override
                            public void onConnected(Binder b) {
                                CoreServiceBinder binder = (CoreServiceBinder) b;
                                binder.init();
                                binder.setCoreHttpServerCBFunction(app.httpServerCB);
                                binder.StartHttpServer("/", MyApp.context.get());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("eee22", e.getMessage());
                        Toast.makeText(PloreActivity.this, "开启wifi热点失败", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                StringBuilder sb = new StringBuilder();
                if (!mFileAdpter.isShow_cb()) {
                    mFileAdpter.setShow_cb(true);
                    mFileAdpter.notifyDataSetChanged();
                } else {
                    Boolean[] mlist = mFileAdpter.getCheckBox_List();
                    sb.append("fileplore|")
                            .append(ssid)
                            .append("|");
                    for (int i = 0; i < mlist.length; i++) {
                        if (mlist[i]) {
                            File file = (File) mFileAdpter.getItem(i);
                            if (!file.isDirectory()) {
                                MyApp myapp = (MyApp) getApplication();
                                String ip = myapp.getIp();
                                int port = myapp.getPort();
                                sb.append("http://")
                                        .append(ip)
                                        .append(":")
                                        .append(port)
                                        .append(file.getPath())
                                        .append("|");
                            } else {
                                Toast.makeText(PloreActivity.this, "暂不支持文件夹", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                float scale = PloreActivity.this.getResources().getDisplayMetrics().density;
                iv_qr.setImageBitmap(
                        Utils.createImage(sb.toString(), (int) (200 * scale + 0.5f), (int) (200 * scale + 0.5f)));
                alertDialog_qr.show();
                alertDialog_qr.getWindow().setLayout((int) (210 * scale + 0.5f), (int) (200 * scale + 0.5f));
                break;

            case R.id.it_meun_detail:
                ArrayList<File> detailList = new ArrayList<>();
                if (!mFileAdpter.isShow_cb()) {
                    mFileAdpter.setShow_cb(true);
                    mFileAdpter.notifyDataSetChanged();
                } else {
                    Boolean[] mlist = mFileAdpter.getCheckBox_List();
                    for (int i = 0; i < mlist.length; i++) {
                        if (mlist[i]) {
                            File file = (File) mFileAdpter.getItem(i);
                            if (!file.isDirectory()) {
                                detailList.add(file);
                            }
                        }
                    }
                    if (detailList.size() == 1) {
                        File detailfile = detailList.get(0);
                        String space = Formatter.formatFileSize(PloreActivity.this, detailfile.getTotalSpace());
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
                        detailDialog.show(((MainActivity) myapp.getMainContext()).getFragmentManager(), "detailDialog");

                    }

                }
                break;

            case R.id.it_meun_sorttime:
                sorttype = PloreData.TIME;
                loadData(new File(mPathView.getText().toString()), sorttype);
                break;

            case R.id.it_meun_sortname:
                sorttype = PloreData.NAME;
                loadData(new File(mPathView.getText().toString()), sorttype);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onSearch(String filename) {
        List<File> files = mFileAdpter.getAllFiles();
        List<File> matchfiles = new ArrayList<>();
        for (File file : files) {
            if (file.getName().toLowerCase().contains(filename.toLowerCase())) {
                matchfiles.add(file);
            }
        }
        if (matchfiles.size() > 0) {
            mPathView.setText(getResources().getText(R.string.default_path));
            mItemCount.setText(matchfiles.size() + "" + getResources().getText(R.string.default_count));
            mFileAdpter.updateList(matchfiles);
            return true;
        } else {
            Toast.makeText(this, "未找到文件", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public boolean onNewfile(String filename) {
        if (filename.isEmpty())
            return false;
        File file = new File(mPathView.getText().toString() + "/" + filename);
        if (file.exists())
            Toast.makeText(PloreActivity.this, "文件夹已存在", Toast.LENGTH_SHORT).show();
        else {
            if (file.mkdir())
                Toast.makeText(PloreActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(PloreActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
            }
            File folder = new File(mPathView.getText().toString());
            loadData(folder, sorttype);
        }
        return true;
    }

    @Override
    public void onClickImg(View v, File file) {
        if (!file.canRead()) {
            Toast.makeText(this, "打不开", Toast.LENGTH_SHORT).show();
        } else if (file.isDirectory()) {
            loadData(file, sorttype);
        } else {
            startActivity(Utils.openFile(file));
        }
    }

    @Override
    public void onItemClick(RecyclerViewAdapter adapter, View view, int position) {
        File file = (File) adapter.getItem(position);
        if (!file.canRead()) {
            Toast.makeText(this, "打不开", Toast.LENGTH_SHORT).show();
        } else if (file.isDirectory()) {
            loadData(file, sorttype);
        } else {
            startActivity(Utils.openFile(file));
        }
    }

    @Override
    public void onItemLongClick(RecyclerViewAdapter adapter, View view, int position) {
        if (!(view instanceof ImageView))
            if (!mFileAdpter.isShow_cb()) {
                mFileAdpter.setShow_cb(true);
                setSecondBtn();
                mFileAdpter.notifyDataSetChanged();
            }
    }

    class RefreshDataAsynTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            loadData(new File(mPathView.getText().toString()), sorttype);
        }

    }


}
