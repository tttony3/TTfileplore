package com.changhong.ttfileplore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.adapter.ReciveListAdapter;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ShowReciveDevActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView lv_recivefile;
    private TextView tv_num;
    private TextView tv_path;
    private ReciveListAdapter reciveListAdapter;
    @Override
    protected void findView() {
        lv_recivefile = findView(R.id.lv_recivefile);
        tv_num = findView(R.id.tv_recivefilenum);
        tv_path = findView(R.id.tv_recivefilepath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recive_dev);
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        findView();
        reciveListAdapter = new ReciveListAdapter(MyApp.recivePushList,ShowReciveDevActivity.this);
        lv_recivefile.setAdapter(reciveListAdapter);
        lv_recivefile.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.lv_recivefile:
                Intent intent = new Intent();
                intent.setClass(ShowReciveDevActivity.this, ShowPushFileActivity.class);
                List<String> list=(List<String>)parent.getItemAtPosition(position);
                if(list.size()>0){
                    intent.putStringArrayListExtra("pushList",(ArrayList<String>)list);
                    intent.putExtra("hasJson",true);
                    startActivity(intent);
                }
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
