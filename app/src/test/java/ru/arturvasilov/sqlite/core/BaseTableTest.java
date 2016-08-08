package ru.arturvasilov.sqlite.core;

import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import ru.arturvasilov.sqlite.testutils.TestTable;

import static org.mockito.Matchers.anyString;

/**
 * @author Artur Vasilov
 */
@RunWith(JUnit4.class)
public class BaseTableTest {

    @Test
    public void testTableCreated() throws Exception {
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        Mockito.doNothing().when(database).execSQL(anyString());

        TestTable.TABLE.onCreate(database);
        String createSql = "CREATE TABLE IF NOT EXISTS TestTable(id INTEGER, rating REAL, text TEXT, PRIMARY KEY (id));";
        Mockito.verify(database).execSQL(createSql);
    }

    @Test
    public void testTableDroppedAndRecreated() throws Exception {
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        Mockito.doNothing().when(database).execSQL(anyString());

        TestTable.TABLE.onUpgrade(database);
        Mockito.verify(database).execSQL("DROP TABLE IF EXISTS TestTable");
        String createSql = "CREATE TABLE IF NOT EXISTS TestTable(id INTEGER, rating REAL, text TEXT, PRIMARY KEY (id));";
        Mockito.verify(database).execSQL(createSql);
    }
}
