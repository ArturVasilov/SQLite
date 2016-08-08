package ru.arturvasilov.sqlite.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

/**
 * @author Artur Vasilov
 */
class SQLiteHelper extends SQLiteOpenHelper {

    private final SQLiteSchema mSchema;

    public SQLiteHelper(Context context, @NonNull SQLiteConfig config, @NonNull SQLiteSchema schema) {
        super(context, config.getDatabaseName(), null, schema.calculateVersion());
        mSchema = schema;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        for (Table table : mSchema) {
            table.onCreate(database);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        for (Table table : mSchema) {
            if (oldVersion < newVersion && newVersion <= table.getLastUpgradeVersion()) {
                table.onUpgrade(database);
            }
        }
    }
}
