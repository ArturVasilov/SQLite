package ru.arturvasilov.sqlite;

/**
 * @author Artur Vasilov
 */
public class SQLiteException extends RuntimeException {

    public SQLiteException(String detailMessage) {
        super(detailMessage);
    }
}
