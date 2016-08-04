package ru.arturvasilov.sqlite.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Artur Vasilov
 */
public class Where {

    private String mWhere;
    private String[] mArgs;

    private Where() {
    }

    @NonNull
    public static Where create() {
        return new Where();
    }

    @NonNull
    public Where where(@Nullable String where) {
        mWhere = where;
        return this;
    }

    @NonNull
    public Where whereArgs(@Nullable String[] args) {
        mArgs = args;
        return this;
    }

    @Nullable
    public String where() {
        return mWhere;
    }

    @Nullable
    public String[] whereArgs() {
        return mArgs;
    }

}
