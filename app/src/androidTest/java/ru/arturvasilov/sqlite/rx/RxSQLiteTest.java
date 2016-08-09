package ru.arturvasilov.sqlite.rx;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.sqlite.core.SQLite;
import ru.arturvasilov.sqlite.core.Where;
import ru.arturvasilov.sqlite.testutils.RxUtils;
import ru.arturvasilov.sqlite.testutils.TestObject;
import ru.arturvasilov.sqlite.testutils.TestTable;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;

/**
 * @author Artur Vasilov
 */
@RunWith(AndroidJUnit4.class)
public class RxSQLiteTest {

    @Before
    public void setUp() throws Exception {
        SQLite.initialize(InstrumentationRegistry.getContext());
        RxUtils.setupTestSchedulers();
    }

    @Test
    public void testSingleElement() throws Exception {
        TestObject testElement = new TestObject(10, 10, "abc");
        SQLite.get().insert(TestTable.TABLE, testElement);

        TestObject savedElement = RxSQLite.get()
                .querySingle(TestTable.TABLE)
                .toBlocking()
                .first();

        Assert.assertEquals(testElement, savedElement);
    }

    @Test
    public void testElementsList() throws Exception {
        List<TestObject> elements = new ArrayList<>();
        elements.add(new TestObject(1, 9.5, "a"));
        elements.add(new TestObject(2, 6.7, "ab"));
        elements.add(new TestObject(3, 8.2, "abc"));
        elements.add(new TestObject(4, 3.4, "abcd"));
        elements.add(new TestObject(5, 6.5, "abcde"));
        SQLite.get().insert(TestTable.TABLE, elements);

        Observable.zip(RxSQLite.get().query(TestTable.TABLE),
                Observable.just(elements), new Func2<List<TestObject>, List<TestObject>, Void>() {
                    @Override
                    public Void call(List<TestObject> testElements, List<TestObject> savedElements) {
                        assertEquals(testElements.size(), savedElements.size());
                        for (int i = 0; i < testElements.size(); i++) {
                            assertEquals(testElements.size(), savedElements.size());
                        }
                        return null;
                    }
                })
                .toBlocking()
                .first();
    }

    @Test
    public void testQueryWithParameters() throws Exception {
        List<TestObject> elements = new ArrayList<>();
        elements.add(new TestObject(1, 9.5, "a"));
        elements.add(new TestObject(2, 6.7, "ab"));
        elements.add(new TestObject(3, 8.2, "abc"));
        SQLite.get().insert(TestTable.TABLE, elements);

        int count = RxSQLite.get().query(TestTable.TABLE, Where.create().lessThanOrEqualTo(TestTable.ID, 2))
                .toBlocking()
                .first()
                .size();

        assertEquals(2, count);
    }

    @Test
    public void testEmptyTable() throws Exception {
        RxSQLite.get().querySingle(TestTable.TABLE)
                .subscribe(new Action1<TestObject>() {
                    @Override
                    public void call(TestObject testObject) {
                        fail();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        fail();
                    }
                });
    }

    @Test
    public void testInsertElement() throws Exception {
        assertNotNull(RxSQLite.get().insert(TestTable.TABLE, new TestObject(100, 7, "100"))
                .toBlocking().first());
    }

    @Test
    public void testInsertList() throws Exception {
        List<TestObject> elements = new ArrayList<>();
        elements.add(new TestObject(1, 9.5, "a"));
        elements.add(new TestObject(2, 6.7, "ab"));
        int count = RxSQLite.get().insert(TestTable.TABLE, elements)
                .toBlocking().first();
        assertEquals(2, count);
    }

    @Test
    public void testUpdateElement() throws Exception {
        List<TestObject> elements = new ArrayList<>();
        elements.add(new TestObject(1, 9.5, "a"));
        elements.add(new TestObject(2, 6.7, "ab"));
        SQLite.get().insert(TestTable.TABLE, elements);

        int count = RxSQLite.get().update(TestTable.TABLE, Where.create().equalTo(TestTable.ID, 2), new TestObject(2, 6.7, "abc"))
                .toBlocking().first();
        assertEquals(1, count);

        RxSQLite.get().querySingle(TestTable.TABLE, Where.create().equalTo(TestTable.ID, 2))
                .subscribe(new Action1<TestObject>() {
                    @Override
                    public void call(TestObject testObject) {
                        Assert.assertEquals("abc", testObject.getText());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        fail();
                    }
                });
    }

    @Test
    public void testDeleteElement() throws Exception {
        List<TestObject> elements = new ArrayList<>();
        elements.add(new TestObject(1, 9.5, "a"));
        elements.add(new TestObject(2, 6.7, "ab"));
        SQLite.get().insert(TestTable.TABLE, elements);

        int count = RxSQLite.get().delete(TestTable.TABLE, Where.create().equalTo(TestTable.ID, 2))
                .toBlocking().first();
        assertEquals(1, count);
    }

    @Test
    public void testClearTable() throws Exception {
        List<TestObject> elements = new ArrayList<>();
        elements.add(new TestObject(1, 9.5, "a"));
        elements.add(new TestObject(2, 6.7, "ab"));
        elements.add(new TestObject(3, 8.2, "abc"));
        SQLite.get().insert(TestTable.TABLE, elements);

        int count = RxSQLite.get().delete(TestTable.TABLE).toBlocking().first();
        assertEquals(3, count);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testObserveTableChange() throws Exception {
        Action1<Void> action = Mockito.mock(Action1.class);
        Mockito.doNothing().when(action).call(any(Void.class));
        Subscription subscription = RxSQLite.get().observeChanges(TestTable.TABLE).subscribe(action);

        SQLite.get().insert(TestTable.TABLE, new TestObject(10010, 6.4, "changes"));
        Thread.sleep(1500);
        Mockito.verify(action).call(null);

        Mockito.reset(action);
        subscription.unsubscribe();

        SQLite.get().delete(TestTable.TABLE);
        Thread.sleep(1500);
        Mockito.verifyNoMoreInteractions(action);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testObserveTableChangeWithData() throws Exception {
        Action1<List<TestObject>> action = Mockito.mock(Action1.class);
        Mockito.doNothing().when(action).call(anyListOf(TestObject.class));
        Subscription subscription = RxSQLite.get().observeChanges(TestTable.TABLE).withQuery().subscribe(action);

        SQLite.get().insert(TestTable.TABLE, new TestObject(10410, 8.9, "ca'pcj;s;vhjvksf;bgd"));
        Thread.sleep(1500);
        Mockito.verify(action).call(anyListOf(TestObject.class));

        Mockito.reset(action);
        subscription.unsubscribe();

        SQLite.get().delete(TestTable.TABLE);
        Thread.sleep(1500);
        Mockito.verifyNoMoreInteractions(action);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testObserveTableChangeWithDataAndQuery() throws Exception {
        Action1<List<TestObject>> action = Mockito.mock(Action1.class);
        Mockito.doNothing().when(action).call(anyListOf(TestObject.class));
        RxSQLite.get().observeChanges(TestTable.TABLE)
                .withQuery(Where.create().lessThan(TestTable.RATING, 5))
                .subscribe(action);

        List<TestObject> list = new ArrayList<>();
        list.add(new TestObject(513, 1.6, "text44"));
        list.add(new TestObject(8, 7.6, "text2"));
        list.add(new TestObject(9, 4, "tex7"));
        SQLite.get().insert(TestTable.TABLE, list);
        Thread.sleep(1500);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(action).call(captor.capture());
        assertEquals(2, captor.getValue().size());
    }

    @After
    public void tearDown() throws Exception {
        SQLite.get().delete(TestTable.TABLE);
    }
}
