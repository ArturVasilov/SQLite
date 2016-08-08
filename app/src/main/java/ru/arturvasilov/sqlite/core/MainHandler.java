package ru.arturvasilov.sqlite.core;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * @author Artur Vasilov
 */
class MainHandler {

    private static Handler sHandler;

    private MainHandler() {
    }

    @NonNull
    public static Handler getHandler() {
        Handler handler = sHandler;
        if (handler == null) {
            synchronized (MainHandler.class) {
                handler = sHandler;
                if (handler == null) {
                    handler = sHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return handler;
    }

}
