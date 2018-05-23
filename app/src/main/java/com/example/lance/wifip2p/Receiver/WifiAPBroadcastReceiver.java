package com.example.lance.wifip2p.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
            }
        }
    }
}
