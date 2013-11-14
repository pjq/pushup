package me.pjq.pushup.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.*;
import android.widget.ProgressBar;
import android.widget.Toast;
import me.pjq.pushup.EFLogger;
import me.pjq.pushup.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kicoolzhang on 7/26/13.
 */
public abstract class WebViewActivity extends BaseFragmentActivity {
    protected static String TAG;
    public static final String KEY_URL = "url";

    protected WebView mWebView;
    protected WebSettings webSettings;
    protected Handler handler;
    protected String mUrl;
    protected ProgressBar progressBar;
    protected Map<String, String> extraHeaders;
    final Activity activity = this;

    abstract void ensureUI();

    abstract void postInit();

    @Override
    protected void onCreate(Bundle arg0) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(arg0);

        ensureUI();

        init();

        postInit();

        Intent i = getIntent();
        mUrl = i.getStringExtra(KEY_URL);
        EFLogger.i(TAG, "onCreate openUrl:" + mUrl);

        loadurl(mWebView, mUrl);
    }

    protected void init() {
        extraHeaders = new HashMap<String, String>();

        initWebView();

        progressBar = (ProgressBar) findViewById(R.id.webviewProgressBar);

        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (!Thread.currentThread().isInterrupted()) {
                    switch (msg.what) {
                        case 0:
                            progressBar.setVisibility(View.VISIBLE);
                            webSettings.setBlockNetworkImage(true);
                            break;
                        case 1:
                            progressBar.setVisibility(View.GONE);
                            webSettings.setBlockNetworkImage(false);
                            mWebView.requestFocusFromTouch();
                            break;
                    }
                }
                super.handleMessage(msg);
            }
        };
    }

    private void initWebView() {
        webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(true);
        webSettings.setSavePassword(false);
        webSettings.setAllowFileAccess(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(true);

        mWebView.setWebViewClient(new WebViewClientExtension());

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return false;
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                EFLogger.i(TAG, "progress:" + progress);
                progressBar.setProgress(progress);

                if (progress == 100) {
                    handler.sendEmptyMessage(1);
                }

                super.onProgressChanged(view, progress);
            }
        });
    }

    private class WebViewClientExtension extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            EFLogger.i(TAG, "Load URL:" + url);
            String scheme = Uri.parse(url).getScheme();

            view.loadUrl(url, extraHeaders);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(activity, "Error:" + description, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            EFLogger.i(TAG, "onPageStarted URL:" + url);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            EFLogger.i(TAG, "onPageFinished URL:" + url);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void loadurl(final WebView view, final String url) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
                view.loadUrl(url, extraHeaders);
            }
        });

    }

}
