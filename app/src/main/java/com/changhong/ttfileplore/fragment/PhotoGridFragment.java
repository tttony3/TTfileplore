package com.changhong.ttfileplore.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.activities.PhotoActivity;
import com.changhong.ttfileplore.adapter.PhotoGirdAdapter2;
import com.changhong.ttfileplore.utils.Content;
import com.changhong.ttfileplore.utils.Utils;
import com.changhong.ttfileplore.view.PopupMoreDialog;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tangli on 2015/11/3.
 * Website: https://github.com/tttony3
 */
public class PhotoGridFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private View view ;
    private String path;
    private GridView gv_photogrid;
    private File file ;
    private PhotoGirdAdapter2 photoGirdAdapter2;
    private Context baseContext ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_photo_grid, container, false);
        baseContext = getActivity();
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
        for(File tmpfile :tmp){
            if(Utils.getMIMEType(tmpfile).equals("image/*"))
                files.add(tmpfile);
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
        WindowManager.LayoutParams lp=getActivity().getWindow().getAttributes();
        lp.alpha=0.5f;
        getActivity().getWindow().setAttributes(lp);
        PopupMoreDialog p = new PopupMoreDialog(getActivity(),Utils.dpTopx(150,baseContext), ViewGroup.LayoutParams.WRAP_CONTENT, true,file.getPath());
        int[] viewLocation = new int[2];
        view.getLocationInWindow(viewLocation);
        int viewX = viewLocation[0]; // x 坐标
        int viewY = viewLocation[1]; // y 坐标
        Point point = new Point();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getSize(point);

        if(photoGirdAdapter2.x!=0&&photoGirdAdapter2.y!=0){
            if (point.x -viewX- photoGirdAdapter2.x > Utils.dpTopx(150,baseContext)) {
                if (point.y -viewY- photoGirdAdapter2.y > Utils.dpTopx(240,baseContext)) {
                    p.setAnimationStyle(R.style.PopupAnimationTop);
                    p.showAtLocation(view, Gravity.NO_GRAVITY, viewX + photoGirdAdapter2.x, viewY + photoGirdAdapter2.y);
                } else {
                    p.setAnimationStyle(R.style.PopupAnimationBottom);
                    p.showAtLocation(view, Gravity.NO_GRAVITY, viewX +  photoGirdAdapter2.x, viewY - Utils.dpTopx(240,baseContext)+photoGirdAdapter2.y);
                }
            } else {
                if (point.y - viewY-photoGirdAdapter2.y >Utils.dpTopx(240,baseContext)) {
                    p.setAnimationStyle(R.style.PopupAnimationTopRight);
                    p.showAtLocation(view, Gravity.NO_GRAVITY, viewX + photoGirdAdapter2.x-Utils.dpTopx(150,baseContext), viewY + photoGirdAdapter2.y);
                } else {
                    p.setAnimationStyle(R.style.PopupAnimationBottomRight);
                    p.showAtLocation(view, Gravity.NO_GRAVITY, viewX + photoGirdAdapter2.x-Utils.dpTopx(150,baseContext), viewY - Utils.dpTopx(240,baseContext)+photoGirdAdapter2.y);
                }
            }
        }else{
        if (point.x - viewX > Utils.dpTopx(150,baseContext)) {
            if (point.y - viewY > Utils.dpTopx(240,baseContext)) {
                p.setAnimationStyle(R.style.PopupAnimationTop);
                p.showAsDropDown(view, Utils.dpTopx(80,baseContext), -Utils.dpTopx(80,baseContext));
            } else {
                p.setAnimationStyle(R.style.PopupAnimationBottom);
                p.showAtLocation(view, Gravity.NO_GRAVITY, viewX + Utils.dpTopx(80,baseContext), viewY - Utils.dpTopx(240,baseContext)+Utils.dpTopx(80,baseContext));
            }
        } else {
            if (point.y - viewY >Utils.dpTopx(240,baseContext)) {
                p.setAnimationStyle(R.style.PopupAnimationTopRight);
                p.showAsDropDown(view, -Utils.dpTopx(80,baseContext), -Utils.dpTopx(80,baseContext));
            } else {
                p.setAnimationStyle(R.style.PopupAnimationBottomRight);
                p.showAtLocation(view, Gravity.NO_GRAVITY, viewX - Utils.dpTopx(80,baseContext), viewY - Utils.dpTopx(240,baseContext)+Utils.dpTopx(80,baseContext));
            }
        }
        }

        p.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp=getActivity().getWindow().getAttributes();
                lp.alpha=1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });

            return true;


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = (File) parent.getItemAtPosition(position);
        Intent intent = Utils.openFile(file);
        startActivity(intent);
    }
}
