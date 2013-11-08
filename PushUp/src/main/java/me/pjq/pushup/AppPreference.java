
package me.pjq.pushup;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Preference manager
 *
 * @author pengjianqing
 */
public class AppPreference {
    private static final String TAG = AppPreference.class.getSimpleName();
    private static AppPreference mInstance;

    private static final String APP_PREFERENCE_FILENAME = "app_settings";

    private static final String USER_PREFERENCE_FILENAME = "user_settings";

    private static final String APP_PREFERENCE_COOKIE = "cookie";
    private static final String APP_PREFERENCE_LOGIN_NAME = "loginName";
    private static final String APP_PREFERENCE_LOGIN_PASSWORD = "loginPassword";
    private static final String APP_PREFERENCE_LANGUAGE = "lang";
    private static final String APP_PREFERENCE_LANGUAGE_ALREADY_CHANGED_MANUALLY = "lang_changed";
    private static final String APP_PREFERENCE_SYNCTIME = "synctime";
    private static final String APP_PREFERENCE_SETTING_SHOW_NOTIFICATION = "setting_show_notification";
    private static final String APP_PREFERENCE_SETTING_PLAY_AUDIO_AUTO = "setting_play_audiio_auto";
    private static final String APP_PREFERENCE_SETTING_SHOW_PICTURE = "setting_show_picture";
    private static final String APP_PREFERENCE_SETTING_VOLUME = "setting_volume";

    /**
     * application related preference
     */
    private SharedPreferences appPreference;

    /*
     * user related preference
     */
    private SharedPreferences userPreference;
    private String cookie;
    private Object volume;

    public AppPreference(Context context) {
        appPreference = context
                .getSharedPreferences(APP_PREFERENCE_FILENAME, Activity.MODE_PRIVATE);
        userPreference = context.getSharedPreferences(USER_PREFERENCE_FILENAME,
                Activity.MODE_PRIVATE);
    }

    public static AppPreference getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new AppPreference(context);
        }

        return mInstance;
    }

    private boolean set(SharedPreferences preferences, String key, String value) {
        Editor editor = preferences.edit();
        editor.putString(key, value);

        return editor.commit();
    }

    private boolean set(SharedPreferences preferences, String key, boolean value) {
        Editor editor = preferences.edit();
        editor.putBoolean(key, value);

        return editor.commit();
    }

    private boolean set(SharedPreferences preferences, String key, float value) {
        Editor editor = preferences.edit();
        editor.putFloat(key, value);

        return editor.commit();
    }

    private boolean get(SharedPreferences preferences, String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    private String get(SharedPreferences preferences, String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    private float get(SharedPreferences preferences, String key, float defValue) {
        return preferences.getFloat(key, defValue);
    }

    public boolean setUserPreference(String key, String value) {
        return set(userPreference, key, value);
    }

    public boolean setAppPreference(String key, String value) {
        return set(appPreference, key, value);
    }

    public boolean setLoginName(String loginName) {
        return set(appPreference, APP_PREFERENCE_LOGIN_NAME, loginName);
    }

    public String getLoginName() {
        return get(appPreference, APP_PREFERENCE_LOGIN_NAME, "");
    }

    public boolean setLanguage(String lang) {
        set(appPreference, APP_PREFERENCE_LANGUAGE_ALREADY_CHANGED_MANUALLY, true);
        return set(appPreference, APP_PREFERENCE_LANGUAGE, lang);
    }

    public boolean setLoginPassword(String loginPassword) {
        //use the app preference,remove it when logout.
        return set(appPreference, APP_PREFERENCE_LOGIN_PASSWORD, loginPassword);
    }

    public String getLoginPassword() {
        return get(appPreference, APP_PREFERENCE_LOGIN_PASSWORD, "");
    }

    public boolean setSynctime(String synctime) {
        //use the app preference,keep it when logout.
        return set(userPreference, APP_PREFERENCE_SYNCTIME, synctime);
    }

    public String getSynctime() {
        return get(userPreference, APP_PREFERENCE_SYNCTIME, "");
    }

    public void logout() {
        //clear the user preference.
        userPreference.edit().clear().commit();
        appPreference.edit().remove(APP_PREFERENCE_LOGIN_PASSWORD).commit();
    }

    public void resetApp() {
        userPreference.edit().clear().commit();
        appPreference.edit().clear().commit();
    }

    public boolean setCookie(String cookie) {
        return set(userPreference, APP_PREFERENCE_COOKIE, cookie);
    }

    public String getCookie() {
        return get(userPreference, APP_PREFERENCE_COOKIE, null);
    }

    public boolean getShowNotification() {
        return get(appPreference, APP_PREFERENCE_SETTING_SHOW_NOTIFICATION, false);
    }

    public boolean setShowNotification(boolean value) {
        return set(appPreference, APP_PREFERENCE_SETTING_SHOW_NOTIFICATION, value);
    }

    public boolean getPlayAudioAuto() {
        return get(appPreference, APP_PREFERENCE_SETTING_PLAY_AUDIO_AUTO, true);
    }

    public boolean setPlayAudioAuto(boolean value) {
        return set(appPreference, APP_PREFERENCE_SETTING_PLAY_AUDIO_AUTO, value);
    }

    public boolean getShowPicture() {
        return get(appPreference, APP_PREFERENCE_SETTING_SHOW_PICTURE, true);
    }

    public boolean setShowPicture(boolean value) {
        return set(appPreference, APP_PREFERENCE_SETTING_SHOW_PICTURE, value);
    }

    public float getVolume() {
        return get(appPreference, APP_PREFERENCE_SETTING_VOLUME, 100);
    }

    public boolean setVolume(float value) {
        return set(appPreference, APP_PREFERENCE_SETTING_VOLUME, value);
    }
}
