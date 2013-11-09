package me.pjq.pushup;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by pengjianqing on 11/8/13.
 */
public class ProximityActivity extends BaseFragmentActivity implements SensorEventListener, View.OnClickListener {
    private static final String TAG = ProximityActivity.class.getSimpleName();

    private SensorManager mgr;
    private Sensor proximity;
    private Vibrator vibrator;
    private float lastVal = -1;
    private TextView countTextView;
    private TextView tipsTextView;
    private TextView infoTextView;
    private TextView shareTextView;
    private ImageView refreshButton;
    private int count = 0;
    private long lastTime;
    private View titlebarIcon;
    private View titlebarText;

    private TextToSpeech tts;
    private boolean isTtsInited = false;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.proximity);

        countTextView = (TextView) findViewById(R.id.count_textview);
        refreshButton = (ImageView) findViewById(R.id.refresh_button);
        tipsTextView = (TextView) findViewById(R.id.tips_textview);
        infoTextView = (TextView) findViewById(R.id.info_textview);
        shareTextView = (TextView) findViewById(R.id.share_textview);
        titlebarIcon = (ImageView) findViewById(R.id.icon);
        titlebarText = (TextView) findViewById(R.id.title);

        refreshButton.setOnClickListener(this);
        titlebarIcon.setOnClickListener(this);
        titlebarText.setOnClickListener(this);
        shareTextView.setOnClickListener(this);

        this.mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        this.proximity = this.mgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE) {
                    } else {
                        isTtsInited = true;
                        countDownDelay(100);
                    }
                }
            }
        });
    }


    private boolean alreadyRegistered = false;
    private boolean alreadyCountDown = false;

    @Override
    protected void onResume() {
        super.onResume();

        if (!alreadyRegistered) {
            EFLogger.d(ProximityActivity.TAG, "registerListener...");

            mgr.registerListener(this, proximity,
                    SensorManager.SENSOR_DELAY_NORMAL);
            alreadyRegistered = true;
        }

        if (!alreadyCountDown) {

            alreadyCountDown = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float thisVal = event.values[0];
        EFLogger.d(ProximityActivity.TAG, "onSensorChanged...,thisVal=" + thisVal);
        if (this.lastVal == -1) {
            this.lastVal = thisVal;
        } else {
            if (thisVal < this.lastVal) {
                this.vibrator.vibrate(100);
                updateTips("Down");
            } else {
                if (increateCount()) {
                    this.vibrator.vibrate(500);
                    updateCount();
                    doCountTextViewAnimation();
                }

                updateTips("Up");
            }
            this.lastVal = thisVal;
        }
        String msg = "Current val: " + this.lastVal;
        EFLogger.d(ProximityActivity.TAG, msg);
    }

    private boolean increateCount() {
        long interval = System.currentTimeMillis() - lastTime;

        //not count down finished yet.
        if (countDown > 0) {
            return false;
        }

        if (interval < 10) {
            return false;
        }

        count++;
        lastTime = System.currentTimeMillis();

        return true;
    }

    private void updateCount() {
        updateCountText(String.valueOf(count));

        if (isTtsInited) {
            if (count == 5) {
                speak("Good Work!");
            } else if (count == 10) {
                speak("Excellent!");
            } else if (count == 20) {
                speak("Extremelly Excellent!");
            } else if (count == 30) {
                speak("God bless you!");
            }
        }
    }

    private void updateTips(String string) {
        if (countDown > 0) {
            return;
        }

        tipsTextView.setText(string);
    }

    private void updateInfo(String string) {
        infoTextView.setText(string);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        String name = sensor.getName();
        float range = sensor.getMaximumRange();
        EFLogger.d(ProximityActivity.TAG, "onAccuracyChanged...,accuracy=" + accuracy + ",name=" + name + ",range=" + range);
        if (ApplicationConfig.INSTANCE.DEBUG()) {
            updateInfo("accuracy=" + accuracy + ",name=" + name + ",range=" + range);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.refresh_button:
                count = 0;
                updateCount();
                break;

            case R.id.title:
                exit();
                break;

            case R.id.icon:
                exit();
                break;

            case R.id.share_textview:
                String text = String.format(getString(R.string.share_text_full), count);
                Utils.share(this, getString(R.string.app_name), text);
                break;

            default:
                break;
        }
    }

    private void exit() {
        finish();
        Utils.overridePendingTransitionLeft2Right(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            exit();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EFLogger.d(ProximityActivity.TAG, "unregisterListener...");
        mgr.unregisterListener(this, proximity);

        AppPreference.getInstance(getApplicationContext()).increate(count);

        tts.shutdown();
        tts = null;
    }

    private void doCountTextViewAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_short);
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
        countTextView.startAnimation(animation);
    }

    private static final int MSG_COUNT_DOWN = 1;
    private static final int MSG_START_COUNT_DOWN = MSG_COUNT_DOWN + 1;
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            int what = msg.what;

            switch (what) {
                case MSG_COUNT_DOWN:
//                    countDown();
                    countDown = countDown - 1;
                    if (0 == countDown) {
                        updateCountText("GO");
                    } else {
                        updateCountText(String.valueOf(countDown));
                    }
                    break;

                case MSG_START_COUNT_DOWN:
                    countDownStart();
                    break;
            }
        }
    };


    int countDown = 3;

    private void countDownDelay(long delay) {
        handler.sendEmptyMessageDelayed(MSG_START_COUNT_DOWN, delay);
    }

    private void countDownStart() {
        updateCountText("" + countDown);
        handler.sendEmptyMessageDelayed(MSG_COUNT_DOWN, 1000);
        handler.sendEmptyMessageDelayed(MSG_COUNT_DOWN, 2000);
        handler.sendEmptyMessageDelayed(MSG_COUNT_DOWN, 3000);
    }

    private void updateCountText(String text) {
        countTextView.setText(text);
        doCountTextViewAnimation();
        speak(text);
    }

    private void countDown() {
        new CountDownTimer(countDown * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                EFLogger.i(TAG, "" + millisUntilFinished);
                int count = 0;
                if (3800 <= millisUntilFinished && millisUntilFinished <= 4200) {
                    count = 4;
                }
                if (2800 <= millisUntilFinished && millisUntilFinished <= 3200) {
                    count = 3;
                } else if (1800 <= millisUntilFinished && millisUntilFinished <= 2200) {
                    count = 2;
                } else if (800 <= millisUntilFinished && millisUntilFinished <= 1200) {
                    count = 1;
                } else if (millisUntilFinished <= 200) {
                    count = 0;
                }
                updateCountText("" + (count - 1));
                doCountTextViewAnimation();
            }

            public void onFinish() {
                updateCountText("Start");
            }

        }.start();
    }

    private void speak(String text) {
        if (isTtsInited) {
            tts.speak(text, TextToSpeech.QUEUE_ADD,
                    null);
        }
    }
}
