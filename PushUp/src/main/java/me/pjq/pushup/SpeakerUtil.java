package me.pjq.pushup;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;

import java.util.Locale;

/**
 * Created by pjq on 11/9/13.
 */
public class SpeakerUtil {
    private static SpeakerUtil instance;
    private TextToSpeech tts;
    private boolean isTtsInited = false;
    private boolean ENABLE_SPEAKER = false;

    public SpeakerUtil(Context context) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE) {
                    } else {
                        isTtsInited = true;
                    }
                }
            }
        });

    }

    public static SpeakerUtil getInstance(Context context) {
        if (null == instance) {
            instance = new SpeakerUtil(context);
        }

        return instance;
    }

    public void speak(String text) {
        if (!ENABLE_SPEAKER) {
            return;
        }

        if (TextUtils.isEmpty(text)) {
            return;
        }

        if (isTtsInited && null != tts) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH,
                    null);
        }
    }

    public boolean isTtsInited() {
        return isTtsInited;
    }


    public void destroy() {
        tts.shutdown();
        isTtsInited = false;
        instance = null;
    }

    public void stop() {
        tts.stop();
    }
}
