package ru.arturvasilov.sqlite.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.sqlite.database.sqlite.SQLiteDatabase;

/**
 * Interface for creating a single table in SQLite database.
 *
 * You normally won't implement this interface but extend {@link BaseTable},
 * which provides good enough implementations for most of the methods.
 *
 * @author Artur Vasilov
 */
public interface Table<T> {

    /**
     * Each table is registered in ContentProvider with Uri from this method.
     * Default implementation returns ContentProvider uri appended with table name.
     *
     * @return Uri for table in database
     */
    @NonNull
    Uri getUri();

    /**
     * This method is used for creating and dropping table in database
     * and in the default implementation for {@link Table#getUri()}
     *
     * Default implementation returns simple class name (e.g. UsersTable)
     *
     * @return name of the table in SQLite database
     */
    @NonNull
    String getTableName();

    /**
     * This method creates and adds table to the database.
     *
     * You can create it manually with SQL, but it's recommended to use {@link ru.arturvasilov.sqlite.utils.TableBuilder}
     *
     * @param database - instance of SQLite database where you should create the table
     */
    void onCreate(@NonNull SQLiteDatabase database);

    /**
     * This method is provided for data migration purposes.
     *
     * In this method you should decide how you want to update this table
     * (how data migration works - {@link Table#getLastUpgradeVersion()}
     *
     * Default implementation in {@link BaseTable} simple drops table and call {@link Table#onCreate(SQLiteDatabase)}
     *
     * @param database - instance of SQLite database where you should update the table
     */
    void onUpgrade(@NonNull SQLiteDatabase database);

    /**
     * This method is also provided for data migration purposes.
     *
     * Version of database is calculated as a maximum from all tables in {@link SQLiteSchema}.
     * When you have updated the table, you just have to override this method to return maximum value from all tables
     * (e.g. current version of database is 5, you overridden this method to return 6 - database version will be six
     * and {@link Table#onUpgrade(SQLiteDatabase)} will be called)
     * If this method returns value which is less than updated database version,
     * {@link Table#onUpgrade(SQLiteDatabase)} won't be called.
     *
     * @return the version of database where the table was lastly updated
     */
    int getLastUpgradeVersion();

    /**
     * @param t - object for this table which you need to convert to {@link ContentValues} to insert in to database
     * @return ContentValues instance for object
     */
    @NonNull
    ContentValues toValues(@NonNull T t);

    /**
     * In this method you need to create instance of class for this table from cursor.
     * It's guarantee that cursor at the right position and its' state is OK.
     *
     * @param cursor - cursor (opened and ready to be read)
     * @return instance of class for this table
     */
    @NonNull
    T fromCursor(@NonNull Cursor cursor);

}
