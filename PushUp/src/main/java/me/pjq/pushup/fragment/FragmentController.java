package me.pjq.pushup.fragment;

import com.google.android.gms.games.GamesClient;

/**
 * Created by pjq on 11/13/13.
 */
public interface FragmentController {
    void showFragment(String tag);

    GamesClient getGamesClientPublic();

    boolean isSignedInPublic();

    void showAlertPublic(String string);

    void onShowAchievementsRequested();

    void onShowLeaderboardsRequested();

    void onShowInvitationRequested();

    void beginUserInitiatedSignInImpl();

    void signOutImpl();
}
