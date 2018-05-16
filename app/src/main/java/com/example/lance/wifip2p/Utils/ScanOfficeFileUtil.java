package com.example.lance.wifip2p.Utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.lance.wifip2p.DataBean.FileBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lance
 * on 2018/5/16.
 */

public class ScanOfficeFileUtil {

    // Get all Word Files in the phone;
    public static List<FileBean> getWordFilesList(Context context) {
        return scanFileList(context, new String[]{".doc", ".docx", ".wps"});
    }

    // Get all PPT Files in the phone;
    public static List<FileBean> getPPTFilesList(Context context) {
        return scanFileList(context, new String[]{".ppt",".pptx",".dps"});
    }

    // Get all Excel Files in the phone;
    public static List<FileBean> getExcelFilesList(Context context) {
        return scanFileList(context, new String[]{".xls",".xlsx",".et"});
    }

    // Get all PDF Files in the phone;
    public static List<FileBean> getPDFFilesList(Context context) {
        return scanFileList(context, new String[]{".pdf"});
    }

    // Scanning all type Files in the phone;
    private static List<FileBean> scanFileList(Context context, String[] extension) {
        List<FileBean> wordList = new ArrayList<>();
        String selection = "";
        for (int i = 0; i < extension.length; i++) {
            if (i != 0) {
                selection += " OR ";
            }
            selection += MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'";
        }

        Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external")
                , new String[]{MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE}
                , selection, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                FileBean file = new FileBean();
                if (cursor.getString(0) != null) {
                    file.setFileName(cursor.getString(0));
                } else {
                    file.setFileName("unknown");
                }
                if (cursor.getString(1) != null) {
                    file.setFilePath(cursor.getString(1));
                } else {
                    file.setFilePath("unknown");
                }
                if (cursor.getInt(2) != 0) {
                    Float size = cursor.getInt(2) / 1024F / 1024F;
                    file.setFileSize(size.toString().substring(0, 3));
                } else {
                    file.setFileSize("unknown");
                }
                file.setFileSelected(false);
                wordList.add(file);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return wordList;
    }
}
