package ru.arturvasilov.sqlite.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.sqlite.utils.SQLiteUtils;

/**
 * @author Artur Vasilov
 */
public class SQLite {

    static {
        System.loadLibrary("sqliteX");
    }

    private static SQLite sSQLite;

    private final Context mContext;

    private final Observers mObservers;

    private SQLite(@NonNull Context context) {
        mContext = context;
        mObservers = new Observers();
    }

    /**
     * This method creates singleton instance of the SQLite and allows you to use non-parametrized method
     * {@link SQLite#get()} at any place of your app.
     *
     * Typically you will call this method in your {@link android.app.Application} class like this:
     * <pre>
     * {@code
     * public class MyApplication extends Application {
     *  @Override
     *  public void onCreate() {
     *      super.onCreate();
     *      SQLite.initialize(this);
     *  }
     * }
     * </pre>
     *
     * @param context - any context to access the content provider
     * @return created singleton instance of SQLite
     */
    @NonNull
    public static SQLite initialize(@NonNull Context context) {
        SQLite sqLite = sSQLite;
        if (sqLite == null) {
            synchronized (SQLite.class) {
                sqLite = sSQLite;
                if (sqLite == null) {
                    sqLite = sSQLite = new SQLite(context.getApplicationContext());
                }
            }
        }
        return sqLite;
    }

    /**
     * You must be sure that you've initialized SQLite instance by {@link SQLite#initialize(Context)}
     * or {@link IllegalStateException} will be thrown.
     *
     * @return singleton instance of SQLite
     */
    @NonNull
    public static SQLite get() {
        if (sSQLite == null) {
            throw new IllegalStateException("You should call initialize(Context) first, to initialize the database");
        }
        return sSQLite;
    }

    /**
     * This methods returns all rows of the table.
     * If you want to specify query parameters, you should call {@link SQLite#query(Table, Where)}
     *
     * @param table - table you want to query
     * @return all rows from the table as a list of table class objects.
     */
    @NonNull
    public <T> List<T> query(@NonNull Table<T> table) {
        return query(table, Where.create());
    }

    /**
     * @param table - table you want to query
     * @param where - arguments for query
     * @return all rows from the table which satisfy where parameter as a list of table class objects.
     */
    @NonNull
    public <T> List<T> query(@NonNull Table<T> table, @NonNull Where where) {
        List<T> list = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(table.getUri(), null, where.where(), where.whereArgs(), null);
        try {
            if (SQLiteUtils.isEmptyCursor(cursor)) {
                return list;
            }
            do {
                T t = table.fromCursor(cursor);
                list.add(t);
            } while (cursor.moveToNext());
            return list;
        } finally {
            SQLiteUtils.safeCloseCursor(cursor);
        }
    }

    /**
     * Query for the first object in the table
     *
     * @param table - table you want to query
     * @return first object from the table or null if table is empty
     */
    @Nullable
    public <T> T querySingle(@NonNull Table<T> table) {
        return querySingle(table, Where.create());
    }

    /**
     * Query for the first object in the table which satisfy where parameter
     *
     * @param table - table you want to query
     * @param where - arguments for query
     * @return first object from the table or null if table is empty
     */
    @Nullable
    public <T> T querySingle(@NonNull Table<T> table, @NonNull Where where) {
        Cursor cursor = mContext.getContentResolver().query(table.getUri(), null, where.where(), where.whereArgs(), where.limit());
        try {
            if (SQLiteUtils.isEmptyCursor(cursor)) {
                return null;
            }
            return table.fromCursor(cursor);
        } finally {
            SQLiteUtils.safeCloseCursor(cursor);
        }
    }

    /**
     * This method inserts object to the table. In cases of conflict the old object will be replaced by the new one.
     *
     * @param table - table in which you want to insert object
     * @param object - object to insert in database
     * @return uri of inserted object. In most cases you won't use it.
     */
    @Nullable
    public <T> Uri insert(@NonNull Table<T> table, @NonNull T object) {
        Uri uri = mContext.getContentResolver().insert(table.getUri(), table.toValues(object));
        if (uri != null) {
            notifyTableChanged(table);
        }
        return uri;
    }

    /**
     * This method inserts objects to the table. In cases of conflict the old objects will be replaced by the new ones.
     *
     * @param table - table in which you want to insert objects
     * @param objects - list of objects to insert in database
     * @return count of successfully inserted objects
     */
    public <T> int insert(@NonNull Table<T> table, @NonNull List<T> objects) {
        ContentValues[] values = new ContentValues[objects.size()];
        for (int i = 0; i < objects.size(); i++) {
            values[i] = table.toValues(objects.get(i));
        }
        int count = mContext.getContentResolver().bulkInsert(table.getUri(), values);
        if (count > 0) {
            notifyTableChanged(table);
        }
        return count;
    }

    /**
     * This method clears the table passed as a parameters
     * If you want to specify parameters for deleting, you should call {@link SQLite#delete(Table, Where)}
     *
     * @param table - table you want to clear
     * @return count of deleted rows
     */
    public <T> int delete(@NonNull Table<T> table) {
        return delete(table, Where.create());
    }

    /**
     * This method deletes all rows in the table which satisfy where parameter
     *
     * @param table - table from which you want to delete rows
     * @param where - arguments for delete rows from the table
     * @return count of deleted objects
     */
    public <T> int delete(@NonNull Table<T> table, @NonNull Where where) {
        int count = mContext.getContentResolver().delete(table.getUri(), where.where(), where.whereArgs());
        if (count > 0) {
            notifyTableChanged(table);
        }
        return count;
    }

    /**
     * This method updates all rows in the table which satisfy where parameter
     *
     * @param table - table where you want to update rows
     * @param where - arguments for update rows in the table
     * @param newObject - object which will replace all rows which satisfy where parameter
     * @return count of updated objects
     */
    public <T> int update(@NonNull Table<T> table, @NonNull Where where, @NonNull T newObject) {
        int count = mContext.getContentResolver().update(table.getUri(), table.toValues(newObject),
                where.where(), where.whereArgs());
        if (count > 0) {
            notifyTableChanged(table);
        }
        return count;
    }

    /**
     * Attaches callback to get notified about changes in certain table
     * For more information take a look at {@link BasicTableObserver}
     *
     * @param table - table to observe changes in
     * @param observer - listener which will be called when table changes
     */
    public <T> void registerObserver(@NonNull Table<T> table, @NonNull final BasicTableObserver observer) {
        mObservers.registerObserver(mContext, table, observer);
    }

    /**
     * Attaches callback to get notified about changes in certain table and query all rows
     * For more information take a look at {@link ContentTableObserver}
     *
     * {@link SQLite#registerObserver(Table, ContentTableObserver, Where)}
     *
     * @param table - table to observe changes in
     * @param observer - listener which will be called when table changes
     */
    public <T> void registerObserver(@NonNull Table<T> table, @NonNull final ContentTableObserver<T> observer) {
        mObservers.registerObserver(mContext, table, observer, Where.create());
    }

    /**
     * Attaches callback to get notified about changes in certain table and query rows which satisfies where parameter
     * For more information take a look at {@link ContentTableObserver}
     *
     * @param table - table to observe changes in
     * @param observer - listener which will be called when table changes
     * @param where - arguments for query
     */
    public <T> void registerObserver(@NonNull Table<T> table, @NonNull ContentTableObserver<T> observer, @NonNull Where where) {
        mObservers.registerObserver(mContext, table, observer, where);
    }

    /**
     * Detaches listener from observing changes in database
     *
     * @param observer - listener to detach from ContentProvider notifications
     */
    public void unregisterObserver(@NonNull BasicTableObserver observer) {
        mObservers.unregisterObserver(mContext, observer);
    }

    /**
     * Detaches listener from observing changes in database
     *
     * @param observer - listener to detach from ContentProvider notifications
     */
    public <T> void unregisterObserver(@NonNull ContentTableObserver<T> observer) {
        mObservers.unregisterObserver(mContext, observer);
    }

    private <T> void notifyTableChanged(@NonNull Table<T> table) {
        mContext.getContentResolver().notifyChange(table.getUri(), null);
    }

    @VisibleForTesting
    static void reset() {
        sSQLite = null;
    }
}
