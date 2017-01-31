package ru.arturvasilov.sqlite.rx;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import ru.arturvasilov.sqlite.core.BasicTableObserver;
import ru.arturvasilov.sqlite.core.SQLite;
import ru.arturvasilov.sqlite.core.Table;
import ru.arturvasilov.sqlite.core.Where;

/**
 * Class which allows you to observe changes in {@link Table} in reactive way
 * <p/>
 * Simple call of {@link RxSQLite#observeChanges(Table)} will return you an instance of this observable,
 * and you can any operations with this observable.
 * <p/>
 * For this Observable {@link Observer#onNext(Object)} called each time when table changes
 * and {@link Observer#onComplete()} is never called.
 * <p/>
 * This observable add {@link TableObservable#withQuery()} method to allow you query all rows from the observed tabled.
 *
 * @author Artur Vasilov
 */
public class TableObservable<T> extends Observable<Boolean> {

    private final Table<T> mTable;

    TableObservable(@NonNull Table<T> table) {
        mTable = table;
    }

    @Override
    protected void subscribeActual(Observer<? super Boolean> observer) {
        TableListener tableListener = new TableListener(observer);
        observer.onSubscribe(tableListener);
        SQLite.get().registerObserver(mTable, tableListener);
    }

    /**
     * This method transforms notifications observable to the observable with list of all objects in the table.
     * <p>
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
     * <p>
     * It also works in the background.
     *
     * @param where - arguments to query table
     * @return observable with all elements from the table
     */
    @NonNull
    public Observable<List<T>> withQuery(@NonNull final Where where) {
        return flatMap(new Function<Boolean, ObservableSource<List<T>>>() {
            @Override
            public ObservableSource<List<T>> apply(Boolean value) throws Exception {
                return RxSQLite.get().query(mTable, where);
            }
        }).subscribeOn(Schedulers.io());
    }

    private class TableListener extends MainThreadDisposable implements BasicTableObserver {

        private final Observer<? super Boolean> mObserver;

        TableListener(@NonNull Observer<? super Boolean> observer) {
            mObserver = observer;
        }

        @Override
        public void onTableChanged() {
            mObserver.onNext(true);
        }

        @Override
        protected void onDispose() {
            SQLite.get().unregisterObserver(this);
        }
    }

}
