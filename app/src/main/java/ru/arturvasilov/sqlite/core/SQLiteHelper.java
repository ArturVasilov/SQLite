package ru.arturvasilov.sqlite.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import ru.arturvasilov.sqlite.table.Table;

/**
 * @author Artur Vasilov
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private final Schema mSchema;

    public SQLiteHelper(Context context, @NonNull SQLiteConfig config, @NonNull Schema schema) {
        super(context, config.getDatabaseName(), null, schema.calculateVersion());
        mSchema = schema;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Table table : mSchema) {
            table.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (Table table : mSchema) {
            table.onUpgrade(db, oldVersion, newVersion);
        }
    }
}
