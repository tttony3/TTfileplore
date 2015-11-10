package com.changhong.ttfileplore.adapter;

import java.util.ArrayList;
import java.util.List;

import com.changhong.alljoyn.simpleclient.DeviceInfo;
import com.changhong.ttfileplore.R;
import com.changhong.synergystorage.javadata.JavaFile;
import com.changhong.synergystorage.javadata.JavaFile.FileType;
import com.changhong.synergystorage.javadata.JavaFolder;
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

public class NetShareFileListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<JavaFile> fileList;
    private List<JavaFolder> folderList;
    private boolean isshowcb = false;
    DeviceInfo devInfo;
    private Boolean[] checkbox_list;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public NetShareFileListAdapter(List<JavaFile> fileList, List<JavaFolder> folderList, DeviceInfo devInfo,
                                   Context context) {
        this.devInfo = devInfo;
        if (null == fileList) {
            this.fileList = new ArrayList<JavaFile>();
        } else {
            this.fileList = fileList;
        }
        if (null == folderList) {
            this.folderList = new ArrayList<JavaFolder>();
        } else {
            this.folderList = folderList;
        }
        checkbox_list = new Boolean[this.fileList.size() + this.folderList.size()];
        for (int i = 0; i < checkbox_list.length; i++) {
            checkbox_list[i] = false;
        }
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        if (null == folderList)
            return fileList.size();
        if (null == fileList)
            return folderList.size();
        return fileList.size() + folderList.size();
    }

    @Override
    public Object getItem(int position) {
        return (position >= folderList.size()) ? fileList.get(position - folderList.size()) : folderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_netfile, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_net_filename);
            viewHolder.url = (TextView) convertView.findViewById(R.id.tv_net_fileurl);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.im_file);
            viewHolder.cb = (CheckBox) convertView.findViewById(R.id.cb_netfile);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.img.setImageDrawable(null);
        if (position >= folderList.size()) {
            JavaFile tmp = fileList.get(position - folderList.size());
            FileType filetype = tmp.getFileType();
            if (JavaFile.FileType.AUDIO == filetype)
                viewHolder.img.setImageResource(R.drawable.file_icon_music);
            else if (JavaFile.FileType.IMAGE == filetype) {
                imageLoader.displayImage(devInfo.getM_httpserverurl() + fileList.get(position - folderList.size()).getLocation(), viewHolder.img);
            } else if (JavaFile.FileType.VIDEO == filetype)
                viewHolder.img.setImageResource(R.drawable.file_icon_movie);
            else if (JavaFile.FileType.OTHER == filetype) {
                String type = Utils.getMIMEType(tmp.getFullName());
                if (type.equals("zip"))
                    viewHolder.img.setImageResource(R.drawable.file_icon_zip);
                else if (type.equals("doc"))
                    viewHolder.img.setImageResource(R.drawable.file_icon_txt);
                else if (type.equals("apk"))
                    viewHolder.img.setImageResource(R.drawable.file_icon_apk);
                else if (type.equals("*"))
                    viewHolder.img.setImageResource(R.drawable.file_icon_unknown);
            }

            viewHolder.name.setText(tmp.getFullName());
            viewHolder.url.setText(tmp.getLocation());
        } else if (position < folderList.size()) {
            viewHolder.img.setImageResource(R.drawable.file_icon_folder);
            viewHolder.name.setText(folderList.get(position).getName());
            viewHolder.url.setText(folderList.get(position).getLocation());
        }
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

    public void updatelistview(List<JavaFile> list1, List<JavaFolder> list2) {

        fileList.clear();
        folderList.clear();
        if (list1 != null)
            fileList.addAll(list1);
        if (list2 != null)
            folderList.addAll(list2);
        checkbox_list = new Boolean[fileList.size() + folderList.size()];
        for (int i = 0; i < checkbox_list.length; i++) {
            checkbox_list[i] = false;
        }
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
