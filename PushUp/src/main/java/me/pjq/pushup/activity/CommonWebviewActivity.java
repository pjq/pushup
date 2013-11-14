package me.pjq.pushup.activity;

import android.webkit.WebView;
import me.pjq.pushup.R;

/**
 * Created by kicoolzhang on 8/6/13.
 */
public class CommonWebviewActivity extends WebViewActivity {
    @Override
    void ensureUI() {
        TAG = CommonWebviewActivity.class.getSimpleName();

        setContentView(R.layout.webview_subcribe);

        mWebView = (WebView) findViewById(R.id.webview);
    }

    @Override
    void postInit() {
        //extraHeaders.put(Api.EH_X_WEBAPP_KEY, Api.EH_X_WEBAPP_VALUE);
    }
}
