package com.example.lance.wifip2p.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Created by Lance
 * on 2018/5/19.
 */

public class Md5Util {

    public static String getMD5(String filePath) {
        File file = new File(filePath);
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream in = new FileInputStream(file);
            int len;
            byte[] bytes = new byte[1024];
            while((len = in.read(bytes)) != -1) {
                digest.update(bytes, 0, len);
            }
            byte[] bs = digest.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : bs) {
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
