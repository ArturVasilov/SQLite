package ru.arturvasilov.sqlite.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import ru.arturvasilov.sqlite.SQLiteUtils;

/**
 * @author Artur Vasilov
 */
public class SQLiteConfig {

    private static final String PREFS_NAME = "sqlite_config_prefs";

    private static final String DATABASE_NAME_KEY = "database_name";
    private static final String AUTHORITY_KEY = "authority";

    private final Context mContext;

    private String mDatabaseName;
    private String mAuthority;

    public SQLiteConfig(Context context) {
        mContext = context;

        SharedPreferences prefs = getPrefs();
        mDatabaseName = prefs.getString(DATABASE_NAME_KEY, SQLiteUtils.defaultDatabaseName());
        mAuthority = prefs.getString(AUTHORITY_KEY, SQLiteUtils.defaultUri());
    }

    @NonNull
    public String getDatabaseName() {
        return mDatabaseName;
    }

    public void setDatabaseName(@NonNull String databaseName) {
        mDatabaseName = databaseName;
    }

    @NonNull
    public String getAuthority() {
        return mAuthority;
    }

    public void setAuthority(@NonNull String uri) {
        mAuthority = uri;
    }

    @NonNull
    private SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
