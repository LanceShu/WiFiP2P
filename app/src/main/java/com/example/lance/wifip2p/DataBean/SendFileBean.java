package com.example.lance.wifip2p.DataBean;

/**
 * Created by Lance
 * on 2018/5/16.
 */

public class SendFileBean {
    public static final String serialVersionUID = "44544519970924120522220018";
    private String sendName;
    private String sendPath;
    private String sendSize;
    private String sendIcon;
    private String sendType;
    private String md5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setSendIcon(String sendIcon) {
        this.sendIcon = sendIcon;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public void setSendPath(String sendPath) {
        this.sendPath = sendPath;
    }

    public void setSendSize(String sendSize) {
        this.sendSize = sendSize;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getSendType() {
        return sendType;
    }

    public String getSendIcon() {
        return sendIcon;
    }

    public String getSendName() {
        return sendName;
    }

    public String getSendPath() {
        return sendPath;
    }

    public String getSendSize() {
        return sendSize;
    }
}
