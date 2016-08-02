package ru.arturvasilov.sqlite.table;

import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import ru.arturvasilov.sqlite.testutils.TestTable;

import static org.mockito.Matchers.anyString;

@RunWith(JUnit4.class)
public class TableBuilderTest {

    private SQLiteDatabase mDb;

    @Before
    public void setUp() throws Exception {
        mDb = Mockito.mock(SQLiteDatabase.class);
        Mockito.doNothing().when(mDb).execSQL(anyString());
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

        Mockito.verify(mDb).execSQL(sql);
    }

    @Test
    public void testSingleStringColumn() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(test TEXT);";

        TableBuilder.create(TestTable.TABLE)
                .stringColumn("test")
                .execute(mDb);

        Mockito.verify(mDb).execSQL(sql);
    }

    @Test
    public void testPrimaryKeyWithIntColumn() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(test INTEGER, PRIMARY KEY (test));";

        TableBuilder.create(TestTable.TABLE)
                .intColumn("test")
                .primaryKey("test")
                .execute(mDb);

        Mockito.verify(mDb).execSQL(sql);
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

        Mockito.verify(mDb).execSQL(sql);
    }

    @Test
    public void testManyColumnsWithPrimaryKey() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(int1 INTEGER, string1 TEXT, string2 TEXT, int2 INTEGER" +
                ", PRIMARY KEY (int1));";

        TableBuilder.create(TestTable.TABLE)
                .primaryKey("int1")
                .intColumn("int1")
                .stringColumn("string1")
                .intColumn("int2")
                .stringColumn("string2")
                .execute(mDb);

        Mockito.verify(mDb).execSQL(sql);
    }

    @Test
    public void testManyColumnsWithMultiplePrimaryKey() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(int1 INTEGER, string1 TEXT, string2 TEXT, int2 INTEGER" +
                ", PRIMARY KEY (int1, string2));";

        TableBuilder.create(TestTable.TABLE)
                .primaryKey("int1", "string2")
                .intColumn("int1")
                .stringColumn("string1")
                .intColumn("int2")
                .stringColumn("string2")
                .execute(mDb);

        Mockito.verify(mDb).execSQL(sql);
    }

}
