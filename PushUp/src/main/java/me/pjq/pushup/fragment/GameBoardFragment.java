
package me.pjq.pushup.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import me.pjq.pushup.R;
import me.pjq.pushup.utils.TitlebarHelper;

public class GameBoardFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = GameBoardFragment.class.getSimpleName();
    private Button archievementButton;
    private Button leaderboardButton;

    private TitlebarHelper titlebarHelper;
    private FragmentController fragmentController;

    private View view;

    public static GameBoardFragment newInstance(Bundle bundle) {
        GameBoardFragment fragment = new GameBoardFragment();

        if (null != bundle) {
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    protected View onGetFragmentView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.game_fragment, null);

        return view;
    }

    @Override
    protected void ensureUi() {
        archievementButton = (Button) view.findViewById(R.id.button_archievement);
        leaderboardButton = (Button) view.findViewById(R.id.button_leaderboard);

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

        leaderboardButton.setOnClickListener(this);
        archievementButton.setOnClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentController = (FragmentController) getActivity();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_archievement:
                onShowAchievementsRequested();
                break;

            case R.id.button_leaderboard:
                onShowLeaderboardsRequested();
                break;
        }
    }

    // request codes we use when invoking an external activity
    final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

    public void onShowAchievementsRequested() {
        if (fragmentController.isSignedInPublic()) {
            getActivity().startActivityForResult(fragmentController.getGamesClientPublic().getAchievementsIntent(), RC_UNUSED);
        } else {
            fragmentController.showAlertPublic(getString(R.string.achievements_not_available));
        }
    }

    public void onShowLeaderboardsRequested() {
        if (fragmentController.isSignedInPublic()) {
            getActivity().startActivityForResult(fragmentController.getGamesClientPublic().getAllLeaderboardsIntent(), RC_UNUSED);
        } else {
            fragmentController.showAlertPublic(getString(R.string.leaderboards_not_available));
        }
    }

    @Override
    public void changeToFragment(String tag) {
        if (tag.equalsIgnoreCase(TAG)) {
            onResume();
        } else {
            onPause();
        }
    }

//    private void exit() {
//        finish();
//        Utils.overridePendingTransitionLeft2Right(this);
//    }

}
