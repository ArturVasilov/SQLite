package ru.arturvasilov.sqlite.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ru.arturvasilov.sqlite.SQLite;

/**
 * @author Artur Vasilov
 */
@RunWith(JUnit4.class)
public class SQLiteStateTest {

    @Test(expected = IllegalStateException.class)
    public void testNotInitialized() throws Exception {
        SQLite.get();
    }
}
