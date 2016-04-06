package ru.arturvasilov.sqlite.table;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ru.arturvasilov.sqlite.utils.TestTable;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;

@RunWith(JUnit4.class)
public class TableBuilderTest {

    private SQLiteDatabase mDb;

    @Before
    public void setUp() throws Exception {
        mDb = mock(SQLiteDatabase.class);
        doNothing().when(mDb).execSQL(anyString());
    }

    @Test(expected = IllegalStateException.class)
    public void testEmptyColumnsList() throws Exception {
        TableBuilder.create(TestTable.TABLE).execute(mDb);
    }

    @Test
    public void testSingleIntegerColumn() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(test INTEGER);";

        TableBuilder.create(TestTable.TABLE)
                .intColumn("test")
                .execute(mDb);

        verify(mDb).execSQL(sql);
    }

    @Test
    public void testSingleStringColumn() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(test TEXT);";

        TableBuilder.create(TestTable.TABLE)
                .stringColumn("test")
                .execute(mDb);

        verify(mDb).execSQL(sql);
    }

    @Test
    public void testPrimaryKeyWithIntColumn() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(" + BaseColumns._ID + " INTEGER PRIMARY KEY, test INTEGER);";

        TableBuilder.create(TestTable.TABLE)
                .primaryKey()
                .intColumn("test")
                .execute(mDb);

        verify(mDb).execSQL(sql);
    }

    @Test
    public void testManyColumnsWithoutPrimaryKey() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(int1 INTEGER, string1 TEXT, string2 TEXT, int2 INTEGER);";

        TableBuilder.create(TestTable.TABLE)
                .intColumn("int1")
                .stringColumn("string1")
                .intColumn("int2")
                .stringColumn("string2")
                .execute(mDb);

        verify(mDb).execSQL(sql);
    }

    @Test
    public void testManyColumnsWithPrimaryKey() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(" + BaseColumns._ID +
                " INTEGER PRIMARY KEY, string1 TEXT, string2 TEXT, int1 INTEGER, int2 INTEGER);";

        TableBuilder.create(TestTable.TABLE)
                .primaryKey()
                .intColumn("int1")
                .stringColumn("string1")
                .intColumn("int2")
                .stringColumn("string2")
                .execute(mDb);

        verify(mDb).execSQL(sql);
    }

}
