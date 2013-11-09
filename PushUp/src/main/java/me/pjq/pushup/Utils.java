package me.pjq.pushup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by pengjianqing on 11/8/13.
 */
public class Utils {
    public static void overridePendingTransitionRight2Left(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void overridePendingTransitionLeft2Right(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static void share(Context context, String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent,
                context.getString(R.string.app_name)));
    }
}
