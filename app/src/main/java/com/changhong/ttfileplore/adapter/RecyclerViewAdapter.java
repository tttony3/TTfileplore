package com.changhong.ttfileplore.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.activities.PloreActivity;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.fragment.FilePreViewFragment;
import com.changhong.ttfileplore.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private boolean show_cb = false;
    private Context context;
    private boolean isAllSelect = false;

    public interface OnItemClickLitener {
        void onClickImg(View v, File file);

        void onItemClick(RecyclerViewAdapter adapter, View view, int position);

        void onItemLongClick(RecyclerViewAdapter adapter, View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    ImageLoader imageLoader;
    static final private int DOC = 1;
    static final private int MUSIC = 2;
    static final private int PHOTO = 3;
    static final private int ZIP = 4;
    static final private int MOVIE = 5;
    static final private int UNKNOW = 6;
    static final private int APK = 7;

    private List<File> files;
    private Boolean[] checkbox_list;

    public Boolean[] getCheckBox_List() {
        return checkbox_list;
    }

    public RecyclerViewAdapter(Context context, List<File> files, ImageLoader imageLoader) {
        this.context = context;
        this.files = files;
        this.imageLoader = imageLoader;
        checkbox_list = new Boolean[files.size()];
        for (int i = 0; i < checkbox_list.length; i++) {
            checkbox_list[i] = false;
        }
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPES.Footer) {
            view = new ImageView(context);
            view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, Utils.dpTopx(40, context)));
        } else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_plore, parent, false);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder viewHolder, final int position) {
        if (position == files.size())
            return;
        final File file = files.get(position);
        if (mOnItemClickLitener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(RecyclerViewAdapter.this, viewHolder.itemView, position);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickLitener.onItemLongClick(RecyclerViewAdapter.this, viewHolder.itemView, position);
                    return false;
                }
            });
        }
        String fileName = file.getName();
        if (fileName.toLowerCase().equals("sdcard0"))
            viewHolder.name.setText("手机空间");
        else if (fileName.toLowerCase().equals("sdcard1"))
            viewHolder.name.setText("SD卡空间");
        else
            viewHolder.name.setText(fileName);
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
        if (file.isDirectory()) {
            viewHolder.time.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(file.lastModified()));
            viewHolder.img.setImageResource(R.drawable.file_icon_folder);
            viewHolder.img.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((OnItemClickLitener) context).onClickImg(v, file);

                }
            });
            viewHolder.img.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    if (!file.exists() || !file.canRead() || !file.isDirectory())
                        return false;
                    FilePreViewFragment filePreViewFragment = new FilePreViewFragment();
                    filePreViewFragment.setSource(v);
                    Bundle bundle = new Bundle();
                    bundle.putString("filePath", file.getPath());
                    bundle.putInt("type", FilePreViewFragment.OTHER);
                    filePreViewFragment.setArguments(bundle);
                    if (context instanceof PloreActivity)
                        filePreViewFragment.show(((Activity) MyApp.mainContext).getFragmentManager(), "filePreViewFragment");
                    else
                        filePreViewFragment.show(((Activity) context).getFragmentManager(), "filePreViewFragment");
                    return true;
                }

            });

        } else {
            viewHolder.img.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((OnItemClickLitener) context).onClickImg(v, file);

                }
            });
            viewHolder.img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });

            viewHolder.time.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm").format((file).lastModified()));

            switch (getMIMEType(fileName)) {
                case MOVIE:
                    final String path1 = file.getPath();
                    imageLoader.displayImage("file://" + path1, viewHolder.img);
                    break;
                case MUSIC:
                    viewHolder.img.setImageResource(R.drawable.file_icon_music);
                    break;
                case PHOTO:
                    final String path = file.getPath();
                    imageLoader.displayImage("file://" + path, viewHolder.img);


                    break;
                case DOC:
                    viewHolder.img.setImageResource(R.drawable.file_icon_txt);
                    break;
                case UNKNOW:
                    viewHolder.img.setImageResource(R.drawable.file_icon_unknown);
                    break;
                case ZIP:
                    viewHolder.img.setImageResource(R.drawable.file_icon_zip);
                    break;
                case APK:
                    viewHolder.img.setImageResource(R.drawable.file_icon_apk);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return files.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cb;
        private ImageView img;
        private TextView name;
        private TextView time;

        public ViewHolder(View view) {
            super(view);
            cb = (CheckBox) view.findViewById(R.id.cb);
            name = (TextView) view.findViewById(R.id.filename);
            time = (TextView) view.findViewById(R.id.lasttime);
            img = (ImageView) view.findViewById(R.id.fileimg);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position == files.size())
            return VIEW_TYPES.Footer;
        else
            return VIEW_TYPES.Normal;

    }

    public int getMIMEType(String name) {

        String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("wav")) {
            return MUSIC;
        } else if (end.equals("mp4") || end.equals("3gp")) {
            return MOVIE;
        } else if (end.equals("jpg") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")
                || end.equals("gif")) {
            return PHOTO;
        } else if (end.equals("zip") || end.equals("rar")) {
            return ZIP;
        } else if (end.equals("doc") || end.equals("docx") || end.equals("txt")) {
            return DOC;
        } else if (end.equals("apk")) {
            return APK;
        }
        return UNKNOW;
    }

    public boolean isShow_cb() {
        return show_cb;
    }

    public void setShow_cb(boolean show_cb) {
        this.show_cb = show_cb;
    }

    public Object getItem(int position) {

        return files.get(position);
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

    public void updateList(List<File> files) {
        this.files = files;
        checkbox_list = new Boolean[files.size()];
        for (int i = 0; i < checkbox_list.length; i++) {
            checkbox_list[i] = false;
        }
        notifyDataSetChanged();
    }

    public List<File> getAllFiles() {

        return files;
    }

    private class VIEW_TYPES {
        public static final int Header = 1;
        public static final int Normal = 2;
        public static final int Footer = 3;
    }
}
