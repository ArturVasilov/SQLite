package ru.arturvasilov.sqlite.core;

import android.database.ContentObserver;

import ru.arturvasilov.sqlite.utils.MainHandler;

/**
 * @author Artur Vasilov
 */
class DatabaseObserver extends ContentObserver {

    public DatabaseObserver() {
        super(MainHandler.getHandler());
    }

}
