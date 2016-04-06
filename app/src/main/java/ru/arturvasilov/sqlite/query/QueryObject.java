package ru.arturvasilov.sqlite.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;

/**
 * @author Artur Vasilov
 */
public interface QueryObject<T> {

    @NonNull
    QueryObject<T> where(@Nullable String query);

    @NonNull
    QueryObject<T> whereArgs(@Nullable String[] args);

    @Nullable
    T execute();

    @NonNull
    Observable<T> asObservable();

}
