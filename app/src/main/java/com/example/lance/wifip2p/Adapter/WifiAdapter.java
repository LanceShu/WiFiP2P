package com.example.lance.wifip2p.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lance.wifip2p.Content;
import com.example.lance.wifip2p.R;
import com.example.lance.wifip2p.Utils.WifiAPManager;

import java.util.List;

/**
 * Created by Lance
 * on 2018/5/20.
 */

public class WifiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ScanResult> scanResults;
    private WifiManager wifiManager;
    private WifiConfiguration config;
    private WifiAPManager wifiAPManager;

    public WifiAdapter(Context context, List<ScanResult> scanResults, WifiManager wifiManager, WifiConfiguration config, WifiAPManager wifiAPManager) {
        this.context = context;
        this.scanResults = scanResults;
        this.wifiManager = wifiManager;
        this.config = config;
        this.wifiAPManager = wifiAPManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wifi_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ScanResult scanResult = scanResults.get(position);
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
        viewHolder.wifiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiManager.disconnect();
                String capabilities = scanResult.capabilities;
                int type = Content.WIFICIPHER_WPA;
                if (!TextUtils.isEmpty(capabilities)) {
                    if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                        type = Content.WIFICIPHER_WPA;
                    } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                        type = Content.WIFICIPHER_WEP;
                    } else {
                        type = Content.WIFICIPHER_NOPASS;
                    }
                }
                config = wifiAPManager.isExsitis(wifiManager, scanResult.SSID);
                if (config == null) {
                    Log.e("type", type + "");
                    if (type != Content.WIFICIPHER_NOPASS) {
                        final EditText editText = new EditText(context);
                        final int finalType = type;
                        new AlertDialog.Builder(context).setTitle("请输入Wifi密码").setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        config = wifiAPManager.createWifiInfo(scanResult.SSID, editText.getText().toString(), finalType);
                                        wifiAPManager.connect(wifiManager, config);
                                    }
                                }).setNegativeButton("取消", null).show();
                    } else {
                        config = wifiAPManager.createWifiInfo(scanResult.SSID, "", type);
                        wifiAPManager.connect(wifiManager, config);
                    }
                } else {
                    wifiAPManager.connect(wifiManager, config);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return scanResults.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView wifiName;
        private TextView wifiLevel;
        private LinearLayout wifiLayout;

        ViewHolder(View itemView) {
            super(itemView);
            wifiName = itemView.findViewById(R.id.wifi_name);
            wifiLevel = itemView.findViewById(R.id.wifi_level);
            wifiLayout = itemView.findViewById(R.id.wifi_layout);
        }
    }
}
