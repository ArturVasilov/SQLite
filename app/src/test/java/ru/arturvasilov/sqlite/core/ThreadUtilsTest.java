package ru.arturvasilov.sqlite.core;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

/**
 * @author Artur Vasilov
 */
@RunWith(JUnit4.class)
public class ThreadUtilsTest {

    @Test
    public void testActionCompletedInBackground() throws Exception {
        final Runnable testRunnable = Mockito.mock(Runnable.class);
        Mockito.doNothing().when(testRunnable).run();

        ThreadUtils.runInBackground(createBackgroundRunnable(testRunnable));
        Thread.sleep(150);

        Mockito.verify(testRunnable).run();
    }

    @NonNull
    private Runnable createBackgroundRunnable(@NonNull final Runnable testRunnable) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5);
                    testRunnable.run();
                } catch (InterruptedException ignored) {
                }
            }
        };
    }

}
