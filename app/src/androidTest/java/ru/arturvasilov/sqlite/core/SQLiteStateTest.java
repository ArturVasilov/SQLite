package ru.arturvasilov.sqlite.core;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertNotNull;

/**
 * @author Artur Vasilov
 */
@RunWith(AndroidJUnit4.class)
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
        SQLite.initialize(InstrumentationRegistry.getContext());
        assertNotNull(SQLite.get());
    }
}
