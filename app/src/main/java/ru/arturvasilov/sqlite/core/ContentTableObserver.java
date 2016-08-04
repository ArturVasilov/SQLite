package ru.arturvasilov.sqlite.core;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * @author Artur Vasilov
 */
public interface ContentTableObserver<T> {

    void onTableChanged(@NonNull List<T> tableData);

}
