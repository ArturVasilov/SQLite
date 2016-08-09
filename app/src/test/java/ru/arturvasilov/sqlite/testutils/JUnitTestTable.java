package ru.arturvasilov.sqlite.testutils;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import org.sqlite.database.sqlite.SQLiteDatabase;

import ru.arturvasilov.sqlite.core.BaseTable;
import ru.arturvasilov.sqlite.core.Table;
import ru.arturvasilov.sqlite.utils.TableBuilder;

/**
 * @author Artur Vasilov
 */
public class JUnitTestTable extends BaseTable<JUnitTestObject> {

    public static final Table<JUnitTestObject> TABLE = new JUnitTestTable();

    public static final String ID = "id";
    public static final String RATING = "rating";
    public static final String TEXT = "text";

    @Override
    public void onCreate(@NonNull SQLiteDatabase database) {
        TableBuilder.create(this)
                .intColumn(ID)
                .realColumn(RATING)
                .textColumn(TEXT)
                .primaryKey(ID)
                .execute(database);
    }

    @NonNull
    @Override
    public ContentValues toValues(@NonNull JUnitTestObject testObject) {
        ContentValues values = new ContentValues();
        values.put(ID, testObject.getId());
        values.put(RATING, testObject.getRating());
        values.put(TEXT, testObject.getText());
        return values;
    }

    @NonNull
    @Override
    public JUnitTestObject fromCursor(@NonNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(ID));
        double rating = cursor.getDouble(cursor.getColumnIndex(RATING));
        String text = cursor.getString(cursor.getColumnIndex(TEXT));
        return new JUnitTestObject(id, rating, text);
    }

}
