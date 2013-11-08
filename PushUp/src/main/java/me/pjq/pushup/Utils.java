package me.pjq.pushup;

import android.app.Activity;

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

}
