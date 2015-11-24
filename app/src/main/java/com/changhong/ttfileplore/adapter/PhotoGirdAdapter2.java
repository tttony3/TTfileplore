package com.changhong.ttfileplore.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.ttfileplore.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

public class PhotoGirdAdapter2 extends BaseAdapter {
	ArrayList<File> results;
	private LayoutInflater inflater;
	ImageLoader imageLoader =ImageLoader.getInstance() ;
	public int x;
	public int y;
	public void updateList(ArrayList<File> results) {
		this.results = results;
		notifyDataSetChanged();
	}

	public PhotoGirdAdapter2(ArrayList<File> results, Context context) {
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
			convertView = inflater.inflate(R.layout.griditem_classify, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.griditem_text);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.griditem_img);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
		viewHolder.title.setText(results.get(position).getName());
		final String path = results.get(position).getPath();
		imageLoader.displayImage("file://" + path, viewHolder.image);
		convertView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					x = (int) event.getX();
					y = (int) event.getY();
				} else {
					x = 0;
					y = 0;
				}
				return false;
			}

		});
		return convertView;
	}
	class ViewHolder {
		public TextView title;
		public ImageView image;
	}
}

