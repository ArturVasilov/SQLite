package ru.arturvasilov.sqlite.core;

import android.support.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Artur Vasilov
 */
final class ThreadUtils {

    private static final long RETRY_DELAY = 100;
    private static final int POOL_SIZE = 2;
    private static final int KEEP_ALIVE_TIME = 10;

    private ThreadUtils() {
    }

    private static final RejectedExecutionHandler REJECTED_EXECUTION_HANDLER = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(final Runnable runnable, final ThreadPoolExecutor executor) {
            MainHandler.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    executor.execute(runnable);
                }
            }, RETRY_DELAY);
        }
    };

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(POOL_SIZE,
            POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(POOL_SIZE),
            REJECTED_EXECUTION_HANDLER);

    static void runInBackground(@NonNull final Runnable runnable) {
        THREAD_POOL_EXECUTOR.execute(runnable);
    }
}
