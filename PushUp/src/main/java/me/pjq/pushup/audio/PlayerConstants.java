package me.pjq.pushup.audio;

import android.media.AudioFormat;

public class PlayerConstants {
	public static final int STATUS_INVALID = 100;

	public static final int STATUS_IDLE = STATUS_INVALID + 1;

	public static final int STATUS_INITIALIZED = STATUS_IDLE + 1;

	public static final int STATUS_PREPARED = STATUS_INITIALIZED + 1;

	public static final int STATUS_STARTED = STATUS_PREPARED + 1;

	public static final int STATUS_PAUSED = STATUS_STARTED + 1;

	public static final int STATUS_PLACKBACK_COMPLETED = STATUS_PAUSED + 1;

	public static final int STATUS_STOPPED = STATUS_PLACKBACK_COMPLETED + 1;

	public static final int STATUS_ERROR = STATUS_STOPPED + 1;


    public static final int RECORDER_SAMPLERATE = 8000 * 2;
    public static final float RECORDER_SAMPLERATE_FLOAT = RECORDER_SAMPLERATE;
    public static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    public static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    public static final int RECORDER_MODE_JUST_RECORDER = 1;
    public static final int RECORDER_MODE_JUST_ASR = RECORDER_MODE_JUST_RECORDER + 1;
    public static final int RECORDER_MODE_RECORDER_ASR = RECORDER_MODE_JUST_ASR + 1;
}
