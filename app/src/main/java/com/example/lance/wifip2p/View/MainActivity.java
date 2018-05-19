package com.example.lance.wifip2p.View;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lance.wifip2p.Adapter.FileAdapter;
import com.example.lance.wifip2p.Content;
import com.example.lance.wifip2p.DataBean.FileBean;
import com.example.lance.wifip2p.DataBean.SendFileBean;
import com.example.lance.wifip2p.DataBean.WordList;
import com.example.lance.wifip2p.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Handler postHandler = new Handler();
    public static List<FileBean> wordList;
    private List<WifiP2pDevice> deviceList = new ArrayList<>();

    private ProgressDialog progressDialog;
    private RecyclerView fileList;
    private Button sendFileBtn;
    private Button receiveFileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    private void initWight() {
        fileList = findViewById(R.id.file_list);
        sendFileBtn = findViewById(R.id.send_bt);
        receiveFileBtn = findViewById(R.id.receive_bt);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Scanning files...");
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        sendFileBtn.setOnClickListener(this);
        receiveFileBtn.setOnClickListener(this);
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
                progressDialog.setMessage("Scanning devices...");
                progressDialog.show();
                findServer();
                break;
            case R.id.receive_bt:
                createGroup();
                break;
        }
    }

    // Find the all devices can shot wifi;
    private void findServer() {
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e("SendBtn", "搜索设备成功");
            }

            @Override
            public void onFailure(int i) {
                Log.e("SendBtn", "搜索设备失败");
            }
        });
    }

    // connect the device;
    private void connectDevice(WifiP2pDevice wifiP2pDevice) {
        WifiP2pConfig config = new WifiP2pConfig();
        if (wifiP2pDevice != null) {
            config.deviceAddress = wifiP2pDevice.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i) {
                    Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onPeersInfo(Collection<WifiP2pDevice> wifiP2pDeviceList) {
        super.onPeersInfo(wifiP2pDeviceList);
        for (WifiP2pDevice device : wifiP2pDeviceList) {
            if (!deviceList.contains(device)) {
                deviceList.add(device);
            }
        }
        progressDialog.dismiss();
        showDeviceInfo();
    }

    private void showDeviceInfo() {
        if (deviceList != null) {
            for (WifiP2pDevice device : deviceList) {
                Log.e("设备: ", device.deviceName + "----" + device.deviceAddress);
            }
        }
    }

    // create the receiver's wifi group;
    public void createGroup() {
        wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "创建群组成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(MainActivity.this, "创建群组失败,请移除已有的组群或者连接同一WIFI重试", Toast.LENGTH_SHORT).show();
                removeGroup();
            }
        });
    }

    // remove the receiver's wifi group;
    public void removeGroup() {
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "移除群组成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(MainActivity.this, "移除组群失败,请创建组群重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
