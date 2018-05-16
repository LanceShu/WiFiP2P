package com.example.lance.wifip2p.DataBean;

import android.content.Context;

import com.example.lance.wifip2p.Utils.ScanOfficeFileUtil;

import java.util.List;

/**
 * Created by Lance
 * on 2018/5/16.
 */

public class WordList {
    private static List<FileBean> wordList;
    private WordList(){}
    public static List<FileBean> getWordListInstance(Context context) {
        if (wordList == null) {
            synchronized (WordList.class) {
                if (wordList == null) {
                    wordList = ScanOfficeFileUtil.getWordFilesList(context);
                }
            }
        }
        return wordList;
    }
}
