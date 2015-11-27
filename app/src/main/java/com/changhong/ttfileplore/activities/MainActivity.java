package com.changhong.ttfileplore.activities;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.changhong.ttfileplore.fragment.FilePreViewFragment;
import com.changhong.ttfileplore.fragment.NewfileDialogFragment;
import com.changhong.ttfileplore.fragment.SearchDialogFragment;
import com.changhong.ttfileplore.view.ColorCursorView;
import com.changhong.ttfileplore.view.ColorTrackView;
import com.chobit.corestorage.ConnectedService;
import com.chobit.corestorage.CoreService.CoreServiceBinder;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.adapter.MainViewPagerAdapter;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;

import android.app.LocalActivityManager;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, OnLongClickListener, SearchDialogFragment.OnClickSearchDialog, NewfileDialogFragment.OnClickNewfileDialog, MenuItem.OnMenuItemClickListener {


    SharedPreferences sharedPreferences;
    View view0;
    View view1;
    ImageView iv_apk;
    ImageView iv_movie;
    ImageView iv_music;
    ImageView iv_photo;
    ImageView iv_txt;
    ImageView iv_zip;
    ImageView iv_qq;
    ImageView iv_wechat;
    ImageView iv_app;
    MyApp myapp;
    final ArrayList<View> list = new ArrayList<>();
    Context context = null;

    @SuppressWarnings("deprecation")
    LocalActivityManager manager = null;
    ViewPager pager = null;
    TableLayout tl_brwloc;
    RelativeLayout rl_brwnet;
    RelativeLayout rl_showdown;
    MainViewPagerAdapter myPagerAdapter;

    private int currIndex = 0;// 当前页卡编号
    private long curtime = 0;
    private List<ColorTrackView> mTabs = new ArrayList<>();
    private List<ColorCursorView> mCursors = new ArrayList<>();
    int theme;
    boolean isshare = true;
    DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("set", Context.MODE_PRIVATE); //私有数据
        switch (sharedPreferences.getInt("Theme", R.style.DayTheme)) {
            case R.style.DayTheme:
                setTheme(R.style.DayTheme);
                theme = R.style.DayTheme;
                break;
            case R.style.NightTheme:
                setTheme(R.style.NightTheme);
                theme = R.style.NightTheme;
                break;
        }

        setContentView(R.layout.activity_main);


        myapp = (MyApp) getApplication();
        MyApp.setContext(this);
        myapp.setMainContext(this);
        context = MainActivity.this;
        isshare = sharedPreferences.getBoolean("share", true);
        if (isshare) {
            myapp.setConnectedService(new ConnectedService() {

                @Override
                public void onConnected(Binder b) {
                    CoreServiceBinder binder = (CoreServiceBinder) b;
                    binder.init();
                    binder.setCoreHttpServerCBFunction(myapp.httpServerCB);
                    //noinspection deprecation
                    binder.StartHttpServer("/", context);
                }
            });
        }

        //noinspection deprecation
        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);

        InitImageView();
        initTextView();
        initPagerViewer();
        initToolBar();

    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.mDrawerLayout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.cancel, R.string.capacity) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);//设置监听器
        NavigationView navigationView = (NavigationView) mDrawerLayout.findViewById(R.id.id_nv_menu);
        navigationView.getMenu().findItem(R.id.left_movie).setOnMenuItemClickListener(this);
        navigationView.getMenu().findItem(R.id.left_set).setOnMenuItemClickListener(this);
        navigationView.getMenu().findItem(R.id.left_music).setOnMenuItemClickListener(this);
        navigationView.getMenu().findItem(R.id.left_recivefile).setOnMenuItemClickListener(this);
        navigationView.getMenu().findItem(R.id.left_photo).setOnMenuItemClickListener(this);
        navigationView.getMenu().findItem(R.id.left_netfile).setOnMenuItemClickListener(this);
    }


    /**
     * 初始化标题
     */
    private void initTextView() {
        mTabs.clear();
        ColorTrackView mTab0 = (ColorTrackView) findViewById(R.id.text1);
        ColorTrackView mTab1 = (ColorTrackView) findViewById(R.id.text2);
        mTab0.setOnClickListener(new MyOnClickListener(0));
        mTab1.setOnClickListener(new MyOnClickListener(1));
        mTabs.add(mTab0);
        mTabs.add(mTab1);
    }

    /**
     * 初始化PageViewer
     */
    private void initPagerViewer() {
        pager = (ViewPager) findViewById(R.id.viewpage);
        list.clear();
        Intent intent1 = new Intent(context, BrowseActivity.class);
        list.add(0, getView("A", intent1));

        Intent intent2 = new Intent(context, PloreActivity.class);
        list.add(1, getView("B", intent2));
        view0 = list.get(0);
        view1 = list.get(1);
        tl_brwloc = (TableLayout) view0.findViewById(R.id.browse_tab_2);
        rl_brwnet = (RelativeLayout) view0.findViewById(R.id.browse_rl_net);
        rl_showdown = (RelativeLayout) view0.findViewById(R.id.browse_rl_downlist);
        tl_brwloc.setOnClickListener(new MyOnClickListener(1));
        rl_brwnet.setOnClickListener(this);
        rl_showdown.setOnClickListener(this);
        iv_apk = (ImageView) view0.findViewById(R.id.img_apk);
        iv_movie = (ImageView) view0.findViewById(R.id.img_movie);
        iv_music = (ImageView) view0.findViewById(R.id.img_music);
        iv_photo = (ImageView) view0.findViewById(R.id.img_photo);
        iv_txt = (ImageView) view0.findViewById(R.id.img_txt);
        iv_zip = (ImageView) view0.findViewById(R.id.img_zip);
        iv_app = (ImageView) view0.findViewById(R.id.img_app);
        iv_qq = (ImageView) view0.findViewById(R.id.img_qq);
        iv_wechat = (ImageView) view0.findViewById(R.id.img_wechat);
        myPagerAdapter = new MainViewPagerAdapter(list);
        pager.setAdapter(myPagerAdapter);
        pager.setCurrentItem(0);
        pager.addOnPageChangeListener(new MyOnPageChangeListener());

        iv_apk.setOnClickListener(this);
        iv_apk.setOnLongClickListener(this);
        iv_movie.setOnClickListener(this);
        iv_movie.setOnLongClickListener(this);
        iv_music.setOnClickListener(this);
        iv_music.setOnLongClickListener(this);
        iv_photo.setOnClickListener(this);
        iv_photo.setOnLongClickListener(this);
        iv_txt.setOnClickListener(this);
        iv_txt.setOnLongClickListener(this);
        iv_zip.setOnClickListener(this);
        iv_zip.setOnLongClickListener(this);
        iv_qq.setOnClickListener(this);
        iv_wechat.setOnClickListener(this);
        iv_app.setOnClickListener(this);


    }

    /**
     * 初始化动画
     */
    private void InitImageView() {
        mCursors.clear();
        ColorCursorView mCursor1 = (ColorCursorView) findViewById(R.id.cursor_1);
        ColorCursorView mCursor2 = (ColorCursorView) findViewById(R.id.cursor_2);
        mCursors.add(mCursor1);
        mCursors.add(mCursor2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.plore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();

        int id = item.getItemId();
        switch (id) {
            case R.id.action_set:
                intent.setClass(MainActivity.this, SetActivity.class);
                startActivity(intent);
                break;

            case (R.id.action_samba):
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.samba_option, (ViewGroup) findViewById(R.id.samba_op));
                new AlertDialog.Builder(this).setTitle("samba设置").setView(layout)
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText ip = (EditText) layout.findViewById(R.id.ip);
                                EditText user = (EditText) layout.findViewById(R.id.user);
                                EditText password = (EditText) layout.findViewById(R.id.password);
                                EditText dir = (EditText) layout.findViewById(R.id.dir);
                                if (!ip.getText().toString().isEmpty() && !user.getText().toString().isEmpty()
                                        && !password.getText().toString().isEmpty()) {
                                    Intent intent = new Intent();
                                    intent.putExtra("ip", ip.getText().toString());
                                    intent.putExtra("user", user.getText().toString());
                                    intent.putExtra("password", password.getText().toString());
                                    intent.putExtra("dir", dir.getText().toString());
                                    intent.setClass(MainActivity.this, SambaActivity.class);
                                    startActivity(intent);

                                }

                            }
                        }).setPositiveButton("取消", null).show();
                break;

            case (R.id.action_net):
                intent.setClass(MainActivity.this, ShowNetDevActivity.class);
                startActivity(intent);
                break;
            case (R.id.action_scanner):
                if (!sharedPreferences.getBoolean("share", true)) {
                    Toast.makeText(this, "未开启共享", Toast.LENGTH_SHORT).show();
                    return true;
                }
                intent.setClass(MainActivity.this, CaptureActivity.class);
                startActivity(intent);
                break;
            case (R.id.action_sharefile):
                intent.setClass(MainActivity.this, ShowSharefileActivity.class);
                startActivity(intent);
                break;
            case (R.id.action_share):
                ArrayList<File> detailList = new ArrayList<>();
                pager.setCurrentItem(1);
                Boolean[] mlist = ((PloreActivity) view1.getContext()).mFileAdpter.getCheckBox_List();
                for (int i = 0; i < mlist.length; i++) {
                    if (mlist[i]) {
                        File file = (File) ((PloreActivity) view1.getContext()).mFileAdpter.getItem(i);
                        if (!file.isDirectory()) {
                            detailList.add(file);
                        } else {
                            Toast.makeText(MainActivity.this, "文件夹不支持分享", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if (detailList.size() == 1) {
                    File detailfile = detailList.get(0);
                    onClickShare(detailfile);

                } else if (detailList.size() > 1) {
                    Toast.makeText(MainActivity.this, "一次只能分享一个文件", Toast.LENGTH_SHORT).show();
                }

                break;
        }
        return super.onOptionsItemSelected(item);

    }

    /**
     * 通过activity获取视图
     */
    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }

    @Override
    public boolean onNewfile(String filename) {
        return ((PloreActivity) view1.getContext()).onNewfile(filename);
    }

    @Override
    public boolean onSearch(String filename) {
        return ((PloreActivity) view1.getContext()).onSearch(filename);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.left_set:
                intent.setClass(MainActivity.this, SetActivity.class);
                startActivity(intent);
                break;
            case R.id.left_movie:
                intent.setClass(MainActivity.this, ClassifyGridActivity.class);
                intent.putExtra("key", R.id.img_movie);
                startActivity(intent);
                break;
            case R.id.left_music:
                intent.setClass(MainActivity.this, ClassifyListActivity.class);
                intent.putExtra("key", R.id.img_music);
                startActivity(intent);
            case R.id.left_recivefile:
                intent.setClass(MainActivity.this, ShowReciveDevActivity.class);
                startActivity(intent);
                break;
            case R.id.left_netfile:
                if (isshare) {
                    intent.setClass(MainActivity.this, ShowNetDevActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(MainActivity.this, "没开启共享", Toast.LENGTH_SHORT).show();
                break;
            case R.id.left_photo:
                intent.setClass(MainActivity.this, ClassifyGridActivity.class);
                intent.putExtra("key", R.id.img_photo);
                startActivity(intent);
                break;
        }
        return false;
    }

    /**
     * Pager适配器
     */

    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            currIndex = arg0;
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            if (positionOffset > 0) {

                ColorTrackView leftTrack = mTabs.get(position);
                ColorTrackView rightTrack = mTabs.get(position + 1);
                leftTrack.setDirection(1);
                rightTrack.setDirection(0);
                leftTrack.setProgress(1 - positionOffset);
                rightTrack.setProgress(positionOffset);

                ColorCursorView leftCursor = mCursors.get(position);
                ColorCursorView rightCursor = mCursors.get(position + 1);
                leftCursor.setDirection(1);
                rightCursor.setDirection(0);
                leftCursor.setProgress(1 - positionOffset);
                rightCursor.setProgress(positionOffset);
            }
        }
    }

    /**
     * 头标点击监听
     */
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            if (index == 0) {
                pager.setCurrentItem(index);
            }
            if (index == 1) {
                pager.setCurrentItem(index);

            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currIndex == 0) {
                if (System.currentTimeMillis() - curtime > 1000) {
                    Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                    curtime = System.currentTimeMillis();
                    return true;
                } else
                    finish();
            } else if (currIndex == 1) {
                if (!((PloreActivity) view1.getContext()).onKeyDown(keyCode, event))
                    pager.setCurrentItem(0);
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            Log.e("home", "home");
            MyApp.setContext(null);

        }
        return super.onKeyDown(keyCode, event);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onRestart() {
        Log.e("onRestart", "onRestart");
        super.onRestart();
        ((PloreActivity) list.get(1).getContext()).showhidefile = sharedPreferences.getBoolean("showhidefile", false);
        myapp = (MyApp) getApplication();
        MyApp.setContext(this);
        ((BrowseActivity) list.get(0).getContext()).callupdate();
        isshare = sharedPreferences.getBoolean("share", true);
        if (isshare) {
            myapp.setConnectedService(new ConnectedService() {

                @Override
                public void onConnected(Binder b) {
                    CoreServiceBinder binder = (CoreServiceBinder) b;
                    binder.init();
                    binder.setCoreHttpServerCBFunction(myapp.httpServerCB);
                    //noinspection deprecation
                    binder.StartHttpServer("/", context);
                }
            });
        }
        int tmptheme = sharedPreferences.getInt("Theme", theme);
        Log.e("tmptheme", tmptheme + " " + theme);
        if (theme != tmptheme) {

            setTheme(tmptheme);
            theme = tmptheme;
            setContentView(R.layout.activity_main);


            InitImageView();
            initTextView();
            ((BrowseActivity) view0.getContext()).onRestart();
            ((PloreActivity) view1.getContext()).onRestart();
            initPagerViewer();
            initToolBar();
            //       supportInvalidateOptionsMenu();
            //       invalidateOptionsMenu();

        }

    }

    @Override
    protected void onDestroy() {

        stopService(new Intent("com.chobit.corestorage.CoreService"));
        stopService(new Intent("com.changhong.fileplore.service.DownLoadService"));
        super.onDestroy();
        System.exit(0);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.img_apk:

                intent.setClass(MainActivity.this, ClassifyListActivity.class);
                intent.putExtra("key", R.id.img_apk);
                startActivity(intent);

                break;
            case R.id.img_movie:
                intent.setClass(MainActivity.this, ClassifyGridActivity.class);
                intent.putExtra("key", R.id.img_movie);
                startActivity(intent);
                break;
            case R.id.img_music:

                intent.setClass(MainActivity.this, ClassifyListActivity.class);
                intent.putExtra("key", R.id.img_music);
                startActivity(intent);
                break;
            case R.id.img_photo:
                intent.setClass(MainActivity.this, ClassifyGridActivity.class);
                intent.putExtra("key", R.id.img_photo);
                startActivity(intent);
                break;
            case R.id.img_txt:

                intent.setClass(MainActivity.this, ClassifyListActivity.class);
                intent.putExtra("key", R.id.img_txt);
                startActivity(intent);
                break;
            case R.id.img_zip:

                intent.setClass(MainActivity.this, ClassifyListActivity.class);
                intent.putExtra("key", R.id.img_zip);
                startActivity(intent);
                break;
            case R.id.browse_rl_net:
                if (isshare) {
                    intent.setClass(MainActivity.this, ShowNetDevActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(MainActivity.this, "没开启共享", Toast.LENGTH_SHORT).show();
                break;
            case R.id.browse_rl_downlist:
                intent.setClass(MainActivity.this, ShowDownFileActivity.class);
                startActivity(intent);
                break;

            case R.id.img_qq:

                intent.setClass(MainActivity.this, QQListActivity.class);
                intent.putExtra("key", R.id.img_qq);
                startActivity(intent);

                break;
            case R.id.img_wechat:

                intent.setClass(MainActivity.this, QQListActivity.class);
                intent.putExtra("key", R.id.img_wechat);
                startActivity(intent);

                break;
            case R.id.img_app:

                intent.setClass(MainActivity.this, QQListActivity.class);
                intent.putExtra("key", R.id.img_app);
                startActivity(intent);

                break;
            case R.id.left_set:
                intent.setClass(MainActivity.this, SetActivity.class);
                startActivity(intent);
                break;
            case R.id.left_recivefile:
                intent.setClass(MainActivity.this, ShowReciveDevActivity.class);
                startActivity(intent);
            default:
                break;
        }

    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.img_apk:

                break;
            case R.id.img_movie:
                onLongClick("/Movies", "/DCIM/Camera", "video/*", v);
                break;
            case R.id.img_music:
                onLongClick("/Music", null, "audio/*", v);
                break;
            case R.id.img_photo:
                onLongClick("/DCIM/Camera", "/Pictures/Saved Pictures", "image/*", v);
                break;
            case R.id.img_txt:

                break;
            case R.id.img_zip:

                break;
            case R.id.browse_rl_net:

                break;
            case R.id.browse_rl_downlist:

                break;
            default:
                break;
        }
        return true;
    }

    private void onLongClick(String foldername1, String foldername2, String type, View v) {
        ArrayList<File> filelist = new ArrayList<>();
        if (null != foldername1) {
            File file1 = new File("/storage/sdcard1" + foldername1);
            File file2 = new File("/storage/sdcard0" + foldername1);
            if (file1.exists() && file1.canRead() && file1.isDirectory()) {
                File[] files = file1.listFiles();
                for (File file : files) {
                    if (Utils.getMIMEType(file).equals(type))
                        filelist.add(file);
                }
            }
            if (file2.exists() && file2.canRead() && file2.isDirectory()) {
                File[] files = file2.listFiles();
                for (File file : files)
                    if (Utils.getMIMEType(file).equals(type))
                        filelist.add(file);
            }

        }
        if (null != foldername2) {
            File file3 = new File("/storage/sdcard1" + foldername2);
            File file4 = new File("/storage/sdcard0" + foldername2);
            if (file3.exists() && file3.canRead() && file3.isDirectory()) {
                File[] files = file3.listFiles();
                for (File file : files)
                    if (Utils.getMIMEType(file).equals(type))
                        filelist.add(file);
            }
            if (file4.exists() && file4.canRead() && file4.isDirectory()) {
                File[] files = file4.listFiles();
                for (File file : files)
                    if (Utils.getMIMEType(file).equals(type))
                        filelist.add(file);
            }
        }

        final File[] files1 = filelist.toArray(new File[filelist.size()]);

        for (int i = 0; i < 4 && i < files1.length - 1; ++i) {
            for (int j = i + 1; j < files1.length; ++j) {
                if (files1[i].lastModified() < files1[j].lastModified()) {
                    File tmp = files1[j];
                    files1[j] = files1[i];
                    files1[i] = tmp;
                }
            }
        }
        FilePreViewFragment filePreViewFragment = new FilePreViewFragment();
        filePreViewFragment.setSource(v);
        Bundle bundle = new Bundle();
        bundle.putSerializable("filelist", files1);
        bundle.putInt("type", FilePreViewFragment.MAIN);
        filePreViewFragment.setArguments(bundle);
        filePreViewFragment.show(((Activity) context).getFragmentManager(), "filePreViewFragment");

    }


    private void onClickShare(File file) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType(Utils.getMIMEType(file));
        startActivity(shareIntent);

    }
}