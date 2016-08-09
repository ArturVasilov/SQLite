package ru.arturvasilov.sqlite.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.sqlite.database.sqlite.SQLiteDatabase;

/**
 * @author Artur Vasilov
 */
@RunWith(JUnit4.class)
public class ContentProviderErrorsTest {

    private static final Table<Integer> BAD_TABLE = new BadTable();

    @Before
    public void setUp() throws Exception {
        SQLite.initialize(InstrumentationRegistry.getContext());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadQuery() throws Exception {
        SQLite.get().query(BAD_TABLE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadInsert() throws Exception {
        SQLite.get().insert(BAD_TABLE, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadUpdate() throws Exception {
        SQLite.get().update(BAD_TABLE, Where.create(), 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadDelete() throws Exception {
        SQLite.get().delete(BAD_TABLE);
    }

    private static class BadTable extends BaseTable<Integer> {

        @Override
        public void onCreate(@NonNull SQLiteDatabase database) {
            throw new RuntimeException("Stub!");
        }

        @NonNull
        @Override
        public ContentValues toValues(@NonNull Integer integer) {
            return new ContentValues();
        }

        @NonNull
        @Override
        public Integer fromCursor(@NonNull Cursor cursor) {
            throw new RuntimeException("Stub!");
        }
    }
}
