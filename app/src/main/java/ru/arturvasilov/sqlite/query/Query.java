package ru.arturvasilov.sqlite.query;

/**
 * @author Artur Vasilov
 */
public interface Query<T> {

    QueryList<T> all();

    QueryObject<T> object();
}
