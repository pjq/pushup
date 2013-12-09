
package me.pjq.pushup.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.games.Player;
import me.pjq.pushup.AppPreference;
import me.pjq.pushup.R;
import me.pjq.pushup.utils.TitlebarHelper;

public class GameBoardFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = GameBoardFragment.class.getSimpleName();
    private Button archievementButton;
    private Button leaderboardButton;
    private Button invitationButton;

    private TitlebarHelper titlebarHelper;
    private FragmentController fragmentController;
    private AppPreference appPreference;

    private TextView userInfo;
    private ImageView userIcon;
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
        appPreference = AppPreference.getInstance(getApplicationContext());
        archievementButton = (Button) view.findViewById(R.id.button_archievement);
        leaderboardButton = (Button) view.findViewById(R.id.button_leaderboard);
        invitationButton = (Button) view.findViewById(R.id.button_invitation);

        userIcon = (ImageView) view.findViewById(R.id.user_icon);
        userInfo = (TextView) view.findViewById(R.id.user_info);

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
        invitationButton.setOnClickListener(this);

        view.findViewById(R.id.button_sign_in).setOnClickListener(this);
        view.findViewById(R.id.button_sign_out).setOnClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentController = (FragmentController) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (fragmentController.isSignedInPublic()) {
            onSignInSucceeded();
        } else {
            showSignInBar();
        }
    }

    // Shows the "sign in" bar (explanation and button).
    private void showSignInBar() {
        view.findViewById(R.id.sign_in_bar).setVisibility(View.VISIBLE);
        view.findViewById(R.id.sign_out_bar).setVisibility(View.GONE);
    }

    // Shows the "sign out" bar (explanation and button).
    private void showSignOutBar() {
        view.findViewById(R.id.sign_in_bar).setVisibility(View.GONE);
        view.findViewById(R.id.sign_out_bar).setVisibility(View.VISIBLE);
    }

    public void onSignInFailed() {
        // Sign-in has failed. So show the user the sign-in button
        // so they can click the "Sign-in" button.
        showSignInBar();
    }

    /**
     * Called to notify us that sign in succeeded. We react by loading the loot from the
     * cloud and updating the UI to show a sign-out button.
     */
    public void onSignInSucceeded() {
        // Sign-in worked!
        showSignOutBar();

        playerInfo();
    }

    private void playerInfo() {
        Player player = fragmentController.getGamesClientPublic().getCurrentPlayer();
        String name = player.getDisplayName();
        Uri uri = player.getIconImageUri();

        String displayName;
        if (player == null) {
            Log.w(TAG, "mGamesClient.getCurrentPlayer() is NULL!");
            displayName = "???";
        } else {
            displayName = player.getDisplayName();
            appPreference.setLoginName(displayName);
        }


        userInfo.setText(String.format(getString(R.string.you_are_signed_in_as), displayName));
        userIcon.setImageURI(uri);
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

            case R.id.button_invitation:
                onShowInvitationRequested();
                break;

            case R.id.button_sign_in:
                // start the sign-in flow
                fragmentController.beginUserInitiatedSignInImpl();
                break;

            case R.id.button_sign_out:
                // sign out.
                fragmentController.signOutImpl();
                showSignInBar();
                break;
        }
    }

    // request codes we use when invoking an external activity
    final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

    public void onShowAchievementsRequested() {
        fragmentController.onShowAchievementsRequested();
    }

    public void onShowLeaderboardsRequested() {
        fragmentController.onShowLeaderboardsRequested();
    }

    public void onShowInvitationRequested() {
        fragmentController.onShowInvitationRequested();
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
