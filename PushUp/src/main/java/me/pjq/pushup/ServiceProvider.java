
package me.pjq.pushup;

import android.content.Context;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceProvider {

    private static final String APP_STORAG_SHARED_PREFS_NAME = "appStorageSp";

    private static final String REMOTE_FILE_CACHE_SP_NAME = "remoteFileCacheSp";

    private static Context context;

    private static ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static ExecutorService zipexecutorService = Executors.newFixedThreadPool(1);

    public static void init(Context applicationContext) {

        ServiceProvider.context = applicationContext;
    }


    public static ExecutorService getExecutorService() {
        return executorService;
    }

    private static final Bus BUS = new Bus(ThreadEnforcer.ANY);

    public static Bus getBus() {
        return BUS;
    }

    public static Context getContext() {
        return context;
    }

}
