package com.changhong.ttfileplore.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import com.changhong.alljoyn.simpleservice.FC_GetShareFile;
import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.data.DownData;
import com.changhong.ttfileplore.service.DownLoadService;
import com.changhong.ttfileplore.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import mehdi.sakout.fancybuttons.FancyButton;

public class DownFileListAdapter extends BaseAdapter {

    String download_Path;
    String appname;
    private LayoutInflater inflater;
    ArrayList<DownData> downList;
    ArrayList<DownData> alreadydownList;
    ArrayList<DownData> allList;
    Context context;
    DownLoadService downLoadService;
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.color.gray_alph)
            .showImageForEmptyUri(R.drawable.file_icon_photo).showImageOnFail(R.drawable.file_icon_photo)
            .cacheInMemory(true).cacheOnDisk(false).bitmapConfig(Bitmap.Config.RGB_565)
            //.displayer(new RoundedBitmapDisplayer(20)) // 设置图片的解码类型
            .displayer(new FadeInBitmapDisplayer(0))//是否图片加载好后渐入的动画时间
            .build();

    public DownFileListAdapter(ArrayList<DownData> downList, ArrayList<DownData> alreadydownList, Context context, DownLoadService downLoadService) {
        allList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        if (downList == null) {
            downList = new ArrayList<>();
        } else
            this.downList = downList;
        this.alreadydownList = alreadydownList;
        this.context = context;
        download_Path = Environment.getExternalStorageDirectory().getAbsolutePath();
        appname = FC_GetShareFile.getApplicationName(context);
        this.downLoadService = downLoadService;
        allList.addAll(downList);
        allList.addAll(alreadydownList);

    }

    @Override
    public int getCount() {

        return allList.size();
    }

    @Override
    public Object getItem(int position) {
        return allList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return allList.get(position).getTotalPart();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_downlist, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_process = (TextView) convertView.findViewById(R.id.tv_process);
            viewHolder.pb = (ProgressBar) convertView.findViewById(R.id.download_bar);
            viewHolder.btn = (FancyButton) convertView.findViewById(R.id.btn_download);
            viewHolder.iv = (ImageView) convertView.findViewById(R.id.iv_download);
            //	viewHolder.btn.setFocusable(false);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final DownData tmpdata = allList.get(position);
        if (!(new File(download_Path + "/" + appname + "/download/" + tmpdata.getName()).exists())) {
            boolean isdowning = false;
            for (DownData data : downList) {
                if (data.getName().equals(tmpdata.getName())) {
                    isdowning = true;
                    break;
                }

            }
            if (!isdowning) {
                viewHolder.tv_name.setText("文件已被删除");
                return convertView;
            }
        }
        viewHolder.tv_name.setText(tmpdata.getName());
        int max = (int) (tmpdata.getTotalPart() / 100);
        if (max == 0)
            max = 1;
        viewHolder.pb.setMax(max);
        long part = tmpdata.getCurPart();
        viewHolder.pb.setProgress((int) (part / 100));
        int p = (int) (part / max);
        viewHolder.tv_process.setText(p + "%");
        if (tmpdata.isDone()) {
            if (Utils.getMIMEType(tmpdata.getName()).equals("image") || Utils.getMIMEType(tmpdata.getName()).equals("video"))
                imageLoader.displayImage("File://" + download_Path + "/" + appname + "/download/" + tmpdata.getName(), viewHolder.iv,options);
            else if (Utils.getMIMEType(tmpdata.getName()).equals("audio"))
                viewHolder.iv.setImageResource(R.drawable.file_icon_music);
            else if (Utils.getMIMEType(tmpdata.getName()).equals("apk"))
                viewHolder.iv.setImageResource(R.drawable.file_icon_apk);
            else if (Utils.getMIMEType(tmpdata.getName()).equals("zip"))
                viewHolder.iv.setImageResource(R.drawable.file_icon_zip);
            else if (Utils.getMIMEType(tmpdata.getName()).equals("doc"))
                viewHolder.iv.setImageResource(R.drawable.file_icon_txt);
            else if (Utils.getMIMEType(tmpdata.getName()).equals("*"))
                viewHolder.iv.setImageResource(R.drawable.file_icon_unknown);
            viewHolder.pb.setProgress(viewHolder.pb.getMax());
            viewHolder.tv_process.setText("100%");
            viewHolder.btn.setText("打开");

            viewHolder.btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    context.startActivity(
                            Utils.openFile(new File(download_Path + "/" + appname + "/download/" + tmpdata.getName())));
                }
            });
        } else {
            if (Utils.getMIMEType(tmpdata.getName()).equals("image") || Utils.getMIMEType(tmpdata.getName()).equals("video"))
                imageLoader.displayImage(tmpdata.getUri(), viewHolder.iv,options);
            else if (Utils.getMIMEType(tmpdata.getName()).equals("audio"))
                viewHolder.iv.setImageResource(R.drawable.file_icon_music);
            else if (Utils.getMIMEType(tmpdata.getName()).equals("apk"))
                viewHolder.iv.setImageResource(R.drawable.file_icon_apk);
            else if (Utils.getMIMEType(tmpdata.getName()).equals("zip"))
                viewHolder.iv.setImageResource(R.drawable.file_icon_zip);
            else if (Utils.getMIMEType(tmpdata.getName()).equals("doc"))
                viewHolder.iv.setImageResource(R.drawable.file_icon_txt);
            else if (Utils.getMIMEType(tmpdata.getName()).equals("*"))
                viewHolder.iv.setImageResource(R.drawable.file_icon_unknown);
            if (!tmpdata.isCancel()) {
                viewHolder.btn.setText("取消");
                viewHolder.btn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        downLoadService.stopDownload(tmpdata.getUri());


                    }
                });
            } else {
                viewHolder.btn.setText("下载");
                viewHolder.btn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        downLoadService.addDownloadFile(tmpdata.getUri());

                    }
                });
            }
        }

        return convertView;
    }

    class ViewHolder {
        public TextView tv_process;
        public TextView tv_name;
        public ProgressBar pb;
        public FancyButton btn;
        public ImageView iv;
    }

    public void update(ArrayList<DownData> downList2, DownLoadService downLoadService2) {
        downList = downList2;
        downLoadService = downLoadService2;
        allList.clear();
        alreadydownList =Utils.getDownDataObject("alreadydownlist");
        allList.addAll(downList);
        allList.addAll(alreadydownList);

        notifyDataSetChanged();

    }

    public void updatedelete(ArrayList<DownData> downList2, ArrayList<DownData> alreadydownList, DownLoadService downLoadService2) {
        downList = downList2;
        downLoadService = downLoadService2;
        allList.clear();
        this.alreadydownList = alreadydownList;
        allList.addAll(downList);
        allList.addAll(alreadydownList);

        notifyDataSetChanged();

    }

}
