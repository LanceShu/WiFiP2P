package com.example.lance.wifip2p.Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lance
 * on 2018/5/24.
 */

public class WifiP2pThreadPoolExecute {

    private static ThreadPoolExecutor executor;
    private WifiP2pThreadPoolExecute() {}
    public static ThreadPoolExecutor getThreadPoolExecute() {
        if (executor == null) {
            synchronized (WifiP2pThreadPoolExecute.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(4, 4, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
                }
            }
        }
        return executor;
    }
}
