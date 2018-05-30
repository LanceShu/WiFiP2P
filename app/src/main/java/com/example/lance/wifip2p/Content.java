package com.example.lance.wifip2p;

import android.net.wifi.ScanResult;

import com.example.lance.wifip2p.DataBean.SendFileBean;

import java.util.List;

/**
 * Created by Lance
 * on 2018/5/16.
 */

public class Content {
    public static final int SEND_FILE_LIST = 1;
    public static final int UPDATE_DEVICES_INFO = 2;
    public static final int WIFI_DEVICE_CONNECTED = 3;
    public static final int WIFI_STATE_CLOSED = 4;
    public static final int GET_WIFI_DEVICE_IP = 5;
    public static final int CREATE_WIFI_DEVICE = 6;
    public static final int SCANNED_WIFI_AP_DEVICE = 7;
    public static final int WIFICIPHER_WPA = 8;
    public static final int WIFICIPHER_WEP = 9;
    public static final int WIFICIPHER_NOPASS = 10;
    public static final int WIFI_AP_OPENED = 11;

    public static boolean isConnected = false;

    public static final String defaultSSID = "WIFI_AP_TEST";
    public static final String defaultPASS = "12345678";

    public static List<SendFileBean> selectedFileList;
    public static List<ScanResult> scanResultList;
}
