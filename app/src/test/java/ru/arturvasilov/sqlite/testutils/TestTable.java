package ru.arturvasilov.sqlite.testutils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import ru.arturvasilov.sqlite.core.BaseTable;
import ru.arturvasilov.sqlite.core.Table;
import ru.arturvasilov.sqlite.utils.TableBuilder;

/**
 * @author Artur Vasilov
 */
public class TestTable extends BaseTable<TestObject> {

    public static final Table<TestObject> TABLE = new TestTable();

    public static final String ID = "id";
    public static final String TEXT = "text";

    @Override
    public void onCreate(@NonNull SQLiteDatabase database) {
        TableBuilder.create(this)
                .intColumn(ID)
                .textColumn(TEXT)
                .primaryKey(ID)
                .execute(database);
    }

    @NonNull
    @Override
    public ContentValues toValues(@NonNull TestObject testObject) {
        ContentValues values = new ContentValues();
        values.put(ID, testObject.getId());
        values.put(TEXT, testObject.getText());
        return values;
    }

    @NonNull
    @Override
    public TestObject fromCursor(@NonNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(ID));
        String text = cursor.getString(cursor.getColumnIndex(TEXT));
        return new TestObject(id, text);
    }

}
