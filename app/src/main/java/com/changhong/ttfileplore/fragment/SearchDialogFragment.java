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
import android.widget.Toast;

import com.changhong.ttfileplore.R;

public class SearchDialogFragment extends DialogFragment {
    EditText et_filename;
    Button btn_search;
    Button btn_cancel;

    public interface OnClickSearchDialog {
        boolean onSearch(String filename);

    }

    public SearchDialogFragment() {
        super();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = inflater.inflate(R.layout.fragment_seachdialog, container);
        findView(view);

        initView();

        return view;
    }

    private void initView() {
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof OnClickSearchDialog) {
                    ((OnClickSearchDialog) getActivity()).onSearch(et_filename.getText().toString());
                    SearchDialogFragment.this.dismiss();


                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDialogFragment.this.dismiss();
            }
        });

    }

    private void findView(View view) {
        et_filename = (EditText) view.findViewById(R.id.et_filename);
        btn_search = (Button) view.findViewById(R.id.btn_searchdialog_search);
        btn_cancel = (Button) view.findViewById(R.id.btn_searchdialog_cancel);

    }

}
