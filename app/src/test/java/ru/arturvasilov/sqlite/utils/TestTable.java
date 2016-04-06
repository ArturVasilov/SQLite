package ru.arturvasilov.sqlite.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import ru.arturvasilov.sqlite.table.BaseTable;
import ru.arturvasilov.sqlite.table.Table;
import ru.arturvasilov.sqlite.table.TableBuilder;

/**
 * @author Artur Vasilov
 */
public class TestTable extends BaseTable<TestContentClass> {

    public static final Table<TestContentClass> TABLE = new TestTable();

    @Override
    public void onCreate(@NonNull SQLiteDatabase database) {
        TableBuilder.create(this)
                .intColumn(Columns.ID)
                .stringColumn(Columns.TEXT)
                .execute(database);
    }

    @Override
    public int getLastUpgradeVersion() {
        return 1;
    }

    @NonNull
    @Override
    public ContentValues toValues(@NonNull TestContentClass object) {
        ContentValues values = new ContentValues();
        values.put(Columns.ID, object.getId());
        values.put(Columns.TEXT, object.getText());
        return values;
    }

    @NonNull
    @Override
    public TestContentClass fromCursor(@NonNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(Columns.ID));
        String text = cursor.getString(cursor.getColumnIndex(Columns.TEXT));
        return new TestContentClass(id, text);
    }

    public interface Columns {
        String ID = "id";
        String TEXT = "text";
    }
}
