package com.changhong.ttfileplore.fragment;

import com.changhong.ttfileplore.R;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class DetailDialogFragment extends DialogFragment {
    TextView tv_name;
    TextView tv_url;
    TextView tv_space;
    TextView tv_time;
    String name;
    String url;
    String time;
    String space;

    public DetailDialogFragment() {
        super();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = inflater.inflate(R.layout.fragment_detaildialog, container);
        findView(view);
        Bundle bundle = getArguments();
        name = bundle.getString("name");
        time = bundle.getString("time");
        space = bundle.getString("space");
        url = bundle.getString("path");
        initView();

        return view;
    }

    private void initView() {
        tv_name.setText(name);
        tv_url.setText(url);
        tv_space.setText(space);
        tv_time.setText(time);

    }

    private void findView(View view) {
        tv_name = (TextView) view.findViewById(R.id.tv_detail_name);
        tv_url = (TextView) view.findViewById(R.id.tv_detail_url);
        tv_space = (TextView) view.findViewById(R.id.tv_detail_space);
        tv_time = (TextView) view.findViewById(R.id.tv_detail_time);

    }

}
