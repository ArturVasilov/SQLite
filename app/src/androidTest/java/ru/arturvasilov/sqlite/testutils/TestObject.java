package ru.arturvasilov.sqlite.testutils;

import android.support.annotation.NonNull;

/**
 * @author Artur Vasilov
 */
public class TestObject {

    private final int mId;
    private final double mRating;
    private String mText;

    public TestObject(int id, double rating, @NonNull String text) {
        mId = id;
        mRating = rating;
        mText = text;
    }

    public int getId() {
        return mId;
    }

    public double getRating() {
        return mRating;
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

        TestObject that = (TestObject) o;

        return getId() == that.getId() && Math.abs(getRating() - that.getRating()) < 0.000001
                && getText().equals(that.getText());

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getText().hashCode();
        return result;
    }
}
