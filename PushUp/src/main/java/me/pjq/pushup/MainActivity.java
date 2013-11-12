package me.pjq.pushup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.google.example.games.basegameutils.*;

import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.Player;
import com.google.android.gms.internal.di;
import com.google.android.gms.internal.ga;
import com.google.example.games.basegameutils.BaseGameActivity;

import java.util.ArrayList;

public class MainActivity extends BaseGameActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    TextView startImageView;
    TextView pushupTextView;
    TextView resultTextView;
    TextView totalTextView;
    TextView daysTextView;
    TextView durationTextView;
    TextView levelTextView;
    private TextView shareTextView;
    private View titlebarIcon;
    private View titlebarText;

    private TextView userInfo;
    private ImageView userIcon;

    Bus bus;

    AppPreference appPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bus = ServiceProvider.getBus();
        bus.register(this);

        appPreference = AppPreference.getInstance(getApplicationContext());

        startImageView = (TextView) findViewById(R.id.start_button);
        pushupTextView = (TextView) findViewById(R.id.pushup_text);
        resultTextView = (TextView) findViewById(R.id.result_text);
        totalTextView = (TextView) findViewById(R.id.total_text);
        shareTextView = (TextView) findViewById(R.id.share_textview);
        titlebarIcon = (ImageView) findViewById(R.id.icon);
        titlebarText = (TextView) findViewById(R.id.title);
        daysTextView = (TextView) findViewById(R.id.days);
        durationTextView = (TextView) findViewById(R.id.duration_time);
        levelTextView = (TextView) findViewById(R.id.level);

        userIcon = (ImageView) findViewById(R.id.user_icon);
        userInfo = (TextView) findViewById(R.id.user_info);

        startImageView.setOnClickListener(this);
        shareTextView.setOnClickListener(this);
        resultTextView.setOnClickListener(this);
        pushupTextView.setOnClickListener(this);
        titlebarIcon.setOnClickListener(this);
        titlebarText.setOnClickListener(this);
        userInfo.setOnClickListener(this);

        if (ApplicationConfig.INSTANCE.DEBUG()) {
            totalTextView.setOnClickListener(this);
        }

        getGamesClient().registerConnectionCallbacks(new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                //showAlert("Connect", "Connected!");
                submitScore();
            }

            @Override
            public void onDisconnected() {
                showAlert("Connect", "Disconnected!");
            }
        });

        getGamesClient().connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        showRecord();

        submitScore();
    }

    int totalCount = 0;

    private void showRecord() {
        resultTextView.setText("");
        AppPreference preference = AppPreference.getInstance(getApplicationContext());
        String record = preference.getRecordJson();
        ArrayList<RecordItem> recordItems = preference.getRecordItems();
        int size = recordItems.size();
        int total = 0;
        for (int i = 0; i < size; i++) {
            RecordItem item = recordItems.get(i);
            total += item.getCount();
            resultTextView.append(item.getDate() + ":   " + item.getCount() + '\n');
        }

//        totalTextView.setText(String.valueOf(total));
        totalCount = total;
        doTotalCountIncreaseAnimation(total);
        //showResultText();

        daysTextView.setText(String.format(getString(R.string.how_many_days), appPreference.getHowManyDays()));
    }

    private static final int COUNTDOWN_ANIMATION_DURATION = 2000;

    private void doTotalCountIncreaseAnimation(final int total) {
        new CountDownTimer(COUNTDOWN_ANIMATION_DURATION, 10) {
            public void onTick(long millisUntilFinished) {
                long remain = COUNTDOWN_ANIMATION_DURATION - millisUntilFinished;
                int remainInt = (int) ((float) remain / (float) COUNTDOWN_ANIMATION_DURATION * total);
                totalTextView.setText(String.valueOf(remainInt));
            }

            public void onFinish() {
                totalTextView.setText(String.valueOf(total));
            }

        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.start_button: {
                doAnimation();
                handler.sendEmptyMessageDelayed(MSG_START_PROXIMITY, 300);
                break;
            }

            case R.id.share_textview: {
                final String text = String.format(getString(R.string.share_text_full_total), totalCount);
                final String filename = ScreenshotUtils.getshotFilePath();
                ScreenshotUtils.shotBitmap(MainActivity.this, filename);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Utils.share(MainActivity.this, MainActivity.this.getString(R.string.app_name), text, filename);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                shareTextView.startAnimation(animation);
                break;
            }

            case R.id.result_text: {
                showResultText();
                break;
            }

            case R.id.pushup_text: {
                showResultText();
                break;
            }

            case R.id.title: {
                doAnimation();
                handler.sendEmptyMessageDelayed(MSG_START_PROXIMITY, 300);
                break;
            }

            case R.id.icon: {
                doAnimation();
                handler.sendEmptyMessageDelayed(MSG_START_PROXIMITY, 300);
                break;
            }

            case R.id.total_text: {
                Intent intent = new Intent();
                intent.setClass(this, GameActivity.class);
                startActivity(intent);

                break;
            }

            case R.id.user_info: {
                Intent intent = new Intent();
                intent.setClass(this, GameActivity.class);
                startActivity(intent);
                Utils.overridePendingTransitionRight2Left((Activity) this);
                break;
            }
        }
    }

    private void showResultText() {
        if (resultTextView.isShown()) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    resultTextView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            resultTextView.startAnimation(animation);
        } else {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    resultTextView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            resultTextView.startAnimation(animation);
        }
    }

    private void startProximity() {
        Intent intent = new Intent();
        intent.setClass(this, ProximityActivity.class);
        startActivity(intent);
        Utils.overridePendingTransitionRight2Left((Activity) this);
    }

    private static final int MSG_START_PROXIMITY = 1;
    private static final int MSG_START_START_BUTTON_ANIMATION = MSG_START_PROXIMITY + 1;

    private Handler handler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {

            int what = msg.what;

            switch (what) {
                case MSG_START_PROXIMITY:
                    startProximity();
                    break;
                case MSG_START_START_BUTTON_ANIMATION:
                    doStartButtonAnimation();
                    break;
            }
        }
    };

    private void doStartButtonAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        animation.setRepeatCount(10000);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        startImageView.startAnimation(animation);
    }

    private void doAnimation() {
        doStartButtonAnimation();

    }

    private void doPushupTextAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_left);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //startProximity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        pushupTextView.startAnimation(animation);
    }

    private void doRecordTextAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //startProximity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        resultTextView.startAnimation(animation);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            bus.unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void updateCount(UpdateMsg updateMsg) {
        showRecord();
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

        if (totalDay >= 2) {
            getGamesClient().unlockAchievement(
                    getString(R.string.achievement_2days));
        }

        if (totalDay >= 10) {
            getGamesClient().unlockAchievement(
                    getString(R.string.achievement_10days));
        }

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
        }

        userInfo.setText(String.format(getString(R.string.you_are_signed_in_as), displayName));
        userIcon.setImageURI(uri);
    }
}
