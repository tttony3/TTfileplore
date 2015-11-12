package com.changhong.ttfileplore.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.activities.PhotoActivity;
import com.changhong.ttfileplore.utils.Utils;
import com.changhong.ttfileplore.view.FlowLayout;
import com.changhong.ttfileplore.view.PopupMoreDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tangli on 2015/11/2.
 */
public class PhotoTimeLineFragment extends Fragment {
    private View view ;
    private ListView lv_photolist;
  private PhotoListAdapter photoListAdapter;
    private ArrayList<PhotoItem> fileitems= new ArrayList<>();
    private  File[] listFiles;
    private String path;
    ExecutorService threadPool=Executors.newFixedThreadPool(2);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view =inflater.inflate(R.layout.fragment_photo_timeline, container, false);
        Bundle b =getArguments();
        path =b.getString("path");
        Log.e("onCreateView","onCreateView");
        findView();
        initView();
        return view;
    }

    private void initView() {

        LoadDateTask task = new LoadDateTask();
        task.executeOnExecutor(threadPool);
    }

    private void findView() {
        lv_photolist = (ListView) view.findViewById(R.id.lv_photolist);
    }


    private void mapFile() {
        fileitems.clear();
        if(listFiles.length==1){
            if(Utils.getMIMEType(listFiles[0]).equals("image/*")){
                ArrayList<File> tmp = new ArrayList<>();
                tmp.add(listFiles[0]);
                PhotoItem photoItem=new PhotoItem();
                photoItem.setTime(tmp.get(0).lastModified());
                photoItem.setFiles(tmp);
                fileitems.add(photoItem);
            }
        }
        boolean isquit = false;
        for(int i =0 ;i<listFiles.length-1 && !isquit;i++) {
            ArrayList<File> tmp = new ArrayList<>();
            if(Utils.getMIMEType(listFiles[i]).equals("image/*")){
                tmp.add(listFiles[i]);
            }else
                continue;
            for (int j = i + 1; j < listFiles.length; j++) {
                if (listFiles[j].lastModified() - listFiles[i].lastModified() > -432000000) {
                    if(Utils.getMIMEType(listFiles[j]).equals("image/*")){
                        tmp.add(listFiles[j]);
                    }

                    if(j==listFiles.length-1){
                        isquit = true;
                        break;
                    }

                    if(tmp.size()>=16){
                        i=j;
                        break;
                    }
                }else{
                    i=j-1;
                    break;
                }

            }
            PhotoItem photoItem=new PhotoItem();
            photoItem.setTime(tmp.get(0).lastModified());
            photoItem.setFiles(tmp);
            fileitems.add(photoItem);
        }
    }

    private File[] sortByTime(File[] listFiles){
        for(int i =0;i<listFiles.length-1;i++)
            for(int j =i+1;j<listFiles.length;j++){
                if(listFiles[i].lastModified()<listFiles[j].lastModified()){
                    File tmp = listFiles[i];
                    listFiles[i] = listFiles[j];
                    listFiles[j] = tmp;
                }
            }
        return  listFiles;
    }


    class PhotoItem{

        long time;
        ArrayList<File> files ;

        public long getTime() {
            return time;
        }
        public void setTime(long time) {
            this.time = time;
        }
        public ArrayList<File> getFiles() {
            return files;
        }
        public void setFiles(ArrayList<File> files) {
            this.files = files;
        }


    }

    class PhotoListAdapter extends BaseAdapter{

        private ArrayList<PhotoItem> fileitems= new ArrayList<>();
        private ImageLoader imageLoader = ImageLoader.getInstance();

        PhotoListAdapter(ArrayList<PhotoItem> fileitems){
            this.fileitems =fileitems;

        }

        @Override
        public int getCount() {
            return fileitems.size();
        }

        @Override
        public Object getItem(int position) {
            return fileitems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_photo_timeline, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_left = (TextView) convertView.findViewById(R.id.tv_left);
                viewHolder.tv_right = (TextView) convertView.findViewById(R.id.tv_right);
                viewHolder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
                viewHolder.flowLayoutView= (FlowLayout) convertView.findViewById(R.id.flowlayout_photo);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.flowLayoutView.removeAllViews();
            PhotoItem tmp =fileitems.get(position);
            int size =tmp.getFiles().size();
            viewHolder.tv_left.setText(DateFormat.getDateInstance(DateFormat.SHORT).format((new Date(tmp.getTime()))));

            viewHolder.tv_right.setText(DateFormat.getDateInstance().format((new Date(tmp.getFiles().get(size - 1).lastModified()))));
            viewHolder.tv_right.append("-");
            viewHolder.tv_right.append(DateFormat.getDateInstance().format((new Date(tmp.getTime()))));
            viewHolder.tv_num.setText(size+"张");
            for(final File file :tmp.getFiles()){
                View tmpView =LayoutInflater.from(getActivity()).inflate(R.layout.griditem_timeline,null);
                ((TextView)tmpView.findViewById(R.id.griditem_text)).setText(file.getName());
                imageLoader.displayImage("file://" + file.getPath(), (ImageView) tmpView.findViewById(R.id.griditem_img));
                (tmpView.findViewById(R.id.griditem_img)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startActivity(Utils.openFile(file));
                    }
                });
                (tmpView.findViewById(R.id.griditem_img)).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        WindowManager.LayoutParams lp=getActivity().getWindow().getAttributes();
                        lp.alpha=0.5f;
                        getActivity().getWindow().setAttributes(lp);
                        PopupMoreDialog p = new PopupMoreDialog(getActivity(),300, ViewGroup.LayoutParams.WRAP_CONTENT, true,file.getPath());
                        p.showAsDropDown(v);
                        p.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                WindowManager.LayoutParams lp=getActivity().getWindow().getAttributes();
                                lp.alpha=1f;
                                getActivity().getWindow().setAttributes(lp);
                            }
                        });
//                        MoreDialogFragment moreDialog = new MoreDialogFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("filePath", file.getPath());
//                        moreDialog.setArguments(bundle);
//                        moreDialog.show(getActivity().getFragmentManager(), "detailDialog");
                        return true;
                    }
                });
                viewHolder.flowLayoutView.addView(tmpView);
            }

            return convertView;
        }

        class ViewHolder {
            public TextView tv_left;
            public TextView tv_right;
            public TextView tv_num;
            public FlowLayout flowLayoutView;
        }
    }
    class LoadDateTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            Log.e("go","go");
            File file = new File(path);
            listFiles= file.listFiles();
            listFiles =sortByTime(listFiles);
            mapFile();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ((PhotoActivity)getActivity()).dismissDialog();
            ((PhotoActivity)getActivity()).setPhotoNumText(fileitems.size() + "组");
            photoListAdapter = new PhotoListAdapter(fileitems);
            lv_photolist.setAdapter(photoListAdapter);

        }
    }
}
