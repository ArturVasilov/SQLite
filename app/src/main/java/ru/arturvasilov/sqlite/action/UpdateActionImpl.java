package ru.arturvasilov.sqlite.action;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.arturvasilov.sqlite.table.Table;

/**
 * @author Artur Vasilov
 */
public class UpdateActionImpl<T> implements UpdateAction<T> {

    private final Table<T> mTable;
    private final Context mContext;

    private String mWhere;
    private String[] mArgs;
    private T mObject;

    public UpdateActionImpl(Table<T> table, Context context) {
        mTable = table;
        mContext = context;
    }

    @NonNull
    @Override
    public UpdateAction insert(@NonNull T object) {
        mObject = object;
        return this;
    }

    @NonNull
    @Override
    public UpdateAction where(@Nullable String where) {
        mWhere = where;
        return this;
    }

    @NonNull
    @Override
    public UpdateAction whereArgs(@Nullable String[] args) {
        mArgs = args;
        return this;
    }

    @Override
    public int execute() {
        return mContext.getContentResolver().update(mTable.getUri(), mTable.toValues(mObject), mWhere, mArgs);
    }
}
