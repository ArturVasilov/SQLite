package ru.arturvasilov.sqlite.action;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Artur Vasilov
 */
public interface DeleteAction<T> {

    @NonNull
    DeleteAction<T> where(@Nullable String where);

    @NonNull
    DeleteAction<T> whereArgs(@Nullable String[] args);

    int execute();

}
