package ru.arturvasilov.sqlite.query;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.arturvasilov.sqlite.table.Table;

/**
 * @author Artur Vasilov
 */
public class QueryImpl<T> implements Query<T> {

    private final Context mContext;
    private final Table<T> mTable;

    public QueryImpl(Context context, @NonNull Table<T> table) {
        mContext = context;
        mTable = table;
    }

    @Override
    public QueryList<T> all() {
        return new QueryListImpl<>(mContext, mTable);
    }

    @Override
    public QueryObject<T> object() {
        return new QueryObjectImpl<>(mContext, mTable);
    }
}
