package ru.arturvasilov.sqlite.core;

import android.content.UriMatcher;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Simple class for controlling database schema (for this moment - only tables).
 * You have to use only {@link SQLiteSchema#register(Table)} method to add your tables to database.
 *
 * @author Artur Vasilov
 */
public class SQLiteSchema implements Iterable<Table> {

    private final UriMatcher mUriMatcher;

    private final List<Table> mTables;

    SQLiteSchema() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mTables = new ArrayList<>();
    }

    /**
     * This method adds you table to the ContentProvider
     *
     * @param table - table to add to the database
     */
    public void register(@NonNull Table table) {
        mUriMatcher.addURI(SQLiteContentProvider.getContentAuthority(), table.getTableName(), mTables.size());
        mTables.add(table);
    }

    int calculateVersion() {
        int version = 1;
        for (Table table : mTables) {
            int tableVersion = table.getLastUpgradeVersion();
            if (tableVersion > version) {
                version = tableVersion;
            }
        }
        return version;
    }

    @NonNull
    String findTable(@NonNull Uri uri) {
        int index = mUriMatcher.match(uri);
        if (index < 0 || index >= mTables.size()) {
            return "";
        }
        return mTables.get(index).getTableName();
    }

    @Override
    public Iterator<Table> iterator() {
        return mTables.iterator();
    }
}
