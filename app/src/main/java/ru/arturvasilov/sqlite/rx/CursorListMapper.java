package ru.arturvasilov.sqlite.rx;

import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.sqlite.SQLiteUtils;
import rx.functions.Func1;

/**
 * @author Artur Vasilov
 */
public class CursorListMapper<T> implements Func1<Cursor, List<T>> {

    private final Func1<Cursor, T> mTransformFunc;

    public CursorListMapper(@NonNull Func1<Cursor, T> transformFunc) {
        mTransformFunc = transformFunc;
    }

    @Override
    public List<T> call(Cursor cursor) {
        List<T> list = new ArrayList<>();
        if (SQLiteUtils.isEmptyCursor(cursor)) {
            return list;
        }
        do {
            list.add(mTransformFunc.call(cursor));
        } while (cursor.moveToNext());
        SQLiteUtils.safeCloseCursor(cursor);
        return list;
    }
}
