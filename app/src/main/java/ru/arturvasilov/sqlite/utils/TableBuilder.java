package ru.arturvasilov.sqlite.utils;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.sqlite.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.sqlite.core.Table;

/**
 * @author Artur Vasilov
 */
public final class TableBuilder {

    private String mTableName;
    private final List<String> mPrimaryKeys;
    private final List<String> mIntegerColumns;
    private final List<String> mRealColumns;
    private final List<String> mTextColumns;

    private TableBuilder() {
        mPrimaryKeys = new ArrayList<>();
        mIntegerColumns = new ArrayList<>();
        mRealColumns = new ArrayList<>();
        mTextColumns = new ArrayList<>();
    }

    @NonNull
    public static TableBuilder create(@NonNull Table table) {
        TableBuilder builder = new TableBuilder();
        builder.mTableName = table.getTableName();
        return builder;
    }

    @NonNull
    public TableBuilder primaryKey(@NonNull String... keys) {
        mPrimaryKeys.clear();
        for (String key : keys) {
            if (!mPrimaryKeys.contains(key)) {
                mPrimaryKeys.add(key);
            }
        }
        return this;
    }

    @NonNull
    public TableBuilder intColumn(@NonNull String columnName) {
        if (!mIntegerColumns.contains(columnName)) {
            mIntegerColumns.add(columnName);
        }
        return this;
    }

    @NonNull
    public TableBuilder realColumn(@NonNull String columnName) {
        if (!mRealColumns.contains(columnName)) {
            mRealColumns.add(columnName);
        }
        return this;
    }

    @NonNull
    public TableBuilder textColumn(@NonNull String columnName) {
        if (!mTextColumns.contains(columnName)) {
            mTextColumns.add(columnName);
        }
        return this;
    }

    public void execute(@NonNull SQLiteDatabase database) {
        database.execSQL(buildSQL());
    }

    @VisibleForTesting
    String buildSQL() {
        if (mIntegerColumns.isEmpty() && mRealColumns.isEmpty() && mTextColumns.isEmpty()) {
            throw new IllegalStateException("Cannot create table with no columns");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ")
                .append(mTableName)
                .append("(");

        if (!mIntegerColumns.isEmpty()) {
            String column = mIntegerColumns.remove(0);
            builder.append(column)
                    .append(" INTEGER");
        } else if (!mRealColumns.isEmpty()) {
            String column = mRealColumns.remove(0);
            builder.append(column)
                    .append(" REAL");
        } else {
            String column = mTextColumns.remove(0);
            builder.append(column)
                    .append(" TEXT");
        }

        for (String column : mIntegerColumns) {
            builder.append(", ")
                    .append(column)
                    .append(" INTEGER");
        }

        for (String column : mRealColumns) {
            builder.append(", ")
                    .append(column)
                    .append(" REAL");
        }

        for (String column : mTextColumns) {
            builder.append(", ")
                    .append(column)
                    .append(" TEXT");
        }

        if (!mPrimaryKeys.isEmpty()) {
            builder.append(", PRIMARY KEY (")
                    .append(mPrimaryKeys.get(0));
        }
        for (int i = 1; i < mPrimaryKeys.size(); i++) {
            builder.append(", ").append(mPrimaryKeys.get(i));
        }
        if (!mPrimaryKeys.isEmpty()) {
            builder.append(")");
        }
        builder.append(");");

        return builder.toString();
    }

}
