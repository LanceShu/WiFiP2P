package com.example.lance.wifip2p.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import com.example.lance.wifip2p.Interface.Wifip2pActionListener;

/**
 * Created by Lance
 * on 2018/5/18.
 */

public class Wifip2pReceiver extends BroadcastReceiver{

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private Wifip2pActionListener listener;

    public Wifip2pReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, Wifip2pActionListener listener) {
        this.manager = manager;
        this.channel = channel;
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                // whether wifi is enable;
                case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        listener.wifiP2pEnabled(true);
                    } else {
                        listener.wifiP2pEnabled(false);
                    }
                    break;
                // when the wifi's connected devices are changed;
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                    manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                            listener.onPeersInfo(wifiP2pDeviceList.getDeviceList());
                        }
                    });
                    break;
                // when the wifi's connected state is changed;
                case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                    if (networkInfo.isConnected()) {
                        manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                            @Override
                            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                                listener.onConnection(wifiP2pInfo);
                            }
                        });
                    } else {
                        listener.onDisconnection();
                    }
                    break;
                // when this device's state is changed;
                case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                    WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                    listener.onDeviceInfo(device);
                    break;
                default:
                    break;
            }
        }
    }
}
