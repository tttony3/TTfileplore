package com.changhong.ttfileplore.adapter;

import java.util.ArrayList;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.data.AppInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class AppListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<AppInfo> appList;

    public AppListAdapter(Context context, ArrayList<AppInfo> appList) {
        this.context = context;
        this.appList = appList;
    }

    @Override
    public int getCount() {

        return appList.size();
    }

    @Override
    public Object getItem(int position) {

        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_plore, null);
            convertView.setTag(viewHolder);
            viewHolder.cb = (CheckBox) convertView.findViewById(R.id.cb);
            viewHolder.name = (TextView) convertView.findViewById(R.id.filename);
            viewHolder.time = (TextView) convertView.findViewById(R.id.lasttime);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.fileimg);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AppInfo tmp = (AppInfo) getItem(position);
        viewHolder.name.setText(tmp.appName);
        viewHolder.img.setImageDrawable(tmp.appIcon);
        viewHolder.time.setText(tmp.versionName);
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1f, 0.8f, 1f, 1f, 1f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        convertView.setAnimation(animationSet);
        animationSet.setDuration(250);
        return convertView;
    }

    class ViewHolder {
        private CheckBox cb;
        private ImageView img;
        private TextView name;
        private TextView time;
    }
}
