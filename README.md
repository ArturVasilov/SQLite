# SQLite [![Apache License](https://img.shields.io/badge/license-Apache%20v2-blue.svg)](https://github.com/ArturVasilov/SQLite/blob/master/LICENSE) [![Build Status](https://travis-ci.org/ArturVasilov/SQLite.png?branch=master)](https://github.com/ArturVasilov/SQLite)[![codecov.io](http://codecov.io/github/ArturVasilov/SQLite/coverage.svg?branch=master)](http://codecov.io/github/ArturVasilov/SQLite?branch=master)

#### Yet another Android library for database

Extremely simple database library for Android based on SQLite and ContentProvider, which provides simple way to all operations with data. 

### Advantages:

* 0 reflection
* Full customization (since library is a simple wrapper on ContentProvider you can always access to it)
* Flexible interface for manipulating data
* Data migration
* RxJava support

### Gradle

```groovy
compile 'ru.arturvasilov:sqlite:0.1.0'
```

### Tables:

Instead of generating code and using your model classes for database directly, this library uses tables classes for each table in database (or in fact for each class you want to store). It's routine to write these classes but it also give you more control, which is useful for features like data migration.

So, create your classes and for each table you want create class for this table:

```java
public class PersonTable extends BaseTable<Person> {

    public static final Table<Person> TABLE = new PersonTable();

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String AGE = "age";

    @Override
    public void onCreate(@NonNull SQLiteDatabase database) {
        TableBuilder.create(this)
                .intColumn(ID)
                .stringColumn(NAME)
                .intColumn(AGE)
                .primaryKey(ID)
                .execute(database);
    }

    @NonNull
    @Override
    public ContentValues toValues(@NonNull Person person) {
        ContentValues values = new ContentValues();
        values.put(ID, person.getId());
        values.put(NAME, person.getName());
        values.put(AGE, person.getAge());
        return values;
    }

    @NonNull
    @Override
    public Person fromCursor(@NonNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(ID));
        String name = cursor.getString(cursor.getColumnIndex(NAME));
        int age = cursor.getInt(cursor.getColumnIndex(AGE));
        return new Person(id, name, age);
    }
}
```

### Setting Up

After creating tables your need to specify ContentProvider, where you add these tables:
```java
public class SQLiteProvider extends SQLiteContentProvider {

    private static final String DATABASE_NAME = "com.myapp.database";
    private static final String CONTENT_AUTHORITY = "com.myapp";

    @Override
    protected void prepareConfig(@NonNull SQLiteConfig config) {
        config.setDatabaseName(DATABASE_NAME);
        config.setAuthority(CONTENT_AUTHORITY);
    }

    @Override
    protected void prepareSchema(@NonNull Schema schema) {
        schema.register(PersonTable.TABLE);
    }
}
```

And register it in the AndroidManifest.xml:
```xml
<provider
    android:name=".sqlite.SQLiteProvider"
    android:authorities="com.myapp"
    android:exported="false"/>
```

And initialize SQLite:
```java
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SQLite.initialize(this);
    }
}
```

### Operations

All operations should go through the SQLite or RxSQLite classes. You can access to ContentProvider directly, but note that you can loose features like observing changes in tables.

Every operation (query, insert, update, delete) exist both in direct and rx ways. 

You can query for data like so:
```java
Person person = SQLite.get().queryObject(PersonTable.TABLE, Where.create());
// or for list
List<Person> persons = SQLite.get().query(PersonTable.TABLE, Where.create());
// or with where
List<Person> adults = SQLite.get().query(PersonTable.TABLE, Where.create()
        .where(PersonTable.AGE + ">=?")
        .whereArgs(new String[]{"18"}));
```

Similar way for RxSQLite:

```java
RxSQLite.get().query(PersonTable.TABLE, Where.create())
        .subscribe(persons -> {
            //do something with persons
        });
```

*Note*: RxSQLite doesn't take care about doing operations in background - it's up to your. 

And it's all the same for other operations.

### Observing changes

Observing changes in database is a great way for comunication between your UI classes and network layer. This library provides flexible implementation of this pattern.

Get notified when table changed:
```java
public class MainActivity extends AppCompatActivity implements BasicTableObserver {

    // ...

    @Override
    protected void onResume() {
        super.onResume();
        SQLite.get().registerObserver(PersonTable.TABLE, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SQLite.get().unregisterObserver(this);
    }

    @Override
    public void onTableChanged() {
        // You now know that content in the table has changed
    }

}
```

If you also want to get all the content from database (typical for the observing changes), you can change *BasicTableObserver* to *ContentTableObserver* and implement it's method:
```java
@Override
public void onTableChanged(@NonNull List<Person> persons) {
    // handle changed persons
}
```
Everything else is the same! And more, you don't need to care about perfomance, for these changes library reads queries tables in the background already. *Note*: that's why you should be careful using this type of subscribption - frequent changes in table may affect your app.

It's even more flexible with RxSQLite:
```java
private Subscription mPersonsSubscription;

//...

@Override
protected void onResume() {
    super.onResume();
    mPersonsSubscription = RxSQLite.get().observeChanges(PersonTable.TABLE)
                .subscribe(value -> {
                    // table changed
                });
}

@Override
protected void onPause() {
    super.onPause();
    mPersonsSubscription.unsubscribe();
}
```

You can also query all the table with one simple call:
```java
mPersonsSubscription = RxSQLite.get().observeChanges(PersonTable.TABLE).withQuery().subscribe(persons -> {});
```

*Note* you still have to manage subscription manually.

### Data migration

Data migration is always is most paintful part. Library provides you a way to update the table and decide how it should be updated.

Each table has method ```getLastUpgradeVersion```, which by default returns 1. Current database version is the maximum of all tables versions. 

If you changed any table, simple update it's version. All others table won't be affected, if their version is less than maximum:
```java
@Override
public int getLastUpgradeVersion() {
    return 2;
}
```

By default, *onUpdate* method simply recreates the table, but you can customize it by overriding this method:
```java
@Override
public void onUpgrade(@NonNull SQLiteDatabase database, int oldVersion, int newVersion) {
    if (newVersion <= getLastUpgradeVersion() && newVersion > oldVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + getTableName());
        onCreate(database);
    }
}
```

### Future plans

1. Ability to swap storage to in-memory database for testing purposes
2. Methods for queries parameters (such as between, where and so on) - it's required to write it using SQL syntax now which is not good
3. Add SQLite bindings
4. Add functions and triggers
5. Generate most of boilerplate code

### Issues

Feel free to create issues or even pull requests!
