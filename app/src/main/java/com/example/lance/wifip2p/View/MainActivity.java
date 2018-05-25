package com.example.lance.wifip2p.View;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lance.wifip2p.Adapter.WifiAdapter;
import com.example.lance.wifip2p.Adapter.FileAdapter;
import com.example.lance.wifip2p.Content;
import com.example.lance.wifip2p.DataBean.FileBean;
import com.example.lance.wifip2p.DataBean.SendFileBean;
import com.example.lance.wifip2p.DataBean.WordList;
import com.example.lance.wifip2p.R;
import com.example.lance.wifip2p.Receiver.WifiAPBroadcastReceiver;
import com.example.lance.wifip2p.Utils.MessageUtil;
import com.example.lance.wifip2p.Utils.WifiAPManager;
import com.example.lance.wifip2p.Utils.WifiP2pThreadPoolExecute;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Handler postHandler = new Handler();
    private WifiManager wifiManager;
    private WifiAPManager wifiAPManager;
    private WifiAPBroadcastReceiver broadcastReceiver;
    private WifiConfiguration configuration;

    public static List<FileBean> wordList;
    private WifiAdapter deviceAdapter;
    private ThreadPoolExecutor executor;

    private ProgressDialog progressDialog;
    private Dialog devicesDialog;
    private RecyclerView fileList;
    private Button sendFileBtn;
    private Button receiveFileBtn;
    private Button scanWifiBtn;
    private Button createWifiBtn;
    private RecyclerView wifiList;

    public static MainHanlder mainHanlder;

    public static class MainHanlder extends Handler {

        private WeakReference<MainActivity> activityWeakReference;

        MainHanlder(MainActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case Content.SCANNED_WIFI_AP_DEVICE:
                        if (activity.deviceAdapter != null) {
                            Log.e("adapter", Content.scanResultList.size() + "");
                            activity.deviceAdapter.notifyDataSetChanged();
                        }
                        break;
                    case Content.WIFI_DEVICE_CONNECTED:
                        if (activity.progressDialog != null && activity.progressDialog.isShowing()) {
                            activity.progressDialog.dismiss();
                            Toast.makeText(activity, "Wifi热点已连接", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Content.WIFI_AP_OPENED:
                        if (activity.progressDialog != null && activity.progressDialog.isShowing()) {
                            activity.progressDialog.dismiss();
                            Toast.makeText(activity, "Wifi热点已开启", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Content.GET_WIFI_DEVICE_IP:
                        String device_ip = (String) msg.obj;
                        Toast.makeText(activity, "已连接设备IP：" + device_ip, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainHanlder = new MainHanlder(this);
        wifiAPManager = wifiAPManager == null ? new WifiAPManager(this) : wifiAPManager;
        configuration = configuration == null ? new WifiConfiguration() : configuration;
        executor = executor == null ? WifiP2pThreadPoolExecute.getThreadPoolExecute() : executor;
        wifiManager = wifiManager == null ? (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE) : wifiManager;
        if (Content.selectedFileList == null) {
            Content.selectedFileList = new ArrayList<>();
        } else {
            Content.selectedFileList.clear();
        }
        if (Content.scanResultList == null) {
            Content.scanResultList = new ArrayList<>();
        } else {
            Content.scanResultList.clear();
        }
        // Dynamic application permissions;
        CheckPermission();
        // Init all wights;
        initWight();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Init the BroadcastReceiver;
        initBroadcastReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void initBroadcastReceiver() {
        broadcastReceiver = new WifiAPBroadcastReceiver(wifiManager, this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainHanlder.removeCallbacksAndMessages(0);
    }

    private void initWight() {
        fileList = findViewById(R.id.file_list);
        sendFileBtn = findViewById(R.id.send_bt);
        receiveFileBtn = findViewById(R.id.receive_bt);
        scanWifiBtn = findViewById(R.id.scan_wifi_bt);
        createWifiBtn = findViewById(R.id.create_wifi_bt);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Scanning files...");
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        sendFileBtn.setOnClickListener(this);
        receiveFileBtn.setOnClickListener(this);
        scanWifiBtn.setOnClickListener(this);
        createWifiBtn.setOnClickListener(this);
    }

    private void CheckPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionList.isEmpty()) {
            // If do not apply for application permissions, we can do some operation;
            initData();
        }else{
            String[] permission = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permission, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        finish();
                        break;
                    }
                }
                // Init the data;
                initData();
            } else {
                finish();
            }
        }
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Get all Word files;
                wordList = WordList.getWordListInstance(MainActivity.this);
                // If all data are scanned, make the progressDialog dismiss;
                postHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (wordList != null) {
                            LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
                            manager.setOrientation(LinearLayoutManager.VERTICAL);
                            fileList.setLayoutManager(manager);
                            FileAdapter adapter = new FileAdapter(MainActivity.this, wordList, R.mipmap.ftf_word);
                            fileList.setAdapter(adapter);
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_bt:
                for (SendFileBean fileBean : Content.selectedFileList) {
                    Log.e("SendFileBean", fileBean.getSendName() + "  " + fileBean.getSendPath() + "  " + fileBean.getSendSize());
                }
                break;
            case R.id.receive_bt:
                break;
            case R.id.scan_wifi_bt:
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        searchWifiAP();
//                    }
//                }).start();
//                displayAllWifiAps();
                progressDialog.setMessage("Connecting wifi...");
                progressDialog.show();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!wifiManager.isWifiEnabled()) {
                            wifiManager.setWifiEnabled(true);
                        }
                        WifiConfiguration config = wifiAPManager.createWifiInfo(Content.defaultSSID, "", Content.WIFICIPHER_NOPASS);
                        wifiAPManager.connect(wifiManager, config);
                    }
                });
                break;
            case R.id.create_wifi_bt:
                if (!wifiAPManager.isWifiApEnabled()) {
                    progressDialog.setMessage("Opening Wifi_AP...");
                    progressDialog.show();
                    wifiAPManager.createWifiAp(true);
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            for (;;) {
                                if (wifiAPManager.getConnectedIP().size() > 0) {
                                    Log.e("Connected_Device", wifiAPManager.getConnectedIP().get(0));
                                    MessageUtil.sendMessageToHandler(Content.GET_WIFI_DEVICE_IP, wifiAPManager.getConnectedIP().get(0), mainHanlder);
                                    break;
                                }
                            }
                        }
                    });
                } else {
                    wifiAPManager.closeWifiAp();
                }
                break;
        }
    }

    private void searchWifiAP() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
    }

    private void displayAllWifiAps() {
        // open the scanning wifi devices dialog;
        devicesDialog = new Dialog(MainActivity.this);
        devicesDialog.setContentView(R.layout.scan_wifi_aps);
        wifiList = devicesDialog.findViewById(R.id.devices_list);
        deviceAdapter = new WifiAdapter(MainActivity.this, Content.scanResultList, wifiManager, configuration, wifiAPManager);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        wifiList.setLayoutManager(manager);
        wifiList.setAdapter(deviceAdapter);
        devicesDialog.show();
    }
}
