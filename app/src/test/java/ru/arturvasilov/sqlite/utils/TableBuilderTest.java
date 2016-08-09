package ru.arturvasilov.sqlite.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ru.arturvasilov.sqlite.testutils.JUnitTestTable;

import static junit.framework.TestCase.assertEquals;

@RunWith(JUnit4.class)
public class TableBuilderTest {

    @Test(expected = IllegalStateException.class)
    public void testEmptyColumnsList() throws Exception {
        TableBuilder.create(JUnitTestTable.TABLE).buildSQL();
    }

    @Test
    public void testSingleIntegerColumn() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS JUnitTestTable(test INTEGER);";

        assertEquals(sql, TableBuilder.create(JUnitTestTable.TABLE)
                .intColumn("test")
                .buildSQL());
    }

    @Test
    public void testSingleRealColumn() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS JUnitTestTable(test REAL);";

        assertEquals(sql, TableBuilder.create(JUnitTestTable.TABLE)
                .realColumn("test")
                .buildSQL());
    }

    @Test
    public void testSingleStringColumn() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS JUnitTestTable(test TEXT);";

        assertEquals(sql, TableBuilder.create(JUnitTestTable.TABLE)
                .textColumn("test")
                .buildSQL());
    }

    @Test
    public void testPrimaryKeyWithIntColumn() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS JUnitTestTable(test INTEGER, PRIMARY KEY (test));";

        assertEquals(sql, TableBuilder.create(JUnitTestTable.TABLE)
                .intColumn("test")
                .primaryKey("test")
                .buildSQL());
    }

    @Test
    public void testManyColumnsWithoutPrimaryKey() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS JUnitTestTable(int1 INTEGER, int2 INTEGER, " +
                "real1 REAL, string1 TEXT, string2 TEXT);";

        assertEquals(sql, TableBuilder.create(JUnitTestTable.TABLE)
                .intColumn("int1")
                .textColumn("string1")
                .intColumn("int2")
                .realColumn("real1")
                .textColumn("string2")
                .buildSQL());
    }

    @Test
    public void testManyColumnsWithPrimaryKey() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS JUnitTestTable(int1 INTEGER, int2 INTEGER," +
                " real1 REAL, string1 TEXT, string2 TEXT, PRIMARY KEY (int1));";

        assertEquals(sql, TableBuilder.create(JUnitTestTable.TABLE)
                .intColumn("int1")
                .textColumn("string1")
                .intColumn("int2")
                .realColumn("real1")
                .textColumn("string2")
                .primaryKey("int1")
                .buildSQL());
    }

    @Test
    public void testManyColumnsWithMultiplePrimaryKey() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS JUnitTestTable(int1 INTEGER, int2 INTEGER, " +
                "real1 REAL, real2 REAL, string1 TEXT, string2 TEXT, PRIMARY KEY (int1, real1, string2));";

        assertEquals(sql, TableBuilder.create(JUnitTestTable.TABLE)
                .intColumn("int1")
                .realColumn("real1")
                .textColumn("string1")
                .intColumn("int2")
                .textColumn("string2")
                .realColumn("real2")
                .primaryKey("int1", "real1", "string2")
                .buildSQL());
    }

    @Test
    public void testSameColumnsIgnored() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS JUnitTestTable(testInt INTEGER, testReal REAL, testText TEXT);";

        assertEquals(sql, TableBuilder.create(JUnitTestTable.TABLE)
                .intColumn("testInt")
                .intColumn("testInt")
                .realColumn("testReal")
                .realColumn("testReal")
                .textColumn("testText")
                .textColumn("testText")
                .buildSQL());
    }

    @Test
    public void testSamePrimaryKeysIgnored() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS JUnitTestTable(test INTEGER, PRIMARY KEY (test));";

        assertEquals(sql, TableBuilder.create(JUnitTestTable.TABLE)
                .intColumn("test")
                .primaryKey("test", "test")
                .buildSQL());
    }
}
