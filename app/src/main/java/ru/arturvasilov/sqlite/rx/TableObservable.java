package ru.arturvasilov.sqlite.rx;

import android.support.annotation.NonNull;

import java.util.List;

import ru.arturvasilov.sqlite.core.Table;
import ru.arturvasilov.sqlite.core.Where;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Class which allows you to observe changes in {@link Table} in reactive way
 * <p/>
 * Simple call of {@link RxSQLite#observeChanges(Table)} will return you an instance of this observable,
 * and you can any operations with this observable.
 * <p/>
 * For this Observable {@link rx.Subscriber#onNext(Object)} called each time when table changes
 * and {@link Subscriber#onCompleted()} is never called.
 * <p/>
 * This observable add {@link TableObservable#withQuery()} method to allow you query all rows from the observed tabled.
 *
 * @author Artur Vasilov
 */
class TableObservable<T> extends Observable<Void> {

    private final Table<T> mTable;

    TableObservable(@NonNull OnSubscribe<Void> f, @NonNull Table<T> table) {
        super(f);
        mTable = table;
    }

    /**
     * This method transforms notifications observable to the observable with list of all objects in the table.
     *
     * It also works in the background.
     *
     * @return observable with all elements from the table
     */
    @NonNull
    public Observable<List<T>> withQuery() {
        return withQuery(Where.create());
    }

    /**
     * This method transforms notifications observable to the observable
     * with list of all objects in the table which satisfies where parameter
     *
     * It also works in the background.
     *
     * @param where - arguments to query table
     * @return observable with all elements from the table
     */
    @NonNull
    public Observable<List<T>> withQuery(@NonNull final Where where) {
        return flatMap(new Func1<Void, Observable<List<T>>>() {
            @Override
            public Observable<List<T>> call(Void value) {
                return RxSQLite.get().query(mTable, where);
            }
        })
                .subscribeOn(Schedulers.io());
    }
}
