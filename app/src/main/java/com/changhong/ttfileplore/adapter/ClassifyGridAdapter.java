package com.changhong.ttfileplore.adapter;

import java.util.ArrayList;

import com.changhong.ttfileplore.utils.Content;
import com.changhong.ttfileplore.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ClassifyGridAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<Content> pictures;
	private int type = 0;
public int x;
	public int y ;
	/**
	 * 
	 * @param pictures
	 *            适配列表
	 */
	public void updateList(ArrayList<Content> pictures){
		this.pictures = pictures;
		notifyDataSetChanged();
	}
	public ClassifyGridAdapter(ArrayList<Content> pictures, Context context, int type) {
		super();
		this.pictures = pictures;
		inflater = LayoutInflater.from(context);
		this.type = type;

	}

	@Override
	public int getCount() {
		if (null != pictures) {
			return pictures.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return pictures.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.griditem_classify, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.griditem_text);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.griditem_img);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.title.setText(pictures.get(position).getTitle());
		if (null != pictures.get(position).getImg())
			viewHolder.image.setImageBitmap(pictures.get(position).getImg());
		else if (type == R.id.img_music)
			viewHolder.image.setBackgroundResource(R.drawable.file_icon_music);
		else if (type == R.id.img_movie)
			viewHolder.image.setBackgroundResource(R.drawable.file_icon_movie);
		else if (type == R.id.img_txt)
			viewHolder.image.setBackgroundResource(R.drawable.file_icon_txt);
		else if (type == R.id.img_zip)
			viewHolder.image.setBackgroundResource(R.drawable.file_icon_zip);
		else if (type == R.id.img_photo)
			viewHolder.image.setBackgroundResource(R.drawable.file_icon_photo);
		else
			viewHolder.image.setBackgroundResource(R.drawable.file_icon_unknown);
		convertView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					x=(int)event.getX();
					y=(int)event.getY();
				}else{
					x=0;y=0;
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
