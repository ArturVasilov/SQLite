package ru.arturvasilov.sqlite.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Configuration class for SQLite database
 *
 * @author Artur Vasilov
 */
public class SQLiteConfig {

    private static final String PREFS_NAME = "sqlite_config_prefs";

    private static final String DATABASE_NAME_KEY = "database_name";
    private static final String AUTHORITY_KEY = "authority";

    private static final String DEFAULT_DATABASE_NAME = "ru.sqlite.database.database";
    private static final String DEFAULT_AUTHORITY = "ru.sqlite.database";

    private final Context mContext;

    private String mDatabaseName;
    private String mAuthority;

    SQLiteConfig(Context context) {
        mContext = context;

        SharedPreferences prefs = getPrefs();
        mDatabaseName = prefs.getString(DATABASE_NAME_KEY, DEFAULT_DATABASE_NAME);
        mAuthority = prefs.getString(AUTHORITY_KEY, DEFAULT_AUTHORITY);
    }

    /**
     * @param databaseName - name of the app database
     */
    public void setDatabaseName(@NonNull String databaseName) {
        mDatabaseName = databaseName;
    }

    /**
     * @param uri - authority of the app database
     */
    public void setAuthority(@NonNull String uri) {
        mAuthority = uri;
    }

    @NonNull
    String getDatabaseName() {
        return mDatabaseName;
    }

    @NonNull
    String getAuthority() {
        return mAuthority;
    }

    @NonNull
    private SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
