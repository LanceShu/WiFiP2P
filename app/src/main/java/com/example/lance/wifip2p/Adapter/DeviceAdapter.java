package com.example.lance.wifip2p.Adapter;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lance.wifip2p.Content;
import com.example.lance.wifip2p.R;
import com.example.lance.wifip2p.View.MainActivity;

import java.util.List;

/**
 * Created by Lance
 * on 2018/5/20.
 */

public class DeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<WifiP2pDevice> deviceList;

    public DeviceAdapter(Context context, List<WifiP2pDevice> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wifi_device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        WifiP2pDevice device = deviceList.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.deviceName.setText(device.deviceName);
        viewHolder.deviceAddress.setText(device.deviceAddress);
        viewHolder.deviceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = Message.obtain();
                message.obj = position;
                message.what = Content.UPDATE_DEVICES_INFO;
                MainActivity.mainHanlder.sendMessage(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView deviceName;
        private TextView deviceAddress;
        private LinearLayout deviceLayout;

        ViewHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name);
            deviceAddress = itemView.findViewById(R.id.device_address);
            deviceLayout = itemView.findViewById(R.id.device_layout);
        }
    }
}
