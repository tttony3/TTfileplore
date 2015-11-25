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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ClassifyListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Content> content;
    private int type;
    private Boolean[] checkbox_list;
    private boolean show_cb = false;
    private boolean isAllSelect = false;
    public int x = 0;
    public int y = 0;

    public ClassifyListAdapter(ArrayList<Content> content, Context context, int type) {
        super();
        this.content = content;
        inflater = LayoutInflater.from(context);
        this.type = type;
        checkbox_list = new Boolean[content.size()];
        for (int i = 0; i < checkbox_list.length; i++) {
            checkbox_list[i] = false;
        }
    }

    @Override
    public int getCount() {
        return content.size();
    }

    @Override
    public Object getItem(int position) {

        return content.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_plore, null);
            viewHolder = new ViewHolder();
            viewHolder.cb = (CheckBox) convertView.findViewById(R.id.cb);
            viewHolder.title = (TextView) convertView.findViewById(R.id.filename);
            viewHolder.singer = (TextView) convertView.findViewById(R.id.lasttime);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.fileimg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (show_cb) {
            viewHolder.cb.setChecked(checkbox_list[position]);
            viewHolder.cb.setVisibility(View.VISIBLE);

        } else {
            viewHolder.cb.setChecked(checkbox_list[position]);
            viewHolder.cb.setVisibility(View.GONE);

        }
        viewHolder.cb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkbox_list[position] = ((CheckBox) v).isChecked();
            }
        });
        viewHolder.title.setText(content.get(position).getTitle());
        if (type == R.id.img_music)
            viewHolder.singer.setText(content.get(position).getSinger());
        else
            viewHolder.singer.setText(content.get(position).getTime());
        if (null != content.get(position).getImg())
            viewHolder.image.setImageBitmap(content.get(position).getImg());
        else if (type == R.id.img_music)
            viewHolder.image.setImageResource(R.drawable.file_icon_music);
        else if (type == R.id.img_txt)
            viewHolder.image.setImageResource(R.drawable.file_icon_txt);
        else if (type == R.id.img_zip)
            viewHolder.image.setImageResource(R.drawable.file_icon_zip);
        else if (type == R.id.img_apk)
            viewHolder.image.setImageResource(R.drawable.file_icon_apk);
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
        public TextView singer;
        public ImageView image;
        public CheckBox cb;

    }

    public Boolean[] getCheckBox_List() {
        return checkbox_list;
    }

    public void setcheckbox_list(Boolean[] checkbox_list) {
        this.checkbox_list = checkbox_list;
    }

    public void setAllSelect() {
        if (!isAllSelect) {
            for (int i = 0; i < checkbox_list.length; i++) {
                checkbox_list[i] = true;
            }
            isAllSelect = true;
        } else {
            for (int i = 0; i < checkbox_list.length; i++) {
                checkbox_list[i] = false;
            }
            isAllSelect = false;
        }

    }

    public boolean isShow_cb() {
        return show_cb;
    }

    public void setShow_cb(boolean show_cb) {
        this.show_cb = show_cb;
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<Content> results) {
        content = results;
        checkbox_list = new Boolean[results.size()];
        for (int i = 0; i < checkbox_list.length; i++) {
            checkbox_list[i] = false;
        }
        show_cb = false;
        notifyDataSetChanged();

    }
}
