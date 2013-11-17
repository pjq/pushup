package me.pjq.pushup.audio;


import me.pjq.pushup.MyApplication;
import me.pjq.pushup.R;
import me.pjq.pushup.utils.Utils;

/**
 * Created by pengjianqing on 7/1/13.
 */
public class PlayerUtils {
    private static final RecorderAndPlaybackMediaRecorderImpl recorderAndPlaybackInterface = new RecorderAndPlaybackMediaRecorderImpl(MyApplication.getContext(), null);


    public static RecorderAndPlaybackMediaRecorderImpl getInstance() {
        return recorderAndPlaybackInterface;
    }

    public static void startPlayback(String path) {
        recorderAndPlaybackInterface.startPlayback(path);

    }

    public static void release() {
    }

    public static void playRawSound() {
        PlayerUtils.startPlayback("android.resource://" + Utils.getApplicationPackageName(MyApplication.getContext()) + "/" + R.raw.tap);
    }

    public static void releasePlayer() {
        recorderAndPlaybackInterface.release();
    }

    public static void stopRecording() {
        recorderAndPlaybackInterface.stopRecording();
    }

    public static void startRecording() {
        recorderAndPlaybackInterface.startRecording();
    }

    public static void setListener(RecorderAndPlaybackListener listener) {
        recorderAndPlaybackInterface.setListener(listener);
    }
}
