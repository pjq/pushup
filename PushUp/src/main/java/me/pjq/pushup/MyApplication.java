
package me.pjq.pushup;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();

    private static Context context;

    private static boolean sdCardAvailable = true;

    public static int mScreenWidth = 0;
    public static int mScreenHeight = 0;
    public static float mDensity = 0.0f;
    public static int mDensityDpi = 0;

    private MediaCardStateBroadcastReceiver mediaCardStateBroadcastReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        EFLogger.init();
        // Add Exception Handler.
        //Thread.setDefaultUncaughtExceptionHandler(new EFUncaughtExceptionHandler());
        StatUtil.setGoogleAnalyticsExceptionHandler(context);

        mediaCardStateBroadcastReceiver = new MediaCardStateBroadcastReceiver();
        MediaCardStateBroadcastReceiver.register(context, mediaCardStateBroadcastReceiver);

        getSystemParasmeter();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();

        StatUtil.sendEvent("app_memory", "low", null, null);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        EFLogger.d(TAG, "onTerminate");

        if (null != mediaCardStateBroadcastReceiver) {
            MediaCardStateBroadcastReceiver.unRegister(context, mediaCardStateBroadcastReceiver);
        }
    }

    public static void getSystemParasmeter() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;

        if (android.os.Build.VERSION.SDK_INT >= 11 && android.os.Build.VERSION.SDK_INT <= 13) {
            Class<?> c = null;
            Object obj = null;
            Field field = null;
            int x = 0, sbar = 0;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj).toString());
                sbar = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            mScreenHeight = mScreenHeight - sbar;
        }
    }

    public static Context getContext() {
        return context;
    }

    private static class MediaCardStateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            EFLogger.d(TAG, "Media state changed, intentAction:" + intent.getAction());
            if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
                sdCardAvailable = false;
            } else if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
                sdCardAvailable = true;
            }
        }

        public static void register(Context context, MediaCardStateBroadcastReceiver receiver) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            intentFilter.addDataScheme("file");
            context.registerReceiver(receiver, intentFilter);
        }

        public static void unRegister(Context context, MediaCardStateBroadcastReceiver receiver) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    public static boolean isSDCardAvailable() {
        String status = Environment.getExternalStorageState();
        if (sdCardAvailable && status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }

        return false;
    }
}
