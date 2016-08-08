package ru.arturvasilov.sqlite.core;

import android.database.ContentObserver;

/**
 * @author Artur Vasilov
 */
class DatabaseObserver extends ContentObserver {

    public DatabaseObserver() {
        super(MainHandler.getHandler());
    }

}
