package ru.arturvasilov.sqlite.core;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.sqlite.testutils.TestObject;
import ru.arturvasilov.sqlite.testutils.TestTable;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.times;

@RunWith(AndroidJUnit4.class)
public class SQLiteTest {

    @Before
    public void setUp() throws Exception {
        SQLite.initialize(InstrumentationRegistry.getContext());
    }

    @Test
    public void testInsertElement() throws Exception {
        TestObject test = new TestObject(5, 4.8, "aaaa");
        SQLite.get().insert(TestTable.TABLE, test);
        TestObject saved = SQLite.get().querySingle(TestTable.TABLE);
        assertEquals(test, saved);
    }

    @Test
    public void testInsertMultiple() throws Exception {
        List<TestObject> elements = new ArrayList<>();
        elements.add(new TestObject(1, 9.5, "a"));
        elements.add(new TestObject(2, 6.7, "ab"));
        elements.add(new TestObject(3, 8.2, "abc"));
        elements.add(new TestObject(4, 3.4, "abcd"));
        elements.add(new TestObject(5, 6.5, "abcde"));
        SQLite.get().insert(TestTable.TABLE, elements);

        int savedSize = SQLite.get().query(TestTable.TABLE).size();
        assertEquals(elements.size(), savedSize);
    }

    @Test
    public void testInsertReplacedPrimaryKey() throws Exception {
        List<TestObject> elements = new ArrayList<>();
        elements.add(new TestObject(11, 9.9, "a"));
        elements.add(new TestObject(12, 8.9, "ab"));
        SQLite.get().insert(TestTable.TABLE, elements);

        SQLite.get().insert(TestTable.TABLE, new TestObject(12, 5.7, "bc"));
        List<TestObject> savedElements = SQLite.get().query(TestTable.TABLE);
        assertEquals(elements.size(), savedElements.size());
        assertEquals("bc", savedElements.get(1).getText());
    }

    @Test
    public void testQueryWithSingleObject() throws Exception {
        List<TestObject> elements = new ArrayList<>();
        elements.add(new TestObject(11, 1.2, "a"));
        elements.add(new TestObject(12, 2.7, "ab"));
        SQLite.get().insert(TestTable.TABLE, elements);

        TestObject element = SQLite.get().querySingle(TestTable.TABLE, Where.create().equalTo(TestTable.ID, 12));
        assertEquals(elements.get(1), element);
    }

    @Test
    public void testQueryList() throws Exception {
        List<TestObject> elements = new ArrayList<>();
        elements.add(new TestObject(110, 0, "a"));
        elements.add(new TestObject(111, 7.8, "ab"));
        elements.add(new TestObject(112, 9.6, "xaxka"));
        elements.add(new TestObject(113, 5.7, "ab"));
        elements.add(new TestObject(114, 6.8, "abc"));
        SQLite.get().insert(TestTable.TABLE, elements);

        List<TestObject> savedElements = SQLite.get().query(TestTable.TABLE, Where.create().equalTo(TestTable.TEXT, "ab"));

        assertEquals(2, savedElements.size());
        assertEquals(elements.get(1), savedElements.get(0));
        assertEquals(elements.get(3), savedElements.get(1));
    }

    @Test
    public void testEmptyElement() throws Exception {
        TestObject element = SQLite.get().querySingle(TestTable.TABLE);
        assertNull(element);
    }

    @Test
    public void testEmptyList() throws Exception {
        List<TestObject> elements = SQLite.get().query(TestTable.TABLE);
        assertTrue(elements.isEmpty());
    }

    @Test
    public void testUpdateRow() throws Exception {
        TestObject element = new TestObject(123321, 8.4, "abc");
        SQLite.get().insert(TestTable.TABLE, element);

        TestObject update = new TestObject(123321, 0.6, "xyz");
        int count = SQLite.get().update(TestTable.TABLE, Where.create().equalTo(TestTable.ID, 123321), update);
        assertEquals(1, count);

        TestObject updated = SQLite.get().querySingle(TestTable.TABLE);
        assertEquals(update, updated);
    }

    @Test
    public void testNoUpdate() throws Exception {
        TestObject element = new TestObject(123321, 6, "abc");
        SQLite.get().insert(TestTable.TABLE, element);

        TestObject update = new TestObject(123322, 5.2, "xyz");
        int count = SQLite.get().update(TestTable.TABLE, Where.create().equalTo(TestTable.ID, 1233212), update);
        assertEquals(0, count);

        TestObject notUpdated = SQLite.get().querySingle(TestTable.TABLE);
        assertEquals(element, notUpdated);
    }

    @Test
    public void testDeleteAll() throws Exception {
        List<TestObject> elements = new ArrayList<>();
        elements.add(new TestObject(11, 4, "a"));
        elements.add(new TestObject(12, 8.4, "ab"));
        SQLite.get().insert(TestTable.TABLE, elements);

        int count = SQLite.get().delete(TestTable.TABLE);
        assertEquals(2, count);

        elements = SQLite.get().query(TestTable.TABLE);
        assertTrue(elements.isEmpty());
    }

    @Test
    public void testDeleteByParameters() throws Exception {
        List<TestObject> elements = new ArrayList<>();
        elements.add(new TestObject(110, 7.1, "a"));
        elements.add(new TestObject(111, 8, "ab"));
        elements.add(new TestObject(112, 9, "xaxka"));
        elements.add(new TestObject(113, 6.9, "ab"));
        elements.add(new TestObject(114, 2.3, "abc"));
        SQLite.get().insert(TestTable.TABLE, elements);

        int count = SQLite.get().delete(TestTable.TABLE, Where.create().equalTo(TestTable.TEXT, "ab"));
        assertEquals(2, count);

        List<TestObject> leftElements = SQLite.get().query(TestTable.TABLE);

        assertEquals(3, leftElements.size());
        assertEquals(elements.get(0), leftElements.get(0));
        assertEquals(elements.get(2), leftElements.get(1));
        assertEquals(elements.get(4), leftElements.get(2));
    }

    @Test
    public void testSQLite() throws Exception {
        TestObject test = new TestObject(1, 9.3, "aaaa");
        SQLite.get().insert(TestTable.TABLE, test);

        List<TestObject> all = SQLite.get().query(TestTable.TABLE);
        assertEquals(1, all.size());
        assertTrue(test.equals(all.get(0)));

        test.setText("BBBBB");
        int rows = SQLite.get().update(TestTable.TABLE, Where.create().equalTo(TestTable.ID, 1), test);

        all = SQLite.get().query(TestTable.TABLE);
        assertEquals(1, rows);
        assertEquals(1, all.size());
        assertTrue(test.equals(all.get(0)));

        SQLite.get().delete(TestTable.TABLE);

        assertTrue(SQLite.get().query(TestTable.TABLE).isEmpty());
    }

    @Test
    public void testObserveTableChange() throws Exception {
        BasicTableObserver observer = Mockito.mock(BasicTableObserver.class);
        Mockito.doNothing().when(observer).onTableChanged();
        SQLite.get().registerObserver(TestTable.TABLE, observer);

        SQLite.get().insert(TestTable.TABLE, new TestObject(5, 6, "text"));
        SQLite.get().delete(TestTable.TABLE);

        Mockito.verify(observer, times(2)).onTableChanged();

        Mockito.reset(observer);
        SQLite.get().unregisterObserver(observer);
        SQLite.get().insert(TestTable.TABLE, new TestObject(5, 6, "text"));
        Mockito.verifyNoMoreInteractions(observer);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testObserveTableChangeWithData() throws Exception {
        ContentTableObserver<TestObject> observer = Mockito.mock(ContentTableObserver.class);
        Mockito.doNothing().when(observer).onTableChanged(anyListOf(TestObject.class));
        SQLite.get().registerObserver(TestTable.TABLE, observer);

        SQLite.get().insert(TestTable.TABLE, new TestObject(5, 9.7, "text"));
        Thread.sleep(100); //data will be queried asynchronously
        Mockito.verify(observer).onTableChanged(anyListOf(TestObject.class));

        Mockito.reset(observer);
        SQLite.get().unregisterObserver(observer);
        SQLite.get().delete(TestTable.TABLE);
        Mockito.verifyNoMoreInteractions(observer);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testObserveTableChangeWithDataAndQuery() throws Exception {
        ContentTableObserver<TestObject> observer = Mockito.mock(ContentTableObserver.class);
        Mockito.doNothing().when(observer).onTableChanged(anyListOf(TestObject.class));
        SQLite.get().registerObserver(TestTable.TABLE, observer, Where.create().equalTo(TestTable.ID, 5));

        List<TestObject> list = new ArrayList<>();
        list.add(new TestObject(5, 9.7, "text"));
        list.add(new TestObject(6, 8, "text2"));
        SQLite.get().insert(TestTable.TABLE, list);
        Thread.sleep(100); //data will be queried asynchronously

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(observer).onTableChanged(captor.capture());
        assertEquals(1, captor.getValue().size());
    }

    @After
    public void tearDown() throws Exception {
        SQLite.get().delete(TestTable.TABLE);
    }
}