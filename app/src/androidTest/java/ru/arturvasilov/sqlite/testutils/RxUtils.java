package ru.arturvasilov.sqlite.testutils;

import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Artur Vasilov
 */
public class RxUtils {

    public static void setupTestSchedulers() {
        try {
            RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
                @Override
                public Scheduler apply(Scheduler scheduler) {
                    return Schedulers.trampoline();
                }
            });
        } catch (Exception ignored) {
        }
    }

}
