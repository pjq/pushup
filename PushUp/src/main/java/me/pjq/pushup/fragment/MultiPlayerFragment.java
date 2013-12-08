package me.pjq.pushup.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import me.pjq.pushup.AppPreference;
import me.pjq.pushup.ApplicationConfig;
import me.pjq.pushup.EFLogger;
import me.pjq.pushup.MyApplication;
import me.pjq.pushup.R;
import me.pjq.pushup.ScreenshotUtils;
import me.pjq.pushup.ServiceProvider;
import me.pjq.pushup.SpeakerUtil;
import me.pjq.pushup.lan.LanPlayer;
import me.pjq.pushup.lan.LanPlayerHelper;
import me.pjq.pushup.lan.MsgUpdatePlayer;
import me.pjq.pushup.utils.TitlebarHelper;
import me.pjq.pushup.utils.Utils;

import java.util.ArrayList;

/**
 * Created by pjq on 11/26/13.
 */
public class MultiPlayerFragment extends BaseFragment implements View.OnClickListener, SensorEventListener {
    public static final String TAG = MultiPlayerFragment.class.getSimpleName();

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
    private SpeakerUtil speakerUtil;

    private View view;

    private FragmentController fragmentController;
    private TitlebarHelper titlebarHelper;

    private View playerLayout;
    private TextView player1TextView;
    private TextView player2TextView;
    private TextView player3TextView;
    private TextView player4TextView;

    private Bus bus;

    public static MultiPlayerFragment newInstance(Bundle bundle) {
        MultiPlayerFragment fragment = new MultiPlayerFragment();

        if (null != bundle) {
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    protected void ensureUi() {
        onMyResume();

        countTextView = (TextView) view.findViewById(R.id.count_textview);
        refreshButton = (ImageView) view.findViewById(R.id.refresh_button);
        tipsTextView = (TextView) view.findViewById(R.id.tips_textview);
        infoTextView = (TextView) view.findViewById(R.id.info_textview);
        shareTextView = (TextView) view.findViewById(R.id.share_textview);

        player1TextView = (TextView) view.findViewById(R.id.player1);
        player2TextView = (TextView) view.findViewById(R.id.player2);
        player3TextView = (TextView) view.findViewById(R.id.player3);
        player4TextView = (TextView) view.findViewById(R.id.player4);

//        player1TextView.setVisibility(View.GONE);
//        player2TextView.setVisibility(View.GONE);
//        player3TextView.setVisibility(View.GONE);
//        player4TextView.setVisibility(View.GONE);

        refreshButton.setOnClickListener(this);
        shareTextView.setOnClickListener(this);
        countTextView.setOnClickListener(this);
        tipsTextView.setOnClickListener(this);

        titlebarHelper = new TitlebarHelper(view, new TitlebarHelper.OnTitlebarClickListener() {
            @Override
            public void onClickIcon() {
                fragmentController.showFragment(DashboardFragment.TAG);
            }

            @Override
            public void onClickTitle() {
                fragmentController.showFragment(DashboardFragment.TAG);
            }
        });

        shareTextView.setVisibility(View.GONE);

        updatePlayerInfoUI();
        updatePlayerInfo();
    }


    boolean enableAccel = false;

    private void registerSensorListener() {
        if (!enableAccel){
            return;
        }

        SensorManager sensorMgr = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMgr.registerListener(mSensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unRegisterSensorListener() {
        if (!enableAccel){
            return;
        }

        SensorManager sensorMgr = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);

        if (null != mSensorEventListener) {
            sensorMgr.unregisterListener(mSensorEventListener);
            mSensorEventListener = null;
        }
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        private RotateSession session = new RotateSession();

        public void onSensorChanged(SensorEvent e) {
            if (!isShow) {
                return;
            }

            float x = e.values[0];
            float y = e.values[1];
            float z = e.values[2];

            EFLogger.i(TAG, "onSensorChanged,x=" + x + ",y=" + y + ",z=" + z);

            boolean isStop = false;
            int zMinValue = -8;

            session = session.value(z);

            if (session.isSessionFinished()) {
                increaseCountEvent();
                session.reset();
            }


//            if (x < 1 && x > -1 && y < 1 && y > -1) {
//                if (z <= zMinValue) {
//                    isStop = true;
//                    increaseCountEvent();
//                }
//            } else {
//                if (z <= zMinValue) {
//                    isStop = true;
//                    increaseCountEvent();
//                }
//            }
        }

        public void onAccuracyChanged(Sensor s, int accuracy) {

        }
    };

    private class RotateSession {
        private boolean value7_8 = false;
        private boolean value4_5 = false;
        private boolean value0_1 = false;
        private boolean value_1_0 = false;
        private boolean value_5_4 = false;
        private boolean value_8_7 = false;

        public RotateSession value(float val) {
            if (4 <= val && val <= 8) {
                value7_8 = true;
            }

            if (value7_8) {
                if (2 <= val && val <= 4) {
                    value4_5 = true;
                }
            }

            if (0 <= val && val <= 2) {
                value0_1 = true;
            }

            if (value7_8) {
                if (-2 <= val && val <= 0) {
                    value_1_0 = true;
                }
            }

            if (-4 <= val && val <= -2) {
                value_5_4 = true;
            }

            if (value_1_0) {
                if (-8 <= val && val <= -4) {
                    value_8_7 = true;
                }
            }

            return this;
        }

        public boolean isSessionFinished() {
            if (value7_8 && value4_5 && value0_1 && value_1_0 && value_5_4 && value_8_7) {
                return true;
            } else {
                return false;
            }
        }

        public RotateSession reset() {
            value7_8 = false;
            value0_1 = false;
            value_1_0 = false;
            value_5_4 = false;
            value_8_7 = false;

            return this;
        }

    }

    private void updatePlayerInfoUI() {
        ArrayList<Integer> colors = Utils.randomColor();

        updatePlayerUI(player1TextView, colors.get(0));
        updatePlayerUI(player2TextView, colors.get(1));
        updatePlayerUI(player3TextView, colors.get(2));
        updatePlayerUI(player4TextView, colors.get(3));
    }

    private void updatePlayerUI(TextView playerTextView, Integer color) {
        Resources resource = (Resources) getApplicationContext().getResources();
//        playerTextView.setTextColor(resource.getColorStateList(color));

        playerTextView.setBackgroundResource(color);
    }

    private void updatePlayerInfo() {
        ArrayList<LanPlayer> players = LanPlayerHelper.getLanPlayers();

        if (players == null || players.size() == 0) {
//            player1TextView.setVisibility(View.GONE);
//            player2TextView.setVisibility(View.GONE);
//            player3TextView.setVisibility(View.GONE);
//            player4TextView.setVisibility(View.GONE);

            return;
        }

        int size = players.size();

        for (int i = 0; i < size; i++) {
            LanPlayer player = players.get(i);

            if (i == 0) {
                updatePlayer(player1TextView, player);
            } else if (1 == i) {
                updatePlayer(player2TextView, player);
            } else if (2 == i) {
                updatePlayer(player3TextView, player);
            } else if (3 == i) {
                updatePlayer(player4TextView, player);
            }
        }
    }

    private void updatePlayer(TextView playerTextView, LanPlayer player) {
        playerTextView.setVisibility(View.VISIBLE);
        String text = player.getUsername();
        if (!player.getScore().equalsIgnoreCase("0")) {
            text += "\n" + player.getScore();
        }

        String prevtext = playerTextView.getText().toString();
        if (!text.equalsIgnoreCase(prevtext)) {
            doAnimation(playerTextView, null);
        }

        Log.i(TAG, "updatePlayer:" + text);
        playerTextView.setText(text);
    }

    private void doAnimation(View view, Animation.AnimationListener listener) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_in);
        if (null != listener) {
            animation.setAnimationListener(listener);
        }
        view.startAnimation(animation);
    }

    @Override
    protected View onGetFragmentView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.lan_game_fragment, null);

        return view;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        speakerUtil = SpeakerUtil.getInstance(getApplicationContext());
        fragmentController = (FragmentController) getActivity();

        bus = ServiceProvider.getBus();
        bus.register(this);


        this.mgr = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        this.proximity = this.mgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        registerSensorListener();
    }


    private boolean alreadyRegistered = false;
    private boolean alreadyStartCountDown = false;

    public void onMyResume() {
        isShow = true;
        if (!alreadyRegistered) {
            EFLogger.d(TAG, "registerListener...");

            mgr.registerListener(this, proximity,
                    SensorManager.SENSOR_DELAY_NORMAL);
            alreadyRegistered = true;
        }

        if (!alreadyStartCountDown) {
            countDownDelay(1500);
            alreadyStartCountDown = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isShow) {
            return;
        }

        float thisVal = event.values[0];
        EFLogger.d(TAG, "onSensorChanged...,thisVal=" + thisVal);
        if (this.lastVal == -1) {
            this.lastVal = thisVal;
        } else {
            if (thisVal < this.lastVal) {
                this.vibrator.vibrate(100);
                updateTips(R.drawable.down);
            } else {
                if (increateCount()) {
                    increaseCountEvent();
                }

                updateTips(R.drawable.up);
            }
            this.lastVal = thisVal;
        }
        String msg = "Current val: " + this.lastVal;
        EFLogger.d(TAG, msg);
    }

    private void increaseCountEvent() {
        count++;
        this.vibrator.vibrate(500);
        updateCount();
        doCountTextViewAnimation();
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

        lastTime = System.currentTimeMillis();

        return true;
    }

    private boolean canStart() {
        if (countDown > 0) {
            return false;
        } else {
            return true;
        }
    }

    private void updateCount() {
        updateCountText(String.valueOf(count));

        MyApplication.getPeersMgr().sendCount(count);

        if (speakerUtil.isTtsInited()) {
            if (count == 10) {
                speak("Good Work!");
            } else if (count == 20) {
                speak("Excellent!");
            } else if (count == 30) {
                speak("Extremelly Excellent!");
            } else if (count == 40) {
                speak("God bless you!");
            } else if (count == 50) {
                speak("You are the God!");
            } else if (count == 80) {
                speak("You create the God!");
            }
        }
    }

    private void updateTips(int resid) {
        if (countDown > 0) {
            return;
        }

        tipsTextView.setBackgroundResource(resid);
    }

    private void updateInfo(String string) {
        infoTextView.setText(string);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        String name = sensor.getName();
        float range = sensor.getMaximumRange();
        EFLogger.d(TAG, "onAccuracyChanged...,accuracy=" + accuracy + ",name=" + name + ",range=" + range);
        if (ApplicationConfig.INSTANCE.DEBUG()) {
            //updateInfo("accuracy=" + accuracy + ",name=" + name + ",range=" + range);
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

            case R.id.count_textview:
                count = 0;
                updateCount();
                break;

            case R.id.share_textview:
                final String text = String.format(getString(R.string.share_text_full), count);
                final String filename = ScreenshotUtils.getshotFilePath();
                ScreenshotUtils.shotBitmap(getActivity(), filename);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Utils.share(getActivity(), getString(R.string.app_name), text, filename);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                shareTextView.startAnimation(animation);

                break;

            case R.id.tips_textview: {
                count++;
                updateCount();

                break;
            }

            default:
                break;
        }
    }

    private void exit() {
        if (count > 0) {
            AppPreference.getInstance(getApplicationContext()).increase(count);
        }

        handler.removeMessages(MSG_COUNT_DOWN);

        Utils.sendUpdateMsg();
        getActivity().finish();
        Utils.overridePendingTransitionLeft2Right(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EFLogger.d(TAG, "unregisterListener...");
        mgr.unregisterListener(this, proximity);

        unRegisterSensorListener();

        try {
            bus.unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doCountTextViewAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
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
        countTextView.startAnimation(animation);
    }

    private static final int MSG_COUNT_DOWN = 1;
    private static final int MSG_START_COUNT_DOWN = MSG_COUNT_DOWN + 1;
    private static final int MSG_UPDATE_PLAYER_INFO = MSG_START_COUNT_DOWN + 1;

    private Handler handler = new Handler() {
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

                case MSG_UPDATE_PLAYER_INFO:
                    updatePlayerInfo();

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
        speakerUtil.speak(text);
    }


    private boolean isShow;

    @Override
    public void changeToFragment(String tag) {
        if (tag.equalsIgnoreCase(TAG)) {
            onMyResume();
            isShow = true;
        } else {
            isShow = false;
            onPause();
            alreadyRegistered = false;
            alreadyStartCountDown = false;
            countDown = 3;
            if (count > 0) {
                AppPreference.getInstance(getApplicationContext()).increase(count);
            }

            handler.removeMessages(MSG_COUNT_DOWN);

            Utils.sendUpdateMsg();

            count = 0;
        }
    }

    @Subscribe
    public void onUpdatePlayerInfo(MsgUpdatePlayer updatePlayer) {
        handler.sendEmptyMessage(MSG_UPDATE_PLAYER_INFO);
    }
}
