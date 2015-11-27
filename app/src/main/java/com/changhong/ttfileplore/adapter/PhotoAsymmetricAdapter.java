package com.changhong.ttfileplore.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.data.FileImplAsymmeric;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class PhotoAsymmetricAdapter extends BaseAdapter {
    List<FileImplAsymmeric> results;
    private LayoutInflater inflater;
    ImageLoader imageLoader = ImageLoader.getInstance();
    public int x;
    public int y;

    public void updateList(ArrayList<FileImplAsymmeric> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    public PhotoAsymmetricAdapter(List<FileImplAsymmeric> results, Context context) {
        this.results = results;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.griditem_photo_grid, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.griditem_text);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.griditem_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
        viewHolder.title.setText(results.get(position).getFile().getName());
        final String path = results.get(position).getFile().getPath();
        imageLoader.displayImage("file://" + path, viewHolder.image);
        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("touch", "touch");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    x = (int) event.getX();
                    y = (int) event.getY();
                    Log.e("txy", x + " " + y);
                }
                return false;
            }

        });
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, 0.4f, 0.4f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        convertView.setAnimation(animationSet);
        animationSet.setDuration(1200);
        return convertView;
    }

    public void removeNotContain(ArrayList<String> tmp) {
        for (FileImplAsymmeric f : results) {
            if (!tmp.contains(f.getFile().getName())) {
                results.remove(f);
            }
        }
        //	notifyDataSetChanged();
    }

    class ViewHolder {
        public TextView title;
        public ImageView image;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? 1 : 0;
    }

}

