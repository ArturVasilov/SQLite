package ru.arturvasilov.sqlite.observers;

import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;

/**
 * @author Artur Vasilov
 */
public class DatabaseObserver extends ContentObserver {

    public DatabaseObserver() {
        super(new Handler(Looper.getMainLooper()));
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

}
