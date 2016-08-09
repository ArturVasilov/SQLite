package ru.arturvasilov.sqlite.testutils;

import android.support.annotation.NonNull;

import ru.arturvasilov.sqlite.core.SQLiteConfig;
import ru.arturvasilov.sqlite.core.SQLiteContentProvider;
import ru.arturvasilov.sqlite.core.SQLiteSchema;

/**
 * @author Artur Vasilov
 */
public class SQLiteProvider extends SQLiteContentProvider {

    @Override
    protected void prepareConfig(@NonNull SQLiteConfig config) {
        config.setAuthority("ru.arturvasilov.sqlite");
        config.setDatabaseName("database.db");
    }

    @Override
    protected void prepareSchema(@NonNull SQLiteSchema schema) {
        schema.register(TestTable.TABLE);
    }
}
