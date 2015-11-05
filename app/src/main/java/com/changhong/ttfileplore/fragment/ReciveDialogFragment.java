package com.changhong.ttfileplore.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.changhong.ttfileplore.R;

public abstract class ReciveDialogFragment extends DialogFragment {
    TextView tv_message;
    Button btn_recive;
    Button btn_cancel;

    abstract public void onReciveFragmentEnter();
    abstract public void setReciveFragmentMessage(TextView tv_message);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_recivedialog, container);
        findView(view);

        initView();

        return view;
    }

    private void initView() {

        setReciveFragmentMessage(tv_message);
        btn_recive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReciveFragmentEnter();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReciveDialogFragment.this.dismiss();
            }
        });

    }

    private void findView(View view) {
        tv_message = (TextView) view.findViewById(R.id.tv_message);
        btn_recive = (Button) view.findViewById(R.id.btn_recivedialog_recive);
        btn_cancel = (Button) view.findViewById(R.id.btn_recivedialog_cancel);

    }

}
