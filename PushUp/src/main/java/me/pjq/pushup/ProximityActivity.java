package me.pjq.pushup;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

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
        titlebarIcon = (ImageView) findViewById(R.id.icon);
        titlebarText = (TextView) findViewById(R.id.title);

        refreshButton.setOnClickListener(this);
        titlebarIcon.setOnClickListener(this);
        titlebarText.setOnClickListener(this);

        this.mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        this.proximity = this.mgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        updateCount();


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE) {
                    } else {
                        isTtsInited = true;
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        EFLogger.d(ProximityActivity.TAG, "registerListener...");
        mgr.registerListener(this, proximity,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EFLogger.d(ProximityActivity.TAG, "unregisterListener...");
        mgr.unregisterListener(this, proximity);
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

        if (interval < 400) {
            return false;
        }

        count++;
        lastTime = System.currentTimeMillis();

        return true;
    }

    private void updateCount() {
        countTextView.setText(String.valueOf(count));

        if (isTtsInited) {
            tts.speak(String.valueOf(count), TextToSpeech.QUEUE_ADD,
                    null);
        }
    }

    private void updateTips(String string) {
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
                break;

            case R.id.icon:
                finish();
                Utils.overridePendingTransitionLeft2Right(this);

                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            Utils.overridePendingTransitionLeft2Right(this);
        }

        return super.onKeyDown(keyCode, event);
    }
}
