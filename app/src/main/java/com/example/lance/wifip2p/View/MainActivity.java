package com.example.lance.wifip2p.View;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lance.wifip2p.Adapter.FileAdapter;
import com.example.lance.wifip2p.Content;
import com.example.lance.wifip2p.DataBean.FileBean;
import com.example.lance.wifip2p.DataBean.SendFileBean;
import com.example.lance.wifip2p.DataBean.WordList;
import com.example.lance.wifip2p.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Handler postHandler = new Handler();
    public static List<FileBean> wordList;
    private ProgressDialog progressDialog;
    private RecyclerView fileList;
    private Button sendFileBtn;

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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Scanning...");
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        sendFileBtn.setOnClickListener(this);
    }

    private void CheckPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
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
        }
    }
}
