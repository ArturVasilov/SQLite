package ru.arturvasilov.sqlite.rx;

import android.support.annotation.NonNull;

import java.util.List;

import ru.arturvasilov.sqlite.core.Table;
import ru.arturvasilov.sqlite.core.Where;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Artur Vasilov
 */
public class TableObservable<T> extends Observable<Boolean> {

    private final Table<T> mTable;

    public TableObservable(@NonNull OnSubscribe<Boolean> f, @NonNull Table<T> table) {
        super(f);
        mTable = table;
    }

    @NonNull
    public Observable<List<T>> withQuery() {
        return flatMap(new Func1<Boolean, Observable<List<T>>>() {
            @Override
            public Observable<List<T>> call(Boolean value) {
                return RxSQLite.get().query(mTable, Where.create());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }
}
