package ru.arturvasilov.sqlite.utils;

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
    public void testSingleRealColumn() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(test REAL);";

        TableBuilder.create(TestTable.TABLE)
                .realColumn("test")
                .execute(mDb);

        Mockito.verify(mDb).execSQL(sql);
    }

    @Test
    public void testSingleStringColumn() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(test TEXT);";

        TableBuilder.create(TestTable.TABLE)
                .textColumn("test")
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
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(int1 INTEGER, int2 INTEGER, " +
                "real1 REAL, string1 TEXT, string2 TEXT);";

        TableBuilder.create(TestTable.TABLE)
                .intColumn("int1")
                .textColumn("string1")
                .intColumn("int2")
                .realColumn("real1")
                .textColumn("string2")
                .execute(mDb);

        Mockito.verify(mDb).execSQL(sql);
    }

    @Test
    public void testManyColumnsWithPrimaryKey() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(int1 INTEGER, int2 INTEGER," +
                " real1 REAL, string1 TEXT, string2 TEXT, PRIMARY KEY (int1));";

        TableBuilder.create(TestTable.TABLE)
                .intColumn("int1")
                .textColumn("string1")
                .intColumn("int2")
                .realColumn("real1")
                .textColumn("string2")
                .primaryKey("int1")
                .execute(mDb);

        Mockito.verify(mDb).execSQL(sql);
    }

    @Test
    public void testManyColumnsWithMultiplePrimaryKey() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(int1 INTEGER, int2 INTEGER, " +
                "real1 REAL, real2 REAL, string1 TEXT, string2 TEXT, PRIMARY KEY (int1, real1, string2));";

        TableBuilder.create(TestTable.TABLE)
                .intColumn("int1")
                .realColumn("real1")
                .textColumn("string1")
                .intColumn("int2")
                .textColumn("string2")
                .realColumn("real2")
                .primaryKey("int1", "real1", "string2")
                .execute(mDb);

        Mockito.verify(mDb).execSQL(sql);
    }

    @Test
    public void testSameColumnsIgnored() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(testInt INTEGER, testReal REAL, testText TEXT);";

        TableBuilder.create(TestTable.TABLE)
                .intColumn("testInt")
                .intColumn("testInt")
                .realColumn("testReal")
                .realColumn("testReal")
                .textColumn("testText")
                .textColumn("testText")
                .execute(mDb);

        Mockito.verify(mDb).execSQL(sql);
    }

    @Test
    public void testSamePrimaryKeysIgnored() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS TestTable(test INTEGER, PRIMARY KEY (test));";

        TableBuilder.create(TestTable.TABLE)
                .intColumn("test")
                .primaryKey("test", "test")
                .execute(mDb);

        Mockito.verify(mDb).execSQL(sql);
    }
}
