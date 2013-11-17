package me.pjq.pushup.audio;

import java.io.File;

public interface RecorderAndPlaybackInterface {
    void setRecorderMode(int recorderMode);

    String getAudioTmpFilesPath();

    File getAudioTmpFiles();

    void saveAudioRecorderFile(short[] buf, int length);

    boolean startRecording();

    boolean stopRecording();

    boolean startPlayback(String fileName);

    boolean pausePlayback();

    boolean resumePlayback();

    boolean stopPlayback();

    boolean isPlaying();

    void recordingComplete(String filePath);

    void playbackComplete();

    void release();
}
