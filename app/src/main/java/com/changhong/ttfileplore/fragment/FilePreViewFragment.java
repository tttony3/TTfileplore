package com.changhong.ttfileplore.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.activities.PloreActivity;
import com.changhong.ttfileplore.application.MyApp;
import com.changhong.ttfileplore.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

public class FilePreViewFragment extends DialogFragment implements View.OnClickListener, View.OnLongClickListener {
	public static int MAIN =1;
	public static int OTHER =2;
	TableRow tr_1;
	TableRow tr_2 ;
	TableRow tr_3 ;
	TableRow tr_4 ;
	TextView tv_1 ;
	TextView tv_2;
	TextView tv_3 ;
	TextView tv_4 ;
	ImageView iv_1 ;
	ImageView iv_2 ;
	ImageView iv_3;
	ImageView iv_4 ;
	Context baseContext;
	String filePath;
	File file;
	File[] resultfiles;
	ImageLoader imageLoader = ImageLoader.getInstance();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		baseContext = getActivity();
		if(baseContext instanceof PloreActivity){
			baseContext = MyApp.mainContext;
			Log.e("instan","instan");
		}
		View view = inflater.inflate(R.layout.fragment_filepreview_dialog, container);
		findView(view,container);

		Bundle bundle = getArguments();
		if(bundle.getInt("type")==2) {
			filePath = bundle.getString("filePath");
			file = new File(filePath);
			File[] files = file.listFiles();
			resultfiles = getMaxSort(files);
		}
		else if(bundle.getInt("type")==1){
			resultfiles=(File[])bundle.getSerializable("filelist");
		}
		initView();

		return view;
	}

	private void initView() {


		switch(resultfiles.length){
			case 0:
				tv_1.setText("最近没有文件");
				iv_1.setVisibility(View.GONE);
				tr_2.setVisibility(View.GONE);
				tr_3.setVisibility(View.GONE);
				tr_4.setVisibility(View.GONE);
				return;
			case 1:
				tr_2.setVisibility(View.GONE);
				tr_3.setVisibility(View.GONE);
				tr_4.setVisibility(View.GONE);
				break;
			case 2:
				tr_3.setVisibility(View.GONE);
				tr_4.setVisibility(View.GONE);
				break;
			case 3:
				tr_4.setVisibility(View.GONE);
				break;
		}
		tr_1.setOnClickListener(this);
		tr_2.setOnClickListener(this);
		tr_3.setOnClickListener(this);
		tr_4.setOnClickListener(this);
		tv_1.setOnClickListener(this);
		tv_2.setOnClickListener(this);
		tv_3.setOnClickListener(this);
		tv_4.setOnClickListener(this);
		iv_1 .setOnClickListener(this);
		iv_2 .setOnClickListener(this);
		iv_3 .setOnClickListener(this);
		iv_4 .setOnClickListener(this);
		tr_1.setOnLongClickListener(this);
		tr_2.setOnLongClickListener(this);
		tr_3.setOnLongClickListener(this);
		tr_4.setOnLongClickListener(this);
		tv_1.setOnLongClickListener(this);
		tv_2.setOnLongClickListener(this);
		tv_3.setOnLongClickListener(this);
		tv_4.setOnLongClickListener(this);
		iv_1.setOnLongClickListener(this);
		iv_2 .setOnLongClickListener(this);
		iv_3 .setOnLongClickListener(this);
		iv_4.setOnLongClickListener(this);
		if (resultfiles.length >= 1 && resultfiles[0] != null) {
			tv_1.setText(resultfiles[0].getName());
			setImage(iv_1, resultfiles[0]);


		}
		if (resultfiles.length >= 2 && resultfiles[1] != null) {
			tv_2.setText(resultfiles[1].getName());
			setImage(iv_2, resultfiles[1]);
		}
		if (resultfiles.length >= 3 && resultfiles[2] != null) {
			tv_3.setText(resultfiles[2].getName());
			setImage(iv_3, resultfiles[2]);
		}
		if (resultfiles.length >= 4 && resultfiles[3] != null) {
			tv_4.setText(resultfiles[3].getName());
			setImage(iv_4, resultfiles[3]);
		}
	}

	private void findView(View view,ViewGroup container) {
		 tr_1 = (TableRow) view.findViewById(R.id.tr_filepreview_1);
		 tr_2 = (TableRow) view.findViewById(R.id.tr_filepreview_2);
		 tr_3 = (TableRow) view.findViewById(R.id.tr_filepreview_3);
		 tr_4 = (TableRow) view.findViewById(R.id.tr_filepreview_4);
		 tv_1 = (TextView) view.findViewById(R.id.tv_filepreview_1);
		 tv_2 = (TextView) view.findViewById(R.id.tv_filepreview_2);
		 tv_3 = (TextView) view.findViewById(R.id.tv_filepreview_3);
		 tv_4 = (TextView) view.findViewById(R.id.tv_filepreview_4);
		 iv_1 = (ImageView) view.findViewById(R.id.iv_filepreview_1);
		 iv_2 = (ImageView) view.findViewById(R.id.iv_filepreview_2);
		 iv_3 = (ImageView) view.findViewById(R.id.iv_filepreview_3);
		 iv_4 = (ImageView) view.findViewById(R.id.iv_filepreview_4);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.iv_filepreview_1:
			case R.id.tv_filepreview_1:
			case R.id.tr_filepreview_1:
				baseContext.startActivity(Utils.openFile(resultfiles[0]));
				break;
			case R.id.iv_filepreview_2:
			case R.id.tv_filepreview_2:
			case R.id.tr_filepreview_2:
				baseContext.startActivity(Utils.openFile(resultfiles[1]));

			case R.id.iv_filepreview_3:
			case R.id.tv_filepreview_3:
			case R.id.tr_filepreview_3:
				baseContext.startActivity(Utils.openFile(resultfiles[2]));
				break;
			case R.id.iv_filepreview_4:
			case R.id.tv_filepreview_4:
			case R.id.tr_filepreview_4:
				baseContext.startActivity(Utils.openFile(resultfiles[3]));
				break;
		}

	}

	private File[] getMaxSort(File[] files) {
		ArrayList<File> list = new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()) {
				list.add(files[i]);
			}
		}
		File[] files1 = list.toArray(new File[list.size()]);
		for (int i = 0; i<4; ++i) {
			for (int j = i+1; j < files1.length; ++j) {
				if (files1[i].lastModified() < files1[j].lastModified()) {
					File tmp = files1[j];
					files1[j] = files1[i];
					files1[i] = tmp;
				}
			}
		}
		return files1;
	}

	private void setImage(ImageView iv_1, File file) {
		switch (Utils.getMIMEType(file.getName())) {
			case "video":
				final String path1 = file.getPath();
				//	final String name1 = file.getName();
				imageLoader.displayImage("file://" + path1, iv_1);
				// viewHolder.img.setImageResource(R.drawable.file_icon_movie);
				break;
			case "audio":
				iv_1.setImageResource(R.drawable.file_icon_music);
				break;
			case "image":
				final String path = file.getPath();
				imageLoader.displayImage("file://" + path, iv_1);

				break;
			case "doc":
				iv_1.setImageResource(R.drawable.file_icon_txt);
				break;
			case "*":
				iv_1.setImageResource(R.drawable.file_icon_unknown);
				break;
			case "zip":
				iv_1.setImageResource(R.drawable.file_icon_zip);
				break;
			case "apk":
				iv_1.setImageResource(R.drawable.file_icon_apk);
				break;
			default:
				break;
		}

	}

	@Override
	public boolean onLongClick(View v) {
		MoreDialogFragment moreDialog = new MoreDialogFragment();
		Bundle bundle = new Bundle();
		switch (v.getId()){
			case R.id.iv_filepreview_1:
			case R.id.tv_filepreview_1:
			case R.id.tr_filepreview_1:
				bundle.putString("filePath", resultfiles[0].getPath());
				moreDialog.setArguments(bundle);
				moreDialog.show(getFragmentManager(), "moreDialog");
				break;
			case R.id.iv_filepreview_2:
			case R.id.tv_filepreview_2:
			case R.id.tr_filepreview_2:
				bundle.putString("filePath", resultfiles[1].getPath());
				moreDialog.setArguments(bundle);
				moreDialog.show(getFragmentManager(), "moreDialog");
				break;
			case R.id.iv_filepreview_3:
			case R.id.tv_filepreview_3:
			case R.id.tr_filepreview_3:
				bundle.putString("filePath", resultfiles[2].getPath());
				moreDialog.setArguments(bundle);
				moreDialog.show(getFragmentManager(), "moreDialog");
				break;
			case R.id.iv_filepreview_4:
			case R.id.tv_filepreview_4:
			case R.id.tr_filepreview_4:
				bundle.putString("filePath", resultfiles[3].getPath());
				moreDialog.setArguments(bundle);
				moreDialog.show(getFragmentManager(), "moreDialog");
				break;
		}
		return true;
	}
}
