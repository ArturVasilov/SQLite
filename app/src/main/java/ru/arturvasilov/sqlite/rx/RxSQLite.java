package ru.arturvasilov.sqlite.rx;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.Callable;

import ru.arturvasilov.sqlite.core.BasicTableObserver;
import ru.arturvasilov.sqlite.core.SQLite;
import ru.arturvasilov.sqlite.core.Table;
import ru.arturvasilov.sqlite.core.Where;
import ru.arturvasilov.sqlite.utils.SQLiteUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.functions.Func1;

/**
 * @author Artur Vasilov
 */
public class RxSQLite {

    private static RxSQLite sSQLite;

    @NonNull
    public static RxSQLite get() {
        SQLiteUtils.assertInitialized();

        RxSQLite sqLite = sSQLite;
        if (sqLite == null) {
            synchronized (SQLite.class) {
                sqLite = sSQLite;
                if (sqLite == null) {
                    sqLite = sSQLite = new RxSQLite();
                }
            }
        }
        return sqLite;
    }

    @NonNull
    public <T> Observable<List<T>> query(@NonNull final Table<T> table, @NonNull final Where where) {
        return Observable.fromCallable(new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                return SQLite.get().query(table, where);
            }
        });
    }

    @NonNull
    public <T> Observable<T> queryObject(@NonNull final Table<T> table, @NonNull final Where where) {
        return Observable.fromCallable(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return SQLite.get().queryObject(table, where);
            }
        }).flatMap(new Func1<T, Observable<T>>() {
            @Override
            public Observable<T> call(@Nullable T t) {
                return t == null ? Observable.<T>empty() : Observable.just(t);
            }
        });
    }

    @NonNull
    public <T> Observable<Uri> insert(@NonNull final Table<T> table, @NonNull final T object) {
        return Observable.fromCallable(new Callable<Uri>() {
            @Override
            public Uri call() throws Exception {
                return SQLite.get().insert(table, object);
            }
        });
    }

    @NonNull
    public <T> Observable<Integer> insert(@NonNull final Table<T> table, @NonNull final List<T> objects) {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return SQLite.get().insert(table, objects);
            }
        });
    }

    @NonNull
    public <T> Observable<Integer> delete(@NonNull final Table<T> table, @NonNull final Where where) {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return SQLite.get().delete(table, where);
            }
        });
    }

    @NonNull
    public <T> Observable<Integer> update(@NonNull final Table<T> table, @NonNull final Where where,
                                          @NonNull final T newObject) {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return SQLite.get().update(table, where, newObject);
            }
        });
    }

    @NonNull
    public <T> TableObservable<T> observeChanges(@NonNull final Table<T> table) {
        Observable.OnSubscribe<Boolean> onSubscribe = new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                final BasicTableObserver observer = new BasicTableObserver() {
                    @Override
                    public void onTableChanged() {
                        if (subscriber != null && !subscriber.isUnsubscribed()) {
                            subscriber.onNext(true);
                        }
                    }
                };
                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        SQLite.get().unregisterObserver(observer);
                    }
                });

                SQLite.get().registerObserver(table, observer);
            }
        };
        return new TableObservable<>(onSubscribe, table);
    }
}
