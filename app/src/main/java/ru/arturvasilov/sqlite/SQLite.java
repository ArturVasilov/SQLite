package ru.arturvasilov.sqlite;

import android.content.Context;
import android.database.ContentObserver;
import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.sqlite.action.DeleteAction;
import ru.arturvasilov.sqlite.action.DeleteActionImpl;
import ru.arturvasilov.sqlite.action.InsertAction;
import ru.arturvasilov.sqlite.action.InsertActionImpl;
import ru.arturvasilov.sqlite.action.UpdateAction;
import ru.arturvasilov.sqlite.action.UpdateActionImpl;
import ru.arturvasilov.sqlite.observers.DatabaseObserver;
import ru.arturvasilov.sqlite.observers.TableObserver;
import ru.arturvasilov.sqlite.query.Query;
import ru.arturvasilov.sqlite.query.QueryImpl;
import ru.arturvasilov.sqlite.table.Table;

/**
 * @author Artur Vasilov
 */
public class SQLite {

    private final Context mContext;

    private static SQLite sSQLite;

    private final List<Pair<TableObserver, ContentObserver>> mObservers = new ArrayList<>();

    private SQLite(Context context) {
        mContext = context;
    }

    @NonNull
    public static SQLite initialize(Context context) {
        SQLite sqLite = sSQLite;
        if (sqLite == null) {
            synchronized (SQLite.class) {
                sqLite = sSQLite;
                if (sqLite == null) {
                    sqLite = sSQLite = new SQLite(context);
                }
            }
        }
        return sqLite;
    }

    @NonNull
    public static SQLite get() {
        if (sSQLite == null) {
            throw new IllegalStateException("You should call initialize(Context) first");
        }
        return sSQLite;
    }

    @NonNull
    public <T> Query<T> query(@NonNull Table<T> table) {
        return new QueryImpl<>(mContext, table);
    }

    @NonNull
    public <T> InsertAction<T> insert(@NonNull Table<T> table) {
        return new InsertActionImpl<>(table, mContext);
    }

    @NonNull
    public <T> DeleteAction<T> delete(@NonNull Table<T> table) {
        return new DeleteActionImpl<>(mContext, table);
    }

    @NonNull
    public <T> UpdateAction<T> update(@NonNull Table<T> table) {
        return new UpdateActionImpl<>(table, mContext);
    }

    public <T> void registerObserver(@NonNull Table<T> table, @NonNull final TableObserver observer) {
        ContentObserver contentObserver = new DatabaseObserver() {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                observer.onTableChanged();
            }
        };
        mContext.getContentResolver().registerContentObserver(table.getUri(), false, contentObserver);
        mObservers.add(new Pair<>(observer, contentObserver));
        observer.onTableChanged();
    }

    public void unregisterObserver(@NonNull TableObserver observer) {
        int index = -1;
        for (int i = 0; i < mObservers.size(); i++) {
            if (mObservers.get(i).first == observer) {
                index = i;
            }
        }

        if (index >= 0) {
            Pair<TableObserver, ContentObserver> observerPair = mObservers.get(index);
            mContext.getContentResolver().unregisterContentObserver(observerPair.second);
            mObservers.remove(index);
        }
    }

    public <T> void notifyTableChanged(@NonNull Table<T> table) {
        mContext.getContentResolver().notifyChange(table.getUri(), null);
    }
}
