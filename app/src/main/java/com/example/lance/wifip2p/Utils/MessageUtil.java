package com.example.lance.wifip2p.Utils;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Lance
 * on 2018/5/25.
 */

public class MessageUtil {

    public static void sendMessageToHandler(int what, Object obj, Handler handler) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = obj;
        handler.sendMessage(message);
    }

    public static void sendMessageToHandler(int what, Handler handler) {
        Message message = Message.obtain();
        message.what = what;
        handler.sendMessage(message);
    }
}
