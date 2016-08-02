# SQLite

#### Yet another Android library for database

Extremely simple database library for Android based on SQLite and ContentProvider, which provides simple way to all operations with data. 

### Advantages:

* 0 reflection
* Full customization (since library is a simple wrapper on ContentProvider you can always access to it)
* Flexible interface for manipulating data
* Data migration

### Usage:

Create all tables you need (classes which implements Table interface or better extend BaseTable class):
```java
public class TestTable extends BaseTable<TestContentClass> {

    public static final Table<TestContentClass> TABLE = new TestTable();
    
    public static final String ID = "id";
    public static final String TEXT = "text";

    @Override
    public void onCreate(@NonNull SQLiteDatabase database) {
        TableBuilder.create(this)
                .intColumn(Columns.ID)
                .stringColumn(Columns.TEXT)
                .primaryKey(Columns.ID)
                .execute(database);
    }

    @NonNull
    @Override
    public ContentValues toValues(@NonNull TestContentClass object) {
        ContentValues values = new ContentValues();
        values.put(ID, object.getId());
        values.put(TEXT, object.getText());
        return values;
    }

    @NonNull
    @Override
    public TestContentClass fromCursor(@NonNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(ID));
        String text = cursor.getString(cursor.getColumnIndex(TEXT));
        return new TestContentClass(id, text);
    }

}
```

Then you need to create your own ContentProvider, specitfy authority and add these tables:
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
        schema.register(TestTable.TABLE);
    }
}
```

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

### Query

All data operations handled with SQLite class like this:
```java
TestContentClass test = new TestContentClass(5, "aaaa");
SQLite.get().insert(TestTable.TABLE).insert(test);
TestContentClass saved = SQLite.get().query(TestTable.TABLE).object().execute();
```

### Data migration

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

### RxJava support

RxJava is now supported only for query operations. Simply call *asObservable* method:

```java
SQLite.get().query(TestTable.TABLE).all().asObservable();
```

### Future plans

1. Ability to swap storage to in-memory database for testing purposes
2. Methods for queries parameters (such as between, where and so on) - it's required to write it using SQL syntax now which is not good
3. Add SQLite bindings
4. Add functions and triggers
5. Generate most of boilerplate code
