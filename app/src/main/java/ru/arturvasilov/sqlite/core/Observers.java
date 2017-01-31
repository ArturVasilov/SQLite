package ru.arturvasilov.sqlite.core;

import android.content.Context;
import android.database.ContentObserver;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.arturvasilov.sqlite.rx.RxSQLite;

/**
 * @author Artur Vasilov
 */
final class Observers {

    private final List<Pair<Object, ContentObserver>> mObservers = new ArrayList<>();

    public <T> void registerObserver(@NonNull Context context, @NonNull Table<T> table,
                                     @NonNull final BasicTableObserver observer) {
        ContentObserver contentObserver = new DatabaseObserver() {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                MainHandler.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        observer.onTableChanged();
                    }
                });
            }
        };
        context.getContentResolver().registerContentObserver(table.getUri(), false, contentObserver);
        mObservers.add(new Pair<Object, ContentObserver>(observer, contentObserver));
    }

    public <T> void registerObserver(@NonNull Context context, @NonNull final Table<T> table,
                                     @NonNull final ContentTableObserver<T> observer, @NonNull final Where where) {
        ContentObserver contentObserver = new DatabaseObserver() {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                RxSQLite.get().query(table, where)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<T>>() {
                            @Override
                            public void accept(List<T> list) throws Exception {
                                observer.onTableChanged(list);
                            }
                        });
            }
        };
        context.getContentResolver().registerContentObserver(table.getUri(), false, contentObserver);
        mObservers.add(new Pair<Object, ContentObserver>(observer, contentObserver));
    }

    public void unregisterObserver(@NonNull Context context, @NonNull BasicTableObserver observer) {
        unregisterObserver(context, (Object) observer);
    }

    public <T> void unregisterObserver(@NonNull Context context, @NonNull ContentTableObserver<T> observer) {
        unregisterObserver(context, (Object) observer);
    }

    private void unregisterObserver(@NonNull Context context, @NonNull Object object) {
        int index = -1;
        for (int i = 0; i < mObservers.size(); i++) {
            if (mObservers.get(i).first == object) {
                index = i;
            }
        }

        if (index >= 0) {
            context.getContentResolver().unregisterContentObserver(mObservers.get(index).second);
            mObservers.remove(index);
        }
    }

}
