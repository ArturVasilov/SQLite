package ru.arturvasilov.sqlite.core;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static junit.framework.TestCase.assertNotNull;

/**
 * @author Artur Vasilov
 */
@RunWith(JUnit4.class)
public class SQLiteStateTest {

    @Before
    public void setUp() throws Exception {
        SQLite.reset();
    }

    @Test(expected = IllegalStateException.class)
    public void testNotInitialized() throws Exception {
        SQLite.get();
    }

    @Test
    public void testInitialized() throws Exception {
        Context context = Mockito.mock(Context.class);
        Context appContext = Mockito.mock(Context.class);
        Mockito.when(context.getApplicationContext()).thenReturn(appContext);

        SQLite.initialize(context);
        assertNotNull(SQLite.get());
    }
}
