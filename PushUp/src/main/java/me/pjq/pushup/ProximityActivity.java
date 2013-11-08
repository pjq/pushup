package me.pjq.pushup;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    private ImageView refreshButton;
    private int count = 0;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.proximity);

        countTextView = (TextView) findViewById(R.id.count_textview);
        refreshButton = (ImageView) findViewById(R.id.refresh_button);

        refreshButton.setOnClickListener(this);

        this.mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        this.proximity = this.mgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
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
        EFLogger.d(ProximityActivity.TAG, "onSensorChanged...");
        // 目前的距离
        float thisVal = event.values[0];
        if (this.lastVal == -1) {
            // 第一次进来
            this.lastVal = thisVal;
        } else {
            if (thisVal < this.lastVal) {
                // 接近长振动
                this.vibrator.vibrate(1000);
            } else {
                // 离开短振动
                this.vibrator.vibrate(100);
                count++;
                updateCount();

            }
            this.lastVal = thisVal;
        }
        String msg = "Current val: " + this.lastVal;
        EFLogger.d(ProximityActivity.TAG, msg);
    }

    private void updateCount() {
        countTextView.setText(String.valueOf(count));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.refresh_button:
                count = 0;
                updateCount();
                break;

            default:
                break;
        }
    }
}
