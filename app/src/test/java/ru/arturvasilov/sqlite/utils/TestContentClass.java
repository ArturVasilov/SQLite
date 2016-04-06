package ru.arturvasilov.sqlite.utils;

import android.support.annotation.NonNull;

/**
 * @author Artur Vasilov
 */
public class TestContentClass {

    private int mId;
    private String mText;

    public TestContentClass(int id, @NonNull String text) {
        mId = id;
        mText = text;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    @NonNull
    public String getText() {
        return mText;
    }

    public void setText(@NonNull String text) {
        mText = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestContentClass that = (TestContentClass) o;

        if (getId() != that.getId()) return false;
        return getText().equals(that.getText());

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getText().hashCode();
        return result;
    }
}
