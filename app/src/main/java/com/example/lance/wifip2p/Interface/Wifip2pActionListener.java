package com.example.lance.wifip2p.Interface;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.Collection;

/**
 * Created by Lance
 * on 2018/5/18.
 */

public interface Wifip2pActionListener extends WifiP2pManager.ChannelListener {
    // the state whether wifi is enable;
    void wifiP2pEnabled(boolean enabled);
    // when the wifi is connected;
    void onConnection(WifiP2pInfo wifiP2pInfo);
    // when the wifi is disconnected;
    void onDisconnection();
    // when the wifi is connected, the all wifi information for devices;
    void onDeviceInfo(WifiP2pDevice wifiP2pDevice);
    // the all wifi devices;
    void onPeersInfo(Collection<WifiP2pDevice> wifiP2pDeviceList);
}
