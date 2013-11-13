package me.pjq.pushup.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.google.android.gms.games.GamesClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import me.pjq.pushup.*;
import me.pjq.pushup.utils.TitlebarHelper;
import me.pjq.pushup.utils.Utils;

import java.util.ArrayList;

/**
 * Created by pengjianqing on 11/13/13.
 */
public class DashboardFragment extends BaseFragment implements View.OnClickListener {
    View view;

    public static final String TAG = DashboardFragment.class.getSimpleName();

    TextView startImageView;
    TextView pushupTextView;
    TextView resultTextView;
    TextView totalTextView;
    TextView daysTextView;
    TextView durationTextView;
    TextView levelTextView;

    Bus bus;

    AppPreference appPreference;
    FragmentController fragmentController;
    private TitlebarHelper titlebarHelper;

    public static DashboardFragment newInstance(Bundle bundle) {
        DashboardFragment fragment = new DashboardFragment();

        if (null != bundle) {
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    protected View onGetFragmentView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.dashboard_fragment, null);

        return view;
    }

    @Override
    protected void ensureUi() {
        startImageView = (TextView) view.findViewById(R.id.start_button);
        pushupTextView = (TextView) view.findViewById(R.id.pushup_text);
        resultTextView = (TextView) view.findViewById(R.id.result_text);
        totalTextView = (TextView) view.findViewById(R.id.total_text);
        daysTextView = (TextView) view.findViewById(R.id.days);
        durationTextView = (TextView) view.findViewById(R.id.duration_time);
        levelTextView = (TextView) view.findViewById(R.id.level);

        startImageView.setOnClickListener(this);
        resultTextView.setOnClickListener(this);
        pushupTextView.setOnClickListener(this);

        titlebarHelper = new TitlebarHelper(view, new TitlebarHelper.OnTitlebarClickListener() {
            @Override
            public void onClickIcon() {
                fragmentController.showFragment(ProximityFragment.TAG);
            }

            @Override
            public void onClickTitle() {
                fragmentController.showFragment(ProximityFragment.TAG);
            }
        });

        if (ApplicationConfig.INSTANCE.DEBUG()) {
            totalTextView.setOnClickListener(this);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bus = ServiceProvider.getBus();
        bus.register(this);

        appPreference = AppPreference.getInstance(getApplicationContext());

        fragmentController = (FragmentController) getActivity();
    }

    private GamesClient getGamesClient() {
        return fragmentController.getGamesClientPublic();
    }

    @Override
    public void onResume() {
        super.onResume();

        showRecord();
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
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.start_button: {
                fragmentController.showFragment(ProximityFragment.TAG);

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

//            case R.id.total_text: {
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), GameActivity.class);
//                startActivity(intent);
//
//                break;
//            }

            case R.id.user_info: {
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), GameActivity.class);
//                startActivity(intent);
//                Utils.overridePendingTransitionRight2Left(getActivity());
                fragmentController.showFragment(GameBoardFragment.TAG);
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
        intent.setClass(getActivity(), ProximityActivity.class);
        startActivity(intent);
        Utils.overridePendingTransitionRight2Left((Activity) getActivity());
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
    public void onDestroy() {
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

    @Override
    public void changeToFragment(String tag) {
        if (tag.equalsIgnoreCase(TAG)) {
            onResume();
        } else {

        }
    }
}
