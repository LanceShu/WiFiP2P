package com.example.lance.wifip2p.View;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.lance.wifip2p.DataBean.FileBean;
import com.example.lance.wifip2p.DataBean.WordList;
import com.example.lance.wifip2p.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Handler postHandler = new Handler();
    private List<FileBean> wordList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Dynamic application permissions;
        CheckPermission();
        // Init all wights;
        initWight();
    }

    private void initWight() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
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
                if (wordList != null) {
                    for (FileBean wordFile : wordList) {
                        Log.e("WordFile", wordFile.getFileName() + "-"
                                + wordFile.getFilePath() + "-" + wordFile.getFileSize());
                    }
                }
                // If all data are scanned, make the progressDialog dismiss;
                postHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }
}
