package com.example.lance.wifip2p.View;

import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.lance.wifip2p.Interface.Wifip2pActionListener;
import com.example.lance.wifip2p.Receiver.Wifip2pReceiver;

import java.util.Collection;

/**
 * Created by Lance
 * on 2018/5/18.
 */

public class BaseActivity extends AppCompatActivity implements Wifip2pActionListener {

    public WifiP2pManager wifiP2pManager;
    public WifiP2pManager.Channel channel;
    public Wifip2pReceiver wifip2pReceiver;
    public WifiP2pInfo wifiP2pInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // register WifiP2pManager;
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), this);
        // register broadcast
        wifip2pReceiver = new Wifip2pReceiver(wifiP2pManager, channel, this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        registerReceiver(wifip2pReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifip2pReceiver);
        wifip2pReceiver = null;
    }

    @Override
    public void wifiP2pEnabled(boolean enabled) {
        Log.e("BaseActivity", "传输通道是否可用：" + enabled);
    }

    @Override
    public void onConnection(WifiP2pInfo wifiP2pInfo) {
        if (wifiP2pInfo != null) {
            this.wifiP2pInfo = wifiP2pInfo;
            Log.e("BaseActivity", "WifiP2pInfo:" + wifiP2pInfo);
        }
    }

    @Override
    public void onDisconnection() {
        Log.e("BaseActivity", "连接断开");
    }

    @Override
    public void onDeviceInfo(WifiP2pDevice wifiP2pDevice) {
        Log.e("BaseActivity", "当前的的设备名称: " + wifiP2pDevice.deviceName + " --- " + wifiP2pDevice.deviceAddress);
    }

    @Override
    public void onPeersInfo(Collection<WifiP2pDevice> wifiP2pDeviceList) {
        for (WifiP2pDevice device : wifiP2pDeviceList) {
            Log.e("BaseActivity", "连接的设备信息：" + device.deviceName + "--------" + device.deviceAddress);
        }
    }

    @Override
    public void onChannelDisconnected() {

    }
}
