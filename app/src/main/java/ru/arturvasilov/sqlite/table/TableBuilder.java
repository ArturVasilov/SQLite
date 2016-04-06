package ru.arturvasilov.sqlite.table;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Artur Vasilov
 */
public final class TableBuilder {

    private String mTableName;
    private boolean mHasPrimaryKey;
    private final List<String> mIntegerColumns;
    private final List<String> mStringColumns;

    private TableBuilder() {
        mIntegerColumns = new ArrayList<>();
        mStringColumns = new ArrayList<>();
        mHasPrimaryKey = false;
    }

    @NonNull
    public static TableBuilder create(@NonNull Table table) {
        TableBuilder builder = new TableBuilder();
        builder.mTableName = table.getTableName();
        return builder;
    }

    @NonNull
    public TableBuilder primaryKey() {
        mHasPrimaryKey = true;
        return this;
    }

    @NonNull
    public TableBuilder intColumn(@NonNull String columnName) {
        mIntegerColumns.add(columnName);
        return this;
    }

    @NonNull
    public TableBuilder stringColumn(@NonNull String columnName) {
        mStringColumns.add(columnName);
        return this;
    }

    public void execute(@NonNull SQLiteDatabase database) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ")
                .append(mTableName)
                .append("(");

        if (mStringColumns.isEmpty() && mIntegerColumns.isEmpty()) {
            throw new IllegalStateException("No columns present");
        }
        if (mHasPrimaryKey) {
            builder.append(BaseColumns._ID)
                    .append(" INTEGER PRIMARY KEY");
        } else {
            if (mIntegerColumns.isEmpty()) {
                String column = mStringColumns.remove(0);
                builder.append(column)
                        .append(" TEXT");
            } else {
                String column = mIntegerColumns.remove(0);
                builder.append(column)
                        .append(" INTEGER");
            }
        }

        for (String column : mStringColumns) {
            builder.append(", ")
                    .append(column)
                    .append(" TEXT");
        }

        for (String column : mIntegerColumns) {
            builder.append(", ")
                    .append(column)
                    .append(" INTEGER");
        }

        builder.append(");");

        database.execSQL(builder.toString());
    }

}
