package ru.arturvasilov.sqlite.rx;

import android.app.Application;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.sqlite.BuildConfig;
import ru.arturvasilov.sqlite.core.SQLite;
import ru.arturvasilov.sqlite.core.Table;
import ru.arturvasilov.sqlite.core.Where;
import ru.arturvasilov.sqlite.testutils.RxUtils;
import ru.arturvasilov.sqlite.testutils.SQLiteEnv;
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
@Config(constants = BuildConfig.class, sdk = 21)
@RunWith(RobolectricTestRunner.class)
public class RxSQLiteTest {

    @Before
    public void setUp() throws Exception {
        List<Table> tables = new ArrayList<Table>() {{
            add(TestTable.TABLE);
        }};
        SQLiteEnv.registerProvider(tables);

        Application application = RuntimeEnvironment.application;
        application.onCreate();
        SQLite.initialize(application);
    }

    @Test
    public void testSingleElement() throws Exception {
        TestObject testElement = new TestObject(10, 10, "abc");
        SQLite.get().insert(TestTable.TABLE, testElement);

        TestObject savedElement = RxSQLite.get()
                .querySingle(TestTable.TABLE)
                .toBlocking()
                .first();

        assertEquals(testElement, savedElement);
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
                        assertEquals("abc", testObject.getText());
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

    @Test
    public void testObserveTableChange() throws Exception {
        //noinspection unchecked
        Action1<Void> action = Mockito.mock(Action1.class);
        Mockito.doNothing().when(action).call(any(Void.class));
        Subscription subscription = RxSQLite.get().observeChanges(TestTable.TABLE).subscribe(action);

        SQLite.get().insert(TestTable.TABLE, new TestObject(10010, 6.4, "changes"));
        Mockito.verify(action).call(null);

        //noinspection unchecked
        Mockito.reset(action);
        subscription.unsubscribe();

        SQLite.get().delete(TestTable.TABLE);
        Mockito.verifyNoMoreInteractions(action);
    }

    @Test
    public void testObserveTableChangeWithData() throws Exception {
        RxUtils.setupTestSchedulers();

        //noinspection unchecked
        Action1<List<TestObject>> action = Mockito.mock(Action1.class);
        Mockito.doNothing().when(action).call(anyListOf(TestObject.class));
        Subscription subscription = RxSQLite.get().observeChanges(TestTable.TABLE).withQuery().subscribe(action);

        SQLite.get().insert(TestTable.TABLE, new TestObject(10410, 8.9, "ca'pcj;s;vhjvksf;bgd"));
        Mockito.verify(action).call(anyListOf(TestObject.class));

        //noinspection unchecked
        Mockito.reset(action);
        subscription.unsubscribe();

        SQLite.get().delete(TestTable.TABLE);
        Mockito.verifyNoMoreInteractions(action);
    }


    @After
    public void tearDown() throws Exception {
        SQLite.get().delete(TestTable.TABLE);
    }
}
