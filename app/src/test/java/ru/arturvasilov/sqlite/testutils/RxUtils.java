package ru.arturvasilov.sqlite.testutils;

import rx.Scheduler;
import rx.functions.Func1;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

/**
 * @author Artur Vasilov
 */
public class RxUtils {

    public static void setupTestSchedulers() {
        try {
            RxJavaHooks.setOnIOScheduler(new Func1<Scheduler, Scheduler>() {
                @Override
                public Scheduler call(Scheduler scheduler) {
                    return Schedulers.immediate();
                }
            });
        } catch (Exception ignored) {
        }
    }

}
