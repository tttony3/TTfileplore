package com.changhong.ttfileplore.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.changhong.ttfileplore.R;

public abstract class PushDialogFragment extends DialogFragment {
    EditText ev_message;
    Button btn_push;
    Button btn_cancel;

    abstract public void onPushFragmentEnter(String message);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = inflater.inflate(R.layout.fragment_pushdialog, container);
        findView(view);

        initView();

        return view;
    }

    private void initView() {
        btn_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPushFragmentEnter(ev_message.getText().toString());
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushDialogFragment.this.dismiss();
            }
        });

    }

    private void findView(View view) {
        ev_message = (EditText) view.findViewById(R.id.ev_message);
        btn_push = (Button) view.findViewById(R.id.btn_pushdialog_push);
        btn_cancel = (Button) view.findViewById(R.id.btn_pushdialog_cancel);

    }

}
