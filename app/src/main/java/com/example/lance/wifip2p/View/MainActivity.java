package com.example.lance.wifip2p.View;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lance.wifip2p.Adapter.DeviceAdapter;
import com.example.lance.wifip2p.Adapter.FileAdapter;
import com.example.lance.wifip2p.Content;
import com.example.lance.wifip2p.DataBean.FileBean;
import com.example.lance.wifip2p.DataBean.SendFileBean;
import com.example.lance.wifip2p.DataBean.WordList;
import com.example.lance.wifip2p.R;
import com.example.lance.wifip2p.Utils.WifiAPManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Handler postHandler = new Handler();
    private WifiManager wifiManager;
    private WifiAPManager wifiAPManager;

    public static List<FileBean> wordList;
    private List<WifiP2pDevice> deviceList = new ArrayList<>();
    private DeviceAdapter deviceAdapter;

    public static final int SCAN_WIFI_DEVICE = 1;
    public static final int CREATE_WIFI_DEVICE = 2;
    public static final int WIFI_DEVICE_CONNECTED = 3;
    public static final int WIFI_STATE_CLOSED = 4;
    private int wifiType;

    private ProgressDialog progressDialog;
    private Dialog devicesDialog;
    private RecyclerView fileList;
    private Button sendFileBtn;
    private Button receiveFileBtn;
    private Button scanWifiBtn;
    private Button createWifiBtn;

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

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainHanlder = new MainHanlder(this);
        wifiAPManager = new WifiAPManager(this);
        if (wifiManager == null) {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        }
        if (Content.selectedFileList == null) {
            Content.selectedFileList = new ArrayList<>();
        } else {
            Content.selectedFileList.clear();
        }
        // Dynamic application permissions;
        CheckPermission();
        // Init all wights;
        initWight();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    if (wifiAPManager.getConnectedIP().size() > 0) {
                        Log.e("device", wifiAPManager.getConnectedIP().get(0));
                        break;
                    }
                }
            }
        }).start();
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
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS)
//                != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.WRITE_SETTINGS);
//        }
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
                // clear the deviceList when click the button;
                deviceList.clear();
                wifiType = SCAN_WIFI_DEVICE;
                // open the scanning wifi devices dialog;
                devicesDialog = new Dialog(MainActivity.this);
                devicesDialog.setContentView(R.layout.scan_wifi_devices);
                RecyclerView devicesList = devicesDialog.findViewById(R.id.devices_list);
                deviceAdapter = new DeviceAdapter(MainActivity.this, deviceList);
                LinearLayoutManager manager = new LinearLayoutManager(this);
                manager.setOrientation(LinearLayoutManager.VERTICAL);
                devicesList.setLayoutManager(manager);
                devicesList.setAdapter(deviceAdapter);
                devicesDialog.show();
                break;
            case R.id.create_wifi_bt:
                if (!wifiAPManager.isWifiApEnabled()) {
                    wifiAPManager.createAndStartWifiAp(null, null, true);
                } else {
                    wifiAPManager.closeWifiAp();
                }
                break;
        }
    }
}
