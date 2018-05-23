package com.example.lance.wifip2p.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.util.Log;

import com.example.lance.wifip2p.Content;
import com.example.lance.wifip2p.View.MainActivity;

/**
 * Created by Lance
 * on 2018/5/23.
 */

public class WifiAPBroadcastReceiver extends BroadcastReceiver {

    private WifiManager wifiManager;
    private Context context;

    public WifiAPBroadcastReceiver(WifiManager wifiManager, Context context) {
        this.wifiManager = wifiManager;
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    // scanned available wifi successfully;
                    Log.e("wifi", wifiManager.getScanResults().size() + "");
                    Content.scanResultList.clear();
                    Content.scanResultList.addAll(wifiManager.getScanResults());
                    Message message = Message.obtain();
                    message.what = Content.SCANNED_WIFI_AP_DEVICE;
                    MainActivity.mainHanlder.sendMessage(message);
                    break;
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_ENABLED:
                            wifiManager.startScan();
                            break;
                        case WifiManager.WIFI_STATE_DISABLING:
                            break;
                    }
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                        Log.e("wifiState", "wifi is disconnected");
                    } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        Log.e("wifiState", "the wifi's SSID: " + wifiInfo.getSSID());
                        Message message1 = Message.obtain();
                        message1.what = Content.WIFI_DEVICE_CONNECTED;
                        MainActivity.mainHanlder.sendMessage(message1);
                        if (wifiInfo.getSSID().equals(Content.defaultSSID)) {
                            Log.e("wifiState", "this is hotpot");
                        } else {
                            Log.e("wifiState", "this is wifi");
                        }
                    }
                    break;
            }
        }
    }
}
