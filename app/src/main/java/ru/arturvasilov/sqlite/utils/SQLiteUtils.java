package ru.arturvasilov.sqlite.utils;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.arturvasilov.sqlite.core.SQLite;

/**
 * @author Artur Vasilov
 */
public class SQLiteUtils {

    private SQLiteUtils() {
    }

    public static void assertInitialized() throws IllegalStateException {
        SQLite.get();
    }

    public static boolean isEmptyCursor(@Nullable Cursor cursor) {
        if (cursor == null) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (cursor.isClosed()) {
            return true;
        }

        return !cursor.moveToFirst();
    }

    public static void safeCloseCursor(@Nullable Cursor cursor) {
        if (cursor == null || cursor.isClosed()) {
            return;
        }

        try {
            cursor.close();
        } catch (Exception ignored) {
        }
    }

    @NonNull
    public static String defaultDatabaseName() {
        return "ru.sqlite.database.database";
    }

    @NonNull
    public static String defaultUri() {
        return "ru.sqlite.database";
    }
}
