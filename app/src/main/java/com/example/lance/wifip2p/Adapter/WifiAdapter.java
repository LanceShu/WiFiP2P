package com.example.lance.wifip2p.Adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lance.wifip2p.R;

import java.util.List;

/**
 * Created by Lance
 * on 2018/5/20.
 */

public class WifiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ScanResult> scanResults;

    public WifiAdapter(Context context, List<ScanResult> scanResults) {
        this.context = context;
        this.scanResults = scanResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wifi_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ScanResult scanResult = scanResults.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.wifiName.setText(scanResult.SSID);
        int level = scanResult.level;
        String wifi_level;
        if (level <= 0 && level >= -50) {
            wifi_level = "信号很好";
        } else if (level < -50 && level >= -70) {
            wifi_level = "信号较好";
        } else if (level < -70 && level >= -80) {
            wifi_level = "信号一般";
        } else if (level < -80 && level >= -100) {
            wifi_level = "信号较差";
        } else {
            wifi_level = "信号很差";
        }
        viewHolder.wifiLevel.setText(wifi_level);
    }

    @Override
    public int getItemCount() {
        return scanResults.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView wifiName;
        private TextView wifiLevel;

        ViewHolder(View itemView) {
            super(itemView);
            wifiName = itemView.findViewById(R.id.wifi_name);
            wifiLevel = itemView.findViewById(R.id.wifi_level);
        }
    }
}
