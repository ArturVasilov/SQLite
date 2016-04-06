package ru.arturvasilov.sqlite.action;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Artur Vasilov
 */
public interface UpdateAction<T> {

    @NonNull
    UpdateAction insert(@NonNull T object);

    @NonNull
    UpdateAction where(@Nullable String where);

    @NonNull
    UpdateAction whereArgs(@Nullable String[] args);

    int execute();

}
