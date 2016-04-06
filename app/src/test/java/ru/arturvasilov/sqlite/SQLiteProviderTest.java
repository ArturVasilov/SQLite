package ru.arturvasilov.sqlite;

import android.app.Application;
import android.database.Cursor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.sqlite.core.SQLiteContentProvider;
import ru.arturvasilov.sqlite.table.Table;
import ru.arturvasilov.sqlite.utils.SQLiteEnv;
import ru.arturvasilov.sqlite.utils.TestContentClass;
import ru.arturvasilov.sqlite.utils.TestTable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Artur Vasilov
 */
@Config(constants = BuildConfig.class, sdk = 21)
@RunWith(RobolectricGradleTestRunner.class)
public class SQLiteProviderTest {

    private SQLiteContentProvider mProvider;

    @Before
    public void setUp() throws Exception {
        List<Table> tables = new ArrayList<Table>() {{
            add(TestTable.TABLE);
        }};
        mProvider = SQLiteEnv.registerProvider(tables);

        Application application = RuntimeEnvironment.application;
        application.onCreate();
        SQLite.initialize(application);
    }

    @Test
    public void testSQLite() throws Exception {
        TestContentClass test = new TestContentClass(1, "aaaa");
        SQLite.get()
                .insert(TestTable.TABLE)
                .insert(test);

        List<TestContentClass> all = SQLite.get()
                .query(TestTable.TABLE)
                .all()
                .execute();
        assertEquals(1, all.size());
        assertTrue(test.equals(all.get(0)));

        test.setText("BBBBB");
        int rows = SQLite.get()
                .update(TestTable.TABLE)
                .insert(test)
                .where("id=?")
                .whereArgs(new String[]{"1"})
                .execute();

        all = SQLite.get()
                .query(TestTable.TABLE)
                .all()
                .execute();
        assertEquals(1, rows);
        assertEquals(1, all.size());
        assertTrue(test.equals(all.get(0)));

        SQLite.get()
                .delete(TestTable.TABLE)
                .execute();

        assertTrue(SQLite.get()
                .query(TestTable.TABLE)
                .all()
                .execute()
                .isEmpty());
    }

    @Test
    public void testQuery() throws Exception {
        TestContentClass test = new TestContentClass(1, "aaaa");
        SQLite.get().insert(TestTable.TABLE).insert(test);

        Cursor cursor = mProvider.query(TestTable.TABLE.getUri(), null, null, null, null);
        assertNotNull(cursor);
        assertTrue(cursor.moveToFirst());
        TestContentClass test2 = TestTable.TABLE.fromCursor(cursor);
        assertTrue(test.equals(test2));
        cursor.close();

        SQLite.get().delete(TestTable.TABLE).execute();
    }

    @After
    public void tearDown() throws Exception {
        mProvider.shutdown();
    }
}
