package me.pjq.pushup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import com.squareup.otto.Bus;

public class BaseFragmentActivity extends FragmentActivity {
    private static final String TAG = BaseFragmentActivity.class.getSimpleName();

//    protected Bus bus;
    public static int CONTENT_VIEW_ID = 1234;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        //StatUtil.setContext(this);

//        bus = ServiceProvider.getBus();
//        bus.register(this);

        if (!isLayoutFromSubclass()) {
            frameLayout = new FrameLayout(this);
            frameLayout.setId(CONTENT_VIEW_ID);
            setContentView(frameLayout, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        StatUtil.onStart(this);
    }

    protected boolean isLayoutFromSubclass() {
        return false;
    }

    private void addChildFragment(Fragment childFragment) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(CONTENT_VIEW_ID, childFragment);
        fragmentTransaction.commit();
    }

    protected void addChildFragment(Fragment fragment, String tag) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.add(CONTENT_VIEW_ID, fragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    protected void showChildFragment(Fragment fragment, String tag) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(CONTENT_VIEW_ID, fragment, tag);
        fragmentTransaction.commit();
    }

    private void replaceChildFragment(Fragment fragment) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(CONTENT_VIEW_ID, fragment);

        fragmentTransaction.commitAllowingStateLoss();
    }

    protected void replaceChildFragment(Fragment fragment, String tag, boolean fromLeftToRight) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fromLeftToRight) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {

            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        fragmentTransaction.replace(CONTENT_VIEW_ID, fragment, tag);
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    protected void addChildFragment(Fragment fragment, String tag, boolean fromLeftToRight) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fromLeftToRight) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {

            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        fragmentTransaction.add(CONTENT_VIEW_ID, fragment, tag);
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    protected void showFragment(Fragment fragment, String tag, boolean fromLeftToRight) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fromLeftToRight) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {

            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }


    protected void replaceChildFragment(Fragment fragment, String tag) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(CONTENT_VIEW_ID, fragment, tag);
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    protected void removeFragment(Fragment fragment) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }


    protected Fragment findFragmentByTag(String tag) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        EFLogger.i(TAG, "findFragmentByTag,tag=" + tag + ",fragment=" + fragment);

        return fragment;
    }

    protected void hideFragment(Fragment fragment, boolean fromLeftToRight) {
        if (null == fragment) {
            return;
        }

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fromLeftToRight) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {

            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        fragmentTransaction.hide(fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    protected void hideFragment(Fragment fragment) {
        if (null == fragment) {
            return;
        }

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        bus.unregister(this);

        StatUtil.onStop(this);
    }
}
