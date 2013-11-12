/* Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.pjq.pushup;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.Player;
import com.google.android.gms.internal.di;
import com.google.android.gms.internal.ga;
import com.google.example.games.basegameutils.BaseGameActivity;

/**
 * Trivial quest. A sample game that sets up the Google Play game services
 * API and allows the user to click a button to win (yes, incredibly epic).
 * Even though winning the game is fun, the purpose of this sample is to
 * illustrate the simplest possible game that uses the API.
 *
 * @author Bruno Oliveira (Google)
 */
public class GameActivity extends BaseGameActivity implements View.OnClickListener {
    private static boolean DEBUG_ENABLED = true;
    private static final String TAG = "TrivialQuest";
    private TextView userInfo;
    private ImageView userIcon;
    private Button archievementButton;
    private Button leaderboardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableDebugLog(DEBUG_ENABLED, TAG);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_activity_main);
        findViewById(R.id.button_sign_in).setOnClickListener(this);
        findViewById(R.id.button_sign_out).setOnClickListener(this);
        findViewById(R.id.button_win).setOnClickListener(this);

        userIcon = (ImageView) findViewById(R.id.user_icon);
        userInfo = (TextView) findViewById(R.id.user_info);
        archievementButton = (Button) findViewById(R.id.button_archievement);
        leaderboardButton = (Button) findViewById(R.id.button_leaderboard);

        archievementButton.setOnClickListener(this);
        leaderboardButton.setOnClickListener(this);

        getGamesClient().registerConnectionCallbacks(new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                showAlert("Connect", "Connected!");
                getGamesClient().unlockAchievement(
                        getString(R.string.achievement_activate));
                getGamesClient().submitScore(getString(R.string.leaderboard_total_pushups), AppPreference.getInstance(getApplicationContext()).getTotalNumber());
            }

            @Override
            public void onDisconnected() {
                showAlert("Connect", "Disconnected!");
            }
        });

        getGamesClient().connect();
    }

    // Shows the "sign in" bar (explanation and button).
    private void showSignInBar() {
        findViewById(R.id.sign_in_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_bar).setVisibility(View.GONE);
    }

    // Shows the "sign out" bar (explanation and button).
    private void showSignOutBar() {
        findViewById(R.id.sign_in_bar).setVisibility(View.GONE);
        findViewById(R.id.sign_out_bar).setVisibility(View.VISIBLE);
    }

    /**
     * Called to notify us that sign in failed. Notice that a failure in sign in is not
     * necessarily due to an error; it might be that the user never signed in, so our
     * attempt to automatically sign in fails because the user has not gone through
     * the authorization flow. So our reaction to sign in failure is to show the sign in
     * button. When the user clicks that button, the sign in process will start/resume.
     */
    @Override
    public void onSignInFailed() {
        // Sign-in has failed. So show the user the sign-in button
        // so they can click the "Sign-in" button.
        showSignInBar();
    }

    /**
     * Called to notify us that sign in succeeded. We react by loading the loot from the
     * cloud and updating the UI to show a sign-out button.
     */
    @Override
    public void onSignInSucceeded() {
        // Sign-in worked!
        showSignOutBar();

        playerInfo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_sign_in:
                // Check to see the developer who's running this sample code read the instructions :-)
                // NOTE: this check is here only because this is a sample! Don't include this
                // check in your actual production app.
                if (!verifyPlaceholderIdsReplaced()) {
                    showAlert("Error", "Sample not correctly set up. See README!");
                    break;
                }

                // start the sign-in flow
                beginUserInitiatedSignIn();
                break;
            case R.id.button_sign_out:
                // sign out.
                signOut();
                showSignInBar();
                break;

            case R.id.button_archievement:
                onShowAchievementsRequested();
                break;

            case R.id.button_leaderboard:
                onShowLeaderboardsRequested();
                break;

            case R.id.button_win:
                // win!
                showAlert(getString(R.string.victory), getString(R.string.you_won));
                if (getGamesClient().isConnected()) {
                    // unlock the "Trivial Victory" achievement.
                }


                break;
        }
    }

    /**
     * Checks that the developer (that's you!) read the instructions.
     * <p/>
     * IMPORTANT: a method like this SHOULD NOT EXIST in your production app!
     * It merely exists here to check that anyone running THIS PARTICULAR SAMPLE
     * did what they were supposed to in order for the sample to work.
     */
    boolean verifyPlaceholderIdsReplaced() {
        if (true) {
            return true;
        }

        final boolean CHECK_PKGNAME = true; // set to false to disable check (not recommended!)

        // Did the developer forget to change the package name?
        if (CHECK_PKGNAME && getPackageName().startsWith("com.google.example.")) return false;

        // Did the developer forget to replace a placeholder ID?
        int res_ids[] = new int[]{
                R.string.app_id, R.string.achievement_activate
        };

        for (int i : res_ids) {
            if (getString(i).equalsIgnoreCase("ReplaceMe")) return false;
        }
        return true;
    }

    private void playerInfo() {
        Player player = getGamesClient().getCurrentPlayer();
        String name = player.getDisplayName();
        Uri uri = player.getIconImageUri();

        String displayName;
        if (player == null) {
            Log.w(TAG, "mGamesClient.getCurrentPlayer() is NULL!");
            displayName = "???";
        } else {
            displayName = player.getDisplayName();
        }

        userInfo.setText(String.format(getString(R.string.you_are_signed_in_as), displayName));
        userIcon.setImageURI(uri);
    }

    private void playerList() {
        Game game = getGamesClient().getCurrentGame();

    }

    // request codes we use when invoking an external activity
    final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

    public void onShowAchievementsRequested() {
        if (isSignedIn()) {
            startActivityForResult(getGamesClient().getAchievementsIntent(), RC_UNUSED);
        } else {
            showAlert(getString(R.string.achievements_not_available));
        }
    }

    public void onShowLeaderboardsRequested() {
        if (isSignedIn()) {
            startActivityForResult(getGamesClient().getAllLeaderboardsIntent(), RC_UNUSED);
        } else {
            showAlert(getString(R.string.leaderboards_not_available));
        }
    }
}
