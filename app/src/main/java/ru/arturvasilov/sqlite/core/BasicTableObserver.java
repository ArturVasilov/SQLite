package ru.arturvasilov.sqlite.core;

/**
 * Use this interface when you want to be notified about the changes in certain table
 * This interface is used in {@link SQLite#registerObserver(Table, BasicTableObserver)}
 *
 * @author Artur Vasilov
 */
public interface BasicTableObserver {

    /**
     * This method simply notifies you that there are changes in the database
     * (after insert, update and delete operations, which has influenced the table)
     */
    void onTableChanged();

}
