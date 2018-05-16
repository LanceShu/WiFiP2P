package com.example.lance.wifip2p.DataBean;

/**
 * Created by Lance
 * on 2018/5/16.
 */

public class FileBean {
    private String fileName;
    private String filePath;
    private String fileSize;
    private boolean isFileSelected;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileSelected(boolean fileSelected) {
        isFileSelected = fileSelected;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileSize() {
        return fileSize;
    }

    public boolean isFileSelected() {
        return isFileSelected;
    }
}
