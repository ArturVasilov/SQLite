package ru.arturvasilov.sqlite.utils;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.arturvasilov.sqlite.core.SQLite;
import ru.arturvasilov.sqlite.core.Table;

/**
 * Utility class for safe methods to work with database.
 *
 * @author Artur Vasilov
 */
public class SQLiteUtils {

    private SQLiteUtils() {
    }

    /**
     * Fast method to determine if any rows in database exists in table
     *
     * @param table to test if it's empty
     * @return whatever table is empty or not
     */
    public static <T> boolean isTableEmpty(@NonNull Table<T> table) {
        Cursor cursor = SQLite.get().getContentResolver().query(
                table.getUri(),
                new String[] {"count(*) AS count"},
                null,
                null,
                null);
        try {
            return cursor == null || cursor.getCount() == 0
                    || !cursor.moveToFirst() || cursor.getInt(0) == 0;
        }
        finally {
            safeCloseCursor(cursor);
        }
    }

    /**
     * Method to test if any rows in cursor exists. It's safe, you can pass null cursor or closed, e.g.
     *
     * @param cursor - cursor you want to check if it's empty
     * @return - true if cursor is null, closed or empty or false in other cases
     */
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

    /**
     * Closing cursor was always the hell (since you have to check if it's null or closed and so on,
     * but this method handles all the cases and safely closes the cursor.
     *
     * @param cursor - cursor you want to close
     */
    public static void safeCloseCursor(@Nullable Cursor cursor) {
        if (cursor == null || cursor.isClosed()) {
            return;
        }

        try {
            cursor.close();
        } catch (Exception ignored) {
        }
    }
}
