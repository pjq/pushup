package me.pjq.pushup.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.internal.fo;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import me.pjq.pushup.*;
import me.pjq.pushup.lan.LanPlayer;
import me.pjq.pushup.lan.LanPlayerHelper;
import me.pjq.pushup.lan.MsgUpdatePlayer;
import me.pjq.pushup.utils.TitlebarHelper;
import me.pjq.pushup.utils.Utils;

import java.util.ArrayList;

/**
 * Created by pjq on 5/26/13.
 */
public class LanGameFragment extends BaseFragment implements View.OnClickListener, SensorEventListener {
    public static final String TAG = LanGameFragment.class.getSimpleName();

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

    public static LanGameFragment newInstance(Bundle bundle) {
        LanGameFragment fragment = new LanGameFragment();

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

        refreshButton.setOnClickListener(this);
        shareTextView.setOnClickListener(this);
        countTextView.setOnClickListener(this);

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
        playerTextView.setVisibility(View.INVISIBLE);
        String text = player.getUsername();
        if (!player.getScore().equalsIgnoreCase("0")) {
            text += ": " + player.getScore();
        }

        playerTextView.setText(text);
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
                    this.vibrator.vibrate(500);
                    updateCount();
                    doCountTextViewAnimation();
                }

                updateTips(R.drawable.up);
            }
            this.lastVal = thisVal;
        }
        String msg = "Current val: " + this.lastVal;
        EFLogger.d(TAG, msg);
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

        String msg = "[Count:" + count + "]";
        MyApplication.getPeersMgr().sendMessage(msg);

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

            default:
                break;
        }
    }

    private void exit() {
        if (count > 0) {
            AppPreference.getInstance(getApplicationContext()).increate(count);
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
        bus.unregister(this);
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
                AppPreference.getInstance(getApplicationContext()).increate(count);
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
