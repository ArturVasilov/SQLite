package ru.arturvasilov.sqlite.utils;

import android.content.Context;
import android.database.Cursor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import ru.arturvasilov.sqlite.core.SQLite;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Artur Vasilov
 */
@RunWith(JUnit4.class)
public class SQLiteUtilsTest {

    @Test
    public void testInitialized() throws Exception {
        Context context = Mockito.mock(Context.class);
        Context appContext = Mockito.mock(Context.class);
        when(context.getApplicationContext()).thenReturn(appContext);
        SQLite.initialize(context);

        SQLite.get();
    }

    @Test
    public void testCursorEmptyNull() throws Exception {
        boolean isEmpty = SQLiteUtils.isEmptyCursor(null);
        //noinspection ConstantConditions
        assertTrue(isEmpty);
    }

    @Test
    public void testCursorEmptyClosed() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(cursor.isClosed()).thenReturn(true);

        boolean isEmpty = SQLiteUtils.isEmptyCursor(cursor);
        //noinspection ConstantConditions
        assertTrue(isEmpty);
    }

    @Test
    public void testCursorEmpty() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(cursor.isClosed()).thenReturn(true);
        when(cursor.moveToFirst()).thenReturn(false);

        boolean isEmpty = SQLiteUtils.isEmptyCursor(cursor);
        //noinspection ConstantConditions
        assertTrue(isEmpty);
    }

    @Test
    public void testCursorNotEmpty() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(cursor.isClosed()).thenReturn(false);
        when(cursor.moveToFirst()).thenReturn(true);

        boolean isEmpty = SQLiteUtils.isEmptyCursor(cursor);
        //noinspection ConstantConditions
        assertFalse(isEmpty);
    }

    @Test
    public void testCloseNullCursor() throws Exception {
        SQLiteUtils.safeCloseCursor(null);
    }

    @Test
    public void testClosedCursor() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(cursor.isClosed()).thenReturn(true);
        SQLiteUtils.safeCloseCursor(cursor);

        verify(cursor, never()).close();
    }

    @Test
    public void testExceptionCursor() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(cursor.isClosed()).thenReturn(false);
        doThrow(new RuntimeException()).when(cursor).close();
        SQLiteUtils.safeCloseCursor(cursor);
    }

    @Test
    public void testCloseCursor() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(cursor.isClosed()).thenReturn(false);
        doNothing().when(cursor).close();
        SQLiteUtils.safeCloseCursor(cursor);

        verify(cursor).close();
    }

}
