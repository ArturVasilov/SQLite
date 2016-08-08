package ru.arturvasilov.sqlite.core;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Use this interface when you want to be notified about the changes in certain table and also read all data from the table
 * This interface is used in {@link SQLite#registerObserver(Table, ContentTableObserver)}
 *
 * @author Artur Vasilov
 */
public interface ContentTableObserver<T> {

    /**
     * This method simply notifies you that there are changes in the database
     * (after insert, update and delete operations, which has influenced the table).
     *
     * Data from the table is queried in the background thread, but you must be sure,
     * that your table isn't changing too frequently or you may have performance issues.
     *
     * @param tableData - all elements from the table
     */
    void onTableChanged(@NonNull List<T> tableData);

}
