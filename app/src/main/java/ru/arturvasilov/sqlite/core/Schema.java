package ru.arturvasilov.sqlite.core;

import android.content.UriMatcher;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.arturvasilov.sqlite.table.Table;

/**
 * @author Artur Vasilov
 */
public class Schema implements Iterable<Table> {

    private final UriMatcher mUriMatcher;

    private final List<Table> mTables;

    public Schema() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mTables = new ArrayList<>();
    }

    public void register(@NonNull Table table) {
        mUriMatcher.addURI(SQLiteContentProvider.getContentAuthority(), table.getTableName(), mTables.size());
        mTables.add(table);
    }

    public int calculateVersion() {
        int version = 1;
        for (Table table : mTables) {
            int tableVersion = table.getLastUpgradeVersion();
            if (tableVersion > version) {
                version = tableVersion;
            }
        }
        return version;
    }

    @Override
    public Iterator<Table> iterator() {
        return mTables.iterator();
    }

    @NonNull
    public String findTable(@NonNull Uri uri) {
        int index = mUriMatcher.match(uri);
        return mTables.get(index).getTableName();
    }
}
