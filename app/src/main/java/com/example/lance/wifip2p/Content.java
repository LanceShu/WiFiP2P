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
    public static final int SCAN_WIFI_DEVICE = 5;
    public static final int CREATE_WIFI_DEVICE = 6;
    public static final int SCANNED_WIFI_AP_DEVICE = 7;

    public static List<SendFileBean> selectedFileList;
    public static List<ScanResult> scanResultList;
}
