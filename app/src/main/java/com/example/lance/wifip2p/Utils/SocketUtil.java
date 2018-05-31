package com.example.lance.wifip2p.Utils;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Lance
 * on 2018/5/30.
 */

public class SocketUtil {

    private static final int socketPort = 10000;

    public static void createServerSocket() {
        try {
            ServerSocket serverSocket = new ServerSocket(socketPort);
            for (;;) {
                Socket socket = serverSocket.accept();
                if (socket.isConnected()) {
                    Log.e("serversocket", "success");
                    break;
                }
            }
//            for (;;) {
//                try {
//                    SendFileBean fileBean;
//                    fileBean = Content.sendFileList.take();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createClientSocket(String socketIP) {
        try {
            Socket socket = new Socket(socketIP, socketPort);
            if (socket.isConnected())
                Log.e("socket", "success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
