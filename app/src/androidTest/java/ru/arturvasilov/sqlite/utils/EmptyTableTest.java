package ru.arturvasilov.sqlite.utils;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.sqlite.core.SQLite;
import ru.arturvasilov.sqlite.testutils.TestObject;
import ru.arturvasilov.sqlite.testutils.TestTable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Artur Vasilov
 */
@RunWith(AndroidJUnit4.class)
public class EmptyTableTest {

    @Before
    public void setUp() throws Exception {
        SQLite.initialize(InstrumentationRegistry.getContext());
        SQLite.get().disableAutomaticNotifications();
    }

    @Test
    public void testEmptyTable() throws Exception {
        SQLite.get().delete(TestTable.TABLE);
        boolean isEmpty = SQLiteUtils.isTableEmpty(TestTable.TABLE);
        assertTrue(isEmpty);
    }

    @Test
    public void testNotEmptyTable() throws Exception {
        SQLite.get().insert(TestTable.TABLE, new TestObject(1, 6.4, "test"));
        boolean isEmpty = SQLiteUtils.isTableEmpty(TestTable.TABLE);
        assertFalse(isEmpty);
    }

    @Test
    public void testTableChanges() throws Exception {
        SQLite.get().insert(TestTable.TABLE, new TestObject(7, 9.1, "testing empty table"));
        boolean isEmpty = SQLiteUtils.isTableEmpty(TestTable.TABLE);
        assertFalse(isEmpty);

        SQLite.get().delete(TestTable.TABLE);
        isEmpty = SQLiteUtils.isTableEmpty(TestTable.TABLE);
        assertTrue(isEmpty);

        List<TestObject> list = new ArrayList<>();
        list.add(new TestObject(8, 7.6, ""));
        list.add(new TestObject(9, 4, "any"));
        SQLite.get().insert(TestTable.TABLE, list);

        isEmpty = SQLiteUtils.isTableEmpty(TestTable.TABLE);
        assertFalse(isEmpty);

        SQLite.get().delete(TestTable.TABLE);
        isEmpty = SQLiteUtils.isTableEmpty(TestTable.TABLE);
        assertTrue(isEmpty);
    }

    @After
    public void tearDown() throws Exception {
        SQLite.get().delete(TestTable.TABLE);
    }
}
