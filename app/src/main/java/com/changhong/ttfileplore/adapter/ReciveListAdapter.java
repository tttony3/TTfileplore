package com.changhong.ttfileplore.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.alljoyn.simpleclient.ClientBusHandler;
import com.changhong.alljoyn.simpleclient.DeviceInfo;
import com.changhong.ttfileplore.R;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangli on 2015/11/9.
 */
public class ReciveListAdapter extends BaseAdapter{
    private List<List<String>> reciveList =new ArrayList<>();
    private Context context;
    public ReciveListAdapter(List<List<String>> reciveList,Context context){
        this.reciveList.addAll(reciveList);
        this.context=context;
    }
    @Override
    public int getCount() {
        return reciveList.size();
    }

    @Override
    public Object getItem(int position) {
        return reciveList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_netfile, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_net_filename);
            viewHolder.tv_message = (TextView) convertView.findViewById(R.id.tv_net_fileurl);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.im_file);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
       List<String> tmpList =reciveList.get(position);
        String jsonString  =tmpList.get(0);
        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            viewHolder.tv_message.setText(jsonObj.getString("message"));
            String device_id = jsonObj.getString("device_id");
            String device_name="该设备已离线";
            for(DeviceInfo tmp : ClientBusHandler.List_DeviceInfo){
                if(tmp.getM_deviceid().equals(device_id)){
                    device_name = tmp.getM_devicename();
                    break;
                }
            }
            viewHolder.tv_title.setText(device_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewHolder.img.setImageResource(R.drawable.file_icon_folder);
        return convertView;
    }

    class ViewHolder{
        public TextView tv_title;
        public TextView tv_message;
        public ImageView img;
    }

}
