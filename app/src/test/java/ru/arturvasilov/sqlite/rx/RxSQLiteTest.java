package ru.arturvasilov.sqlite.rx;

import android.app.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.sqlite.BuildConfig;
import ru.arturvasilov.sqlite.SQLite;
import ru.arturvasilov.sqlite.table.Table;
import ru.arturvasilov.sqlite.testutils.SQLiteEnv;
import ru.arturvasilov.sqlite.testutils.TestContentClass;
import ru.arturvasilov.sqlite.testutils.TestTable;
import rx.Observable;
import rx.functions.Func2;

import static org.junit.Assert.assertEquals;

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

        SQLite.get().delete(TestTable.TABLE).execute();
    }

    @Test
    public void testSingleElement() throws Exception {
        TestContentClass testElement = new TestContentClass(10, "abc");
        SQLite.get().insert(TestTable.TABLE).insert(testElement);

        TestContentClass savedElement = SQLite.get().query(TestTable.TABLE).object()
                .asObservable()
                .toBlocking()
                .first();

        assertEquals(testElement, savedElement);
    }

    @Test
    public void testElementsList() throws Exception {
        List<TestContentClass> elements = new ArrayList<>();
        elements.add(new TestContentClass(1, "a"));
        elements.add(new TestContentClass(2, "ab"));
        elements.add(new TestContentClass(3, "abc"));
        elements.add(new TestContentClass(4, "abcd"));
        elements.add(new TestContentClass(5, "abcde"));
        SQLite.get().insert(TestTable.TABLE).insert(elements);

        Observable.zip(SQLite.get().query(TestTable.TABLE).all().asObservable(),
                Observable.just(elements), new Func2<List<TestContentClass>, List<TestContentClass>, Void>() {
                    @Override
                    public Void call(List<TestContentClass> testElements, List<TestContentClass> savedElements) {
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
    public void testEmptyList() throws Exception {
        List<TestContentClass> elements = SQLite.get().query(TestTable.TABLE).all().asObservable()
                .toBlocking().first();

        assertEquals(0, elements.size());
    }
}
