package ru.arturvasilov.sqlite.action;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * @author Artur Vasilov
 */
public interface InsertAction<T> {

    Uri insert(@NonNull T object);

    int insert(@NonNull List<T> objects);

}
