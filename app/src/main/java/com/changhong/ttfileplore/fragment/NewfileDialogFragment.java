package com.changhong.ttfileplore.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.changhong.ttfileplore.R;

public class NewfileDialogFragment extends DialogFragment {
    EditText et_filename;
    Button btn_newfile;
    Button btn_cancel;

    public interface OnClickNewfileDialog {
        boolean onNewfile(String filename);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_newfiledialog, container);
        findView(view);

        initView();

        return view;
    }

    private void initView() {
        btn_newfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("11", "33");
                if (getActivity() instanceof OnClickNewfileDialog) {
                    Log.e("11", "22");
                    ((OnClickNewfileDialog) getActivity()).onNewfile(et_filename.getText().toString());
                    NewfileDialogFragment.this.dismiss();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewfileDialogFragment.this.dismiss();
            }
        });

    }

    private void findView(View view) {
        et_filename = (EditText) view.findViewById(R.id.et_filename);
        btn_newfile = (Button) view.findViewById(R.id.btn_newfiledialog_newfile);
        btn_cancel = (Button) view.findViewById(R.id.btn_newfiledialog_cancel);

    }

}
