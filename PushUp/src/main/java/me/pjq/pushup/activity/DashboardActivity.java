package me.pjq.pushup.activity;

import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.Player;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.squareup.otto.Bus;
import me.pjq.pushup.*;
import me.pjq.pushup.fragment.*;
import me.pjq.pushup.utils.ScreenshotUtils;
import me.pjq.pushup.utils.TitlebarHelper;
import me.pjq.pushup.utils.Utils;

/**
 * Created by pengjianqing on 11/13/13.
 */
public class DashboardActivity extends BaseGameActivity implements View.OnClickListener, FragmentController {
    private static final String TAG = DashboardActivity.class.getSimpleName();
    private TextView userInfo;
    private ImageView userIcon;
    private TextView share;

    Bus bus;
    AppPreference appPreference;

    int CONTENT_VIEW_ID;
    DashboardFragment dashboardFragment;
    PushupsFragment proximityFragment;

    private String currentFragmentTag;
    private TitlebarHelper titlebarHelper;

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.user_icon:
            case R.id.user_info: {
                showGameBoardFragment();

                break;
            }

            case R.id.button_sign_in:
                // start the sign-in flow
                beginUserInitiatedSignIn();
                break;

            case R.id.button_sign_out:
                // sign out.
                signOut();
                showSignInBar();
                break;

            case R.id.share_textview: {
                final String text = String.format(getString(R.string.share_text_full_total), appPreference.getTotalNumber());
                final String filename = ScreenshotUtils.getshotFilePath();
                ScreenshotUtils.shotBitmap(DashboardActivity.this, filename);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Utils.share(DashboardActivity.this, DashboardActivity.this.getString(R.string.app_name), text, filename);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                share.startAnimation(animation);
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
        CONTENT_VIEW_ID = R.id.mainFrameLayout;

        bus = ServiceProvider.getBus();
        bus.register(this);

        appPreference = AppPreference.getInstance(getApplicationContext());
        titlebarHelper = new TitlebarHelper(DashboardActivity.this, new TitlebarHelper.OnTitlebarClickListener() {
            @Override
            public void onClickIcon() {
            }

            @Override
            public void onClickTitle() {
            }
        });

        userIcon = (ImageView) findViewById(R.id.user_icon);
        userInfo = (TextView) findViewById(R.id.user_info);
        share = (TextView) findViewById(R.id.share_textview);

        userInfo.setOnClickListener(this);
        userIcon.setOnClickListener(this);
        share.setOnClickListener(this);
        findViewById(R.id.button_sign_in).setOnClickListener(this);
        findViewById(R.id.button_sign_out).setOnClickListener(this);

        getGamesClient().registerConnectionCallbacks(new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                submitScore();
            }

            @Override
            public void onDisconnected() {
                showAlert("Connect", "Disconnected!");
            }
        });

        getGamesClient().connect();

        showDashboardFragment();
    }


    public void showProximityFragment() {
        currentFragmentTag = PushupsFragment.TAG;
        Fragment fragment = findFragmentByTag(PushupsFragment.TAG);
        hideTheOtherFragment();

        if (null == fragment) {
            fragment = PushupsFragment.newInstance(new Bundle());
//            replaceChildFragment(fragment, PushupsFragment.TAG, fromLeft2Right());
            addChildFragment(fragment, PushupsFragment.TAG, fromLeft2Right());

        } else {
            showFragment(fragment, PushupsFragment.TAG, fromLeft2Right());
        }

        notifyFragmentChangeAll(PushupsFragment.TAG);
    }

    public void showGameBoardFragment() {
        currentFragmentTag = GameBoardFragment.TAG;
        Fragment fragment = findFragmentByTag(GameBoardFragment.TAG);
        hideTheOtherFragment();

        if (null == fragment) {
            fragment = GameBoardFragment.newInstance(new Bundle());
//            replaceChildFragment(fragment, GameBoardFragment.TAG, fromLeft2Right());
            addChildFragment(fragment, GameBoardFragment.TAG, fromLeft2Right());

        } else {
            showFragment(fragment, GameBoardFragment.TAG, fromLeft2Right());
        }

        notifyFragmentChangeAll(GameBoardFragment.TAG);
    }

    public void showDashboardFragment() {
        currentFragmentTag = DashboardFragment.TAG;
        Fragment fragment = findFragmentByTag(DashboardFragment.TAG);
        hideTheOtherFragment();

        if (null == fragment) {
            fragment = DashboardFragment.newInstance(new Bundle());
//            replaceChildFragment(fragment, DashboardFragment.TAG, fromLeft2Right());
            addChildFragment(fragment, DashboardFragment.TAG, fromLeft2Right());

        } else {
            showFragment(fragment, DashboardFragment.TAG, fromLeft2Right());
        }

        notifyFragmentChangeAll(DashboardFragment.TAG);
    }

    public void showLanGameFragment() {
        currentFragmentTag = MultiPlayerFragment.TAG;
        Fragment fragment = findFragmentByTag(MultiPlayerFragment.TAG);
        hideTheOtherFragment();

        if (null == fragment) {
            fragment = MultiPlayerFragment.newInstance(new Bundle());
//            replaceChildFragment(fragment, MultiPlayerFragment.TAG, fromLeft2Right());
            addChildFragment(fragment, MultiPlayerFragment.TAG, fromLeft2Right());

        } else {
            showFragment(fragment, MultiPlayerFragment.TAG, fromLeft2Right());
        }

        notifyFragmentChangeAll(MultiPlayerFragment.TAG);
    }

    public void showTwistGameFragment() {
        currentFragmentTag = WristGameFragment.TAG;
        Fragment fragment = findFragmentByTag(WristGameFragment.TAG);
        hideTheOtherFragment();

        if (null == fragment) {
            fragment = WristGameFragment.newInstance(new Bundle());
//            replaceChildFragment(fragment, MultiPlayerFragment.TAG, fromLeft2Right());
            addChildFragment(fragment, WristGameFragment.TAG, fromLeft2Right());

        } else {
            showFragment(fragment, WristGameFragment.TAG, fromLeft2Right());
        }

        notifyFragmentChangeAll(WristGameFragment.TAG);
    }

    private void notifyFragmentChange(String fragmentTag, String tag) {
        Fragment fragment = findFragmentByTag(fragmentTag);
        if (null != fragment) {
            ((FragmentBridge) fragment).changeToFragment(tag);
        }
    }

    private void notifyFragmentChangeAll(String tag) {
        notifyFragmentChange(PushupsFragment.TAG, tag);
        notifyFragmentChange(DashboardFragment.TAG, tag);
        notifyFragmentChange(GameBoardFragment.TAG, tag);
        notifyFragmentChange(MultiPlayerFragment.TAG, tag);
        notifyFragmentChange(WristGameFragment.TAG, tag);
    }


    private boolean fromLeft2Right() {
        return true;
//        if (currentClickedIndex == 0 && previewClickedIndex == 0) {
//            return true;
//        }
//
//
//        if (currentClickedIndex > previewClickedIndex) {
//            return true;
//        } else {
//            return false;
//        }
    }

    private void hideTheOtherFragment() {
        Fragment fragment = findFragmentByTag(PushupsFragment.TAG);
        hideFragment(fragment, fromLeft2Right());

        fragment = findFragmentByTag(DashboardFragment.TAG);
        hideFragment(fragment, fromLeft2Right());

        fragment = findFragmentByTag(GameBoardFragment.TAG);
        hideFragment(fragment, fromLeft2Right());

        fragment = findFragmentByTag(MultiPlayerFragment.TAG);
        hideFragment(fragment, fromLeft2Right());

        fragment = findFragmentByTag(WristGameFragment.TAG);
        hideFragment(fragment, fromLeft2Right());
    }

    private void submitScore() {
        if (!getGamesClient().isConnected()) {
            return;
        }

        getGamesClient().unlockAchievement(
                getString(R.string.achievement_activate));

        int total = AppPreference.getInstance(getApplicationContext()).getTotalNumber();
        getGamesClient().submitScore(getString(R.string.leaderboard_total_pushups), total);

        if (total >= 100) {
            getGamesClient().unlockAchievement(
                    getString(R.string.achievement_100_pushup));
        }

        if (total >= 500) {
            getGamesClient().unlockAchievement(
                    getString(R.string.achievement_master_level500));
        }

        int totalDay = AppPreference.getInstance(getApplicationContext()).getHowManyDays();
        getGamesClient().submitScore(getString(R.string.leaderboard_total_days), totalDay);

        if (totalDay >= 2) {
            getGamesClient().unlockAchievement(
                    getString(R.string.achievement_2days));
        }

        if (totalDay >= 10) {
            getGamesClient().unlockAchievement(
                    getString(R.string.achievement_10days));
        }

        int numberOfTimes= AppPreference.getInstance(getApplicationContext()).getNumberOfTimes();
        getGamesClient().submitScore(getString(R.string.leaderboard_number_of_times), numberOfTimes);
    }

    // Shows the "sign in" bar (explanation and button).
    private void showSignInBar() {
        findViewById(R.id.sign_in_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_bar).setVisibility(View.GONE);
    }

    // Shows the "sign out" bar (explanation and button).
    private void showSignOutBar() {
        findViewById(R.id.sign_in_bar).setVisibility(View.GONE);
        findViewById(R.id.sign_out_bar).setVisibility(View.VISIBLE);
    }

    /**
     * Called to notify us that sign in failed. Notice that a failure in sign in is not
     * necessarily due to an error; it might be that the user never signed in, so our
     * attempt to automatically sign in fails because the user has not gone through
     * the authorization flow. So our reaction to sign in failure is to show the sign in
     * button. When the user clicks that button, the sign in process will start/resume.
     */
    @Override
    public void onSignInFailed() {
        // Sign-in has failed. So show the user the sign-in button
        // so they can click the "Sign-in" button.
        showSignInBar();
    }

    /**
     * Called to notify us that sign in succeeded. We react by loading the loot from the
     * cloud and updating the UI to show a sign-out button.
     */
    @Override
    public void onSignInSucceeded() {
        // Sign-in worked!
        showSignOutBar();

        playerInfo();
    }

    private void playerInfo() {
        Player player = getGamesClient().getCurrentPlayer();
        String name = player.getDisplayName();
        Uri uri = player.getIconImageUri();

        String displayName;
        if (player == null) {
            Log.w(TAG, "mGamesClient.getCurrentPlayer() is NULL!");
            displayName = "???";
        } else {
            displayName = player.getDisplayName();
            appPreference.setLoginName(displayName);
        }


        userInfo.setText(String.format(getString(R.string.you_are_signed_in_as), displayName));
        userIcon.setImageURI(uri);
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
    public void showFragment(String tag) {
        if (tag.equalsIgnoreCase(DashboardFragment.TAG)) {
            showDashboardFragment();
        } else if (tag.equalsIgnoreCase(PushupsFragment.TAG)) {
            showProximityFragment();
        } else if (tag.equalsIgnoreCase(GameBoardFragment.TAG)) {
            showGameBoardFragment();
        } else if (tag.equalsIgnoreCase(MultiPlayerFragment.TAG)) {
            showLanGameFragment();
        } else if (tag.equalsIgnoreCase(WristGameFragment.TAG)) {
            showTwistGameFragment();
        }
    }

    @Override
    public GamesClient getGamesClientPublic() {
        return getGamesClient();
    }

    @Override
    public boolean isSignedInPublic() {
        return isSignedIn();
    }

    @Override
    public void showAlertPublic(String message) {
        showAlert(message);
    }

    @Override
    public void onShowAchievementsRequested() {

    }

    @Override
    public void onShowLeaderboardsRequested() {

    }

    @Override
    public void onShowInvitationRequested() {

    }

    @Override
    public void beginUserInitiatedSignInImpl() {

    }

    @Override
    public void signOutImpl() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (!currentFragmentTag.equalsIgnoreCase(DashboardFragment.TAG)) {
                showDashboardFragment();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MyApplication.getPeersMgr().stop();
        android.os.Process.killProcess(Process.myPid());
    }
}
