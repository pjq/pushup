package me.pjq.pushup.audio;

public interface RecorderAndPlaybackListener {
   void onRecordingComplete(String fileName);
   void onPlaybackComplete();
}
