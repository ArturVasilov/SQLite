package ru.arturvasilov.sqlite.utils;

import android.content.pm.ProviderInfo;
import android.support.annotation.NonNull;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowContentResolver;

import java.util.List;

import ru.arturvasilov.sqlite.core.SQLiteConfig;
import ru.arturvasilov.sqlite.core.SQLiteContentProvider;
import ru.arturvasilov.sqlite.core.Schema;
import ru.arturvasilov.sqlite.table.Table;

/**
 * @author Artur Vasilov
 */
public final class SQLiteEnv {

    public static final String AUTHORITY = "ru.arturvasilov.database";
    public static final String NAME = "test_db_name";

    private SQLiteEnv() {
    }

    public static SQLiteContentProvider registerProvider(@NonNull final List<Table> tables) {
        SQLiteContentProvider provider = new SQLiteContentProvider() {
            @Override
            protected void prepareConfig(@NonNull SQLiteConfig config) {
                config.setDatabaseName(NAME);
                config.setAuthority(AUTHORITY);
            }

            @Override
            protected void prepareSchema(@NonNull Schema schema) {
                for (Table table : tables) {
                    schema.register(table);
                }
            }
        };
        registerProvider(provider);
        return provider;
    }

    public static void registerProvider(@NonNull SQLiteContentProvider provider) {
        final ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.name = NAME;
        providerInfo.authority = AUTHORITY;
        provider.attachInfo(RuntimeEnvironment.application, providerInfo);
        provider.onCreate();
        ShadowContentResolver.registerProvider(AUTHORITY, provider);
    }

}
