package me.pjq.pushup;

import android.app.Activity;
import android.content.Context;
import com.google.analytics.tracking.android.*;


/**
 * Created by pengjianqing on 6/17/13.
 */
public class StatUtil {

    public static void onStart(Activity context) {
        EasyTracker.getInstance(MyApplication.getContext()).activityStart(context);
    }

    public static void onStop(Activity context) {
        EasyTracker.getInstance(MyApplication.getContext()).activityStop(context);
    }

    public static void setGoogleAnalyticsExceptionHandler(Context context) {
        Tracker easyTracker = EasyTracker.getInstance(context);
        if (easyTracker != null) {
            Thread.UncaughtExceptionHandler myHandler = Thread.getDefaultUncaughtExceptionHandler(); // Current default uncaught exception handler.
            ExceptionReporter exceptionReporter = new ExceptionReporter(
                    easyTracker,                                        // Currently used Tracker.
                    GAServiceManager.getInstance(),                   // GAServiceManager singleton.
                    myHandler, MyApplication.getContext());
            exceptionReporter.setExceptionParser(new MyExceptionParser());

            // Make myHandler the new default uncaught exception handler.
            //Thread.setDefaultUncaughtExceptionHandler(myHandler);
        }
    }

    static class MyExceptionParser implements ExceptionParser {
        @Override
        public String getDescription(String s, Throwable throwable) {
            if (null == throwable) {
                return "";
            }

            return "Uncaught Exception. Thread: " + s + " Exception: " + EFLogger.getDebugReport(throwable);
        }
    }

    static public void sendEvent(String category, String action, String label, Long value) {
        // May return null if a EasyTracker has not yet been initialized with a
        // property ID.
        EasyTracker easyTracker = EasyTracker.getInstance(MyApplication.getContext());
        if (easyTracker != null) {
            easyTracker.send(MapBuilder
                    .createEvent(category,      // Event category (required)
                            action,             // Event action (required)
                            label,              // Event label
                            value)              // Event value
                    .build()
            );
        }
    }

    static public void sendEeception(Exception e, boolean fatal) {
        EasyTracker easyTracker = EasyTracker.getInstance(MyApplication.getContext());
        if (easyTracker != null) {
            final StandardExceptionParser parser = new StandardExceptionParser(MyApplication.getContext(), null);
            easyTracker.send(MapBuilder
                    .createException(parser.getDescription(Thread.currentThread().getName(), e), fatal)
                    .build());
        }
    }

}
