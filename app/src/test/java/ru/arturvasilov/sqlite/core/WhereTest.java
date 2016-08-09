package ru.arturvasilov.sqlite.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Artur Vasilov
 */
@RunWith(JUnit4.class)
public class WhereTest {

    @Test
    public void testNoParameters() throws Exception {
        checkWhere(null, null, null, Where.create());
    }

    @Test
    public void testEqualTo() throws Exception {
        Where where = Where.create().equalTo("id", 5);
        checkWhere("id = ?", new String[]{"5"}, null, where);
    }

    @Test
    public void testNotEqualTo() throws Exception {
        Where where = Where.create().notEqualTo("text", "abcd");
        checkWhere("text <> ?", new String[]{"abcd"}, null, where);
    }

    @Test
    public void testLessThan() throws Exception {
        Where where = Where.create().lessThan("age", 18);
        checkWhere("age < ?", new String[]{"18"}, null, where);
    }

    @Test
    public void testLessThanOrEqualTo() throws Exception {
        Where where = Where.create().lessThanOrEqualTo("price", 207.18);
        checkWhere("price <= ?", new String[]{"207.18"}, null, where);
    }

    @Test
    public void testGreaterThan() throws Exception {
        Where where = Where.create().greaterThan("year", 2008);
        checkWhere("year > ?", new String[]{"2008"}, null, where);
    }

    @Test
    public void testGreaterThanOrEqualTo() throws Exception {
        Where where = Where.create().greaterThanOrEqualTo("age", 18);
        checkWhere("age >= ?", new String[]{"18"}, null, where);
    }

    @Test
    public void testLike() throws Exception {
        Where where = Where.create().like("title", "Politics");
        checkWhere("title LIKE ?", new String[]{"Politics"}, null, where);
    }

    @Test
    public void testBetween() throws Exception {
        Where where = Where.create().between("price", 17.5, 19.8);
        checkWhere("price BETWEEN ? AND ?", new String[]{"17.5", "19.8"}, null, where);
    }

    @Test
    public void testIsNull() throws Exception {
        Where where = Where.create().isNull("body");
        checkWhere("body IS NULL", null, null, where);
    }

    @Test
    public void testNotNull() throws Exception {
        Where where = Where.create().notNull("body");
        checkWhere("body NOT NULL", null, null, where);
    }

    @Test
    public void testIn() throws Exception {
        Where where = Where.create().in("id", 5, 6, 7, 9);
        checkWhere("id IN(?, ?, ?, ?)", new String[]{"5", "6", "7", "9"}, null, where);
    }

    @Test
    public void testAnd() throws Exception {
        Where where = Where.create().greaterThanOrEqualTo("age", 18).and().lessThan("age", 45);
        checkWhere("age >= ? AND age < ?", new String[]{"18", "45"}, null, where);
    }

    @Test
    public void testOr() throws Exception {
        Where where = Where.create().equalTo("city", "London").or().like("country", "R");
        checkWhere("city = ? OR country LIKE ?", new String[]{"London", "R"}, null, where);
    }

    @Test
    public void testLimit() throws Exception {
        Where where = Where.create().limit(3);
        checkWhere(null, null, " LIMIT 3", where);
    }

    @Test
    public void testOffset() throws Exception {
        Where where = Where.create().offset(7);
        checkWhere(null, null, " OFFSET 7", where);
    }

    @Test
    public void testLimitAndOffset() throws Exception {
        Where where = Where.create().limit(1).offset(18);
        checkWhere(null, null, " LIMIT 1 OFFSET 18", where);
    }

    @Test
    public void testWhere() throws Exception {
        Where where = Where.create().where("id > ? AND age <= ?", 1000, 60);
        checkWhere("id > ? AND age <= ?", new String[]{"1000", "60"}, null, where);
    }

    @Test
    public void testGroups() throws Exception {
        Where where = Where.create()
                .beginGroup()
                    .greaterThanOrEqualTo("age", 18)
                    .or()
                    .lessThan("age", 50)
                .endGroup()
                .and()
                .like("name", "Sm");
        checkWhere("(age >= ? OR age < ?) AND name LIKE ?", new String[]{"18", "50", "Sm"}, null, where);
    }

    @Test
    public void testComplexQuery() throws Exception {
        Where where = Where.create()
                .in("id", 18, 20, 24)
                .and()
                .like("text", "hello")
                .limit(1);

        checkWhere("id IN(?, ?, ?) AND text LIKE ?", new String[]{"18", "20", "24", "hello"}, " LIMIT 1", where);
    }

    private void checkWhere(@Nullable String where, @Nullable String[] args,
                            @Nullable String limit, @NonNull Where testWhere) {
        assertEquals(where, testWhere.where());
        if (args == null) {
            assertNull(testWhere.whereArgs());
        } else {
            String[] whereArgs = testWhere.whereArgs();
            assertNotNull(whereArgs);
            assertEquals(args.length, whereArgs.length);
            for (int i = 0; i < args.length; i++) {
               assertEquals(args[i], whereArgs[i]);
            }
        }
        Assert.assertEquals(limit, testWhere.limit());
    }
}
