package com.example.lance.wifip2p.Utils;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

/**
 * Created by Lance
 * on 2018/5/21.
 */

public class WifiAPUtil {

    public static final int WIFI_AP_STATE_DISABLING = 0;
    public static final int WIFI_AP_STATE_DISABLED = 0;
    public static final int WIFI_AP_STATE_ENABLING = 0;
    public static final int WIFI_AP_STATE_ENABLED = 0;
    public static final int WIFI_AP_STATE_FAILED = 0;

    public static String defSSID = "WIFI_P2P_TEST";
    public static String defPassword = "11111111";

    public static void startWifiAP(WifiManager wifiManager) {
        startWifi(wifiManager, defSSID, defPassword);
    }

    public static void startWifiAP(WifiManager wifiManager, String SSID) {
        startWifi(wifiManager, SSID, defPassword);
    }

    public static void startWifiAP(WifiManager wifiManager, String SSID, String password) {
        startWifi(wifiManager, SSID, password);
    }

    private static void startWifi(WifiManager wifiManager, String SSID, String password) {
        if (wifiManager != null) {
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
            Method method = null;
            try {
                // use reflect to get WifiManager's methods;
                method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method.setAccessible(true);
                WifiConfiguration netConfig = new WifiConfiguration();
                if (SSID != null) {
                    netConfig.SSID = SSID;
                } else {
                    netConfig.SSID = defSSID;
                }
                if (password != null) {
                    netConfig.preSharedKey = password;
                } else {
                    netConfig.preSharedKey = defPassword;
                }
                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                method.invoke(wifiManager, netConfig, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int getWifiAPState(WifiManager wifiManager) {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            return (int) method.invoke(wifiManager);
        } catch (Exception e) {
            e.printStackTrace();
            return WIFI_AP_STATE_FAILED;
        }
    }
}
