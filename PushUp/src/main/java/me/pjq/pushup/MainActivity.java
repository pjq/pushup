package me.pjq.pushup;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {
    ImageView startImageView;
    TextView pushupTextView;
    TextView resultTextView;
    TextView totalTextView;

    Bus bus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bus = ServiceProvider.getBus();
        bus.register(this);

        startImageView = (ImageView) findViewById(R.id.start_button);
        pushupTextView = (TextView) findViewById(R.id.pushup_text);
        resultTextView = (TextView) findViewById(R.id.result_text);
        totalTextView = (TextView) findViewById(R.id.total_text);
        startImageView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        showRecord();
    }

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
        doTotalCountIncreaseAnimation(total);
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
            case R.id.start_button:
                doAnimation();
                handler.sendEmptyMessageDelayed(MSG_START_PROXIMITY, 300);
                break;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        bus.unregister(this);
    }

    @Subscribe
    public void updateCount(UpdateMsg updateMsg) {
        showRecord();
    }

}
