package com.changhong.ttfileplore.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.activities.PhotoActivity;
import com.changhong.ttfileplore.adapter.PhotoGirdAdapter2;
import com.changhong.ttfileplore.utils.Content;
import com.changhong.ttfileplore.utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tangli on 2015/11/3.
 */
public class PhotoGridFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private View view ;
    private String path;
    private GridView gv_photogrid;
    private File file ;
    private PhotoGirdAdapter2 photoGirdAdapter2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_photo_grid, container, false);
        Bundle b =getArguments();
        path =b.getString("path");
        Log.e("onCreateView", "onCreateView");
        findView();
        initView();
        return view;
    }

    private void initView() {
         file = new File(path);
        ArrayList<File> files = new ArrayList<>();
        File[] tmp = file.listFiles();
        for(int i =0;i<tmp.length;i++){
            if(Utils.getMIMEType(tmp[i]).equals("image/*"))
                files.add(tmp[i]);
        }
        photoGirdAdapter2 = new PhotoGirdAdapter2(files,getActivity());
        gv_photogrid.setAdapter(photoGirdAdapter2);
        ((PhotoActivity)getActivity()).dismissDialog();
        ((PhotoActivity)getActivity()).setPhotoNumText(files.size() + "张");
        gv_photogrid.setOnItemClickListener(this);
        gv_photogrid.setOnItemLongClickListener(this);

    }

    private void findView() {
        gv_photogrid = (GridView)view.findViewById(R.id.gv_photogrid);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            MoreDialogFragment moreDialog = new MoreDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("filePath", file.getPath());
            moreDialog.setArguments(bundle);
            moreDialog.show(getActivity().getFragmentManager(), "moreDialog");
            return true;


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = (File) parent.getItemAtPosition(position);
        Intent intent = Utils.openFile(file);
        startActivity(intent);
    }
}
