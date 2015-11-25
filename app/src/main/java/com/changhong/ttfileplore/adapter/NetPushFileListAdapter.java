package com.changhong.ttfileplore.adapter;

import java.util.ArrayList;
import java.util.List;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class NetPushFileListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<String> pushList;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private Boolean[] checkbox_list;
    private boolean isshowcb = false;

    public NetPushFileListAdapter(List<String> pushList, Context context) {
        if (null == pushList) {
            this.pushList = new ArrayList<>();
        } else {
            this.pushList = pushList;
        }

        inflater = LayoutInflater.from(context);
        checkbox_list = new Boolean[this.pushList.size()];
        for (int i = 0; i < checkbox_list.length; i++) {
            checkbox_list[i] = false;
        }
    }

    @Override
    public int getCount() {
        return pushList.size();
    }

    @Override
    public Object getItem(int position) {
        return pushList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_pushfile, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_push_filename);
            viewHolder.url = (TextView) convertView.findViewById(R.id.tv_push_fileurl);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.im_pushfile);
            viewHolder.cb = (CheckBox) convertView.findViewById(R.id.cb_pushfile);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String loc = pushList.get(position);
        String type = Utils.getMIMEType(loc);
        if (type == "video") {
            imageLoader.displayImage(loc, viewHolder.img);
        } else if (type == "audio") {
            viewHolder.img.setImageResource(R.drawable.file_icon_music);
        } else if (type == "image") {
            imageLoader.displayImage(loc, viewHolder.img);
        } else if (type == "zip") {
            viewHolder.img.setImageResource(R.drawable.file_icon_zip);
        } else if (type == "apk") {
            viewHolder.img.setImageResource(R.drawable.file_icon_apk);
        } else if (type == "doc") {
            viewHolder.img.setImageResource(R.drawable.file_icon_txt);
        } else {
            viewHolder.img.setImageResource(R.drawable.file_icon_unknown);
        }

        viewHolder.name.setText(loc.subSequence(loc.lastIndexOf("/") + 1, loc.length()));
        viewHolder.url.setText(loc);
        if (isshowcb) {
            viewHolder.cb.setVisibility(View.VISIBLE);
        } else {
            viewHolder.cb.setVisibility(View.GONE);
        }
        viewHolder.cb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    checkbox_list[position] = true;
                } else
                    checkbox_list[position] = false;
            }
        });
        return convertView;
    }

    class ViewHolder {
        public ImageView img;
        public TextView name;
        public TextView url;
        public CheckBox cb;

    }

    public void updatelistview(List<String> list) {
        pushList.clear();
        if (list != null)
            pushList.addAll(list);
        notifyDataSetChanged();

    }

    public void setIsshowcb(boolean isshow) {
        isshowcb = isshow;
        notifyDataSetChanged();
    }

    public Boolean[] getCheckbox_list() {
        return checkbox_list;
    }

    public boolean isshowcb() {
        return isshowcb;
    }

}
