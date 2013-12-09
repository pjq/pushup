package me.pjq.pushup.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import me.pjq.pushup.MyApplication;
import me.pjq.pushup.R;

/**
 * Created by pjq on 11/17/13.
 */
public class AnimationsUtil {

    public static void doFrameAnimation() {

    }

    public static void sacleBreath(View view) {
//        ScaleAnimation scaleAnimation = new ScaleAnimation(1, to, 1, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation scaleAnimation = AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.scale_animation);

//        scaleAnimation.setRepeatCount(Animation.INFINITE);
//		scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setDuration(500);
        view.startAnimation(scaleAnimation);
        scaleAnimation.start();
    }
}
