package me.pjq.pushup;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {
    ImageView startImageView;
    TextView pushupTextView;
    TextView resultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startImageView = (ImageView) findViewById(R.id.start_button);
        pushupTextView = (TextView) findViewById(R.id.pushup_text);
        resultTextView = (TextView) findViewById(R.id.result_text);
        startImageView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        showRecord();
    }

    private void showRecord() {
        AppPreference preference = AppPreference.getInstance(getApplicationContext());
        String record = preference.getRecordJson();
        resultTextView.setText(record);
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
}
