package me.pjq.pushup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.squareup.otto.Bus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pengjianqing on 11/8/13.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static void overridePendingTransitionRight2Left(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void overridePendingTransitionLeft2Right(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static void share(Context context, String subject, String text, String filePath) {
        long start = System.currentTimeMillis();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent,
                context.getString(R.string.app_name)));
        long usetime = System.currentTimeMillis() - start;

        EFLogger.i(TAG, "use time " + usetime);
    }

    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * Check if the same day
     *
     * @param lastTime
     * @param now
     * @return
     */
    public static boolean isSameDay(long lastTime, long now) {
        Date lastDate = new Date(lastTime);
        Date nowDate = new Date(now);
        String lastDateString = sDateFormat.format(lastDate);
        String nowDateString = sDateFormat.format(nowDate);

        if (lastDateString.equalsIgnoreCase(nowDateString)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getTheDayKey(long time) {
        String dateKey = sDateFormat.format(time);
        return dateKey;
    }

    public static void sendUpdateMsg() {
        Bus bus = ServiceProvider.getBus();
        bus.post(new UpdateMsg());
    }
}
