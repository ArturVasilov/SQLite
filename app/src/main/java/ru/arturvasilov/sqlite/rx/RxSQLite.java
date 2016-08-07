package ru.arturvasilov.sqlite.rx;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.Callable;

import ru.arturvasilov.sqlite.core.BasicTableObserver;
import ru.arturvasilov.sqlite.core.SQLite;
import ru.arturvasilov.sqlite.core.Table;
import ru.arturvasilov.sqlite.core.Where;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.MainThreadSubscription;
import rx.functions.Func1;

/**
 * For the documentation please take a look at {@link SQLite},
 * Most of the methods in this class simply wraps {@link SQLite} methods in Observables.
 * <p/>
 * Note: this class doesn't handled doing operations in the background - it's up to the user
 * (since in most cases you won't simply subscribe for data, but transform it, so you'll have to write
 * subscribeOn and observeOn one more time.
 *
 * @author Artur Vasilov
 */
public class RxSQLite {

    private static RxSQLite sSQLite;

    /**
     * Before calling this method be sure that you've successfully initialized SQLite instance
     * (by {@link SQLite#initialize(Context)}), since all of the methods in this class are simple wrappers.
     *
     * @return singleton instance of RxSQLite
     */
    @NonNull
    public static RxSQLite get() {
        SQLite.get();

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

    /**
     * {@link SQLite#query(Table)}
     */
    @NonNull
    public <T> Observable<List<T>> query(@NonNull final Table<T> table) {
        return query(table, Where.create());
    }

    /**
     * {@link SQLite#query(Table, Where)}
     */
    @NonNull
    public <T> Observable<List<T>> query(@NonNull final Table<T> table, @NonNull final Where where) {
        return Observable.fromCallable(new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                return SQLite.get().query(table, where);
            }
        });
    }

    /**
     * {@link SQLite#querySingle(Table)}
     */
    @NonNull
    public <T> Observable<T> querySingle(@NonNull final Table<T> table) {
        return querySingle(table, Where.create());
    }

    /**
     * {@link SQLite#querySingle(Table, Where)}
     * <p/>
     * Observable is guarantee to contain no more than one element.
     * If you want to get observable of elements, simply call {@link RxSQLite#query(Table, Where)}
     * all apply {@link Observable#flatMap(Func1)} with {@link Observable#from(Iterable)} to it.
     */
    @NonNull
    public <T> Observable<T> querySingle(@NonNull final Table<T> table, @NonNull final Where where) {
        return Observable.fromCallable(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return SQLite.get().querySingle(table, where);
            }
        }).flatMap(new Func1<T, Observable<T>>() {
            @Override
            public Observable<T> call(@Nullable T t) {
                return t == null ? Observable.<T>empty() : Observable.just(t);
            }
        }).take(1);
    }

    /**
     * {@link SQLite#insert(Table, Object)}
     */
    @NonNull
    public <T> Observable<Uri> insert(@NonNull final Table<T> table, @NonNull final T object) {
        return Observable.fromCallable(new Callable<Uri>() {
            @Override
            public Uri call() throws Exception {
                return SQLite.get().insert(table, object);
            }
        });
    }

    /**
     * {@link SQLite#insert(Table, List)}
     */
    @NonNull
    public <T> Observable<Integer> insert(@NonNull final Table<T> table, @NonNull final List<T> objects) {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return SQLite.get().insert(table, objects);
            }
        });
    }

    /**
     * {@link SQLite#delete(Table)}
     */
    @NonNull
    public <T> Observable<Integer> delete(@NonNull final Table<T> table) {
        return delete(table, Where.create());
    }

    /**
     * {@link SQLite#delete(Table, Where)}
     */
    @NonNull
    public <T> Observable<Integer> delete(@NonNull final Table<T> table, @NonNull final Where where) {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return SQLite.get().delete(table, where);
            }
        });
    }

    /**
     * {@link SQLite#update(Table, Where, Object)}
     */
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

    /**
     * Returns observable that emits new items when passed table is changed.
     * For more information please take a look at {@link TableObservable}
     * <p/>
     * This observable never completes and there is no methods in RxSQLite to unsubscribe,
     * so you have to control subscription manually like this:
     * <p/>
     * <pre>
     * {code
     * private Subscription mPersonsSubscription;
     *
     * //...
     *
     * @Override
     * protected void onResume() {
     *  super.onResume();
     *  mPersonsSubscription = RxSQLite.get().observeChanges(PersonTable.TABLE)
     *      .subscribe(value -> {
     *          // table changed
     *      });
     *  }
     *
     * @Override
     * protected void onPause() {
     *  super.onPause();
     *  mPersonsSubscription.unsubscribe();
     * }
     * }
     * </pre>
     * {@link Subscription#unsubscribe()} will also detach ContentProvider observer
     */
    @NonNull
    public <T> TableObservable<T> observeChanges(@NonNull final Table<T> table) {
        Observable.OnSubscribe<Void> onSubscribe = new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                final BasicTableObserver observer = new BasicTableObserver() {
                    @Override
                    public void onTableChanged() {
                        if (subscriber != null && !subscriber.isUnsubscribed()) {
                            subscriber.onNext(null);
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
