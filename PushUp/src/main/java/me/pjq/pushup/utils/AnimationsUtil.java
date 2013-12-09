package me.pjq.pushup.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * Created by pjq on 11/17/13.
 */
public class AnimationsUtil {

    public static void doFrameAnimation() {

    }

    public static void sacleBreath(View view, float to) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, to, 1, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        scaleAnimation.setRepeatCount(Animation.INFINITE);
//		scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setDuration(500);
        view.startAnimation(scaleAnimation);
        scaleAnimation.start();
    }
}
