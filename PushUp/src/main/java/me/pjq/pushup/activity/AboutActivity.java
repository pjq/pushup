package me.pjq.pushup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import me.pjq.pushup.R;
import me.pjq.pushup.utils.Utils;
import net.sourceforge.simcpux.Util;

public class AboutActivity extends BaseActionBarActivity {
    protected static String TAG = AboutActivity.class.getSimpleName();
    private TextView appName;
    private TextView appVersion;
    private TextView appDetail;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.about);

        init();

        ActionBar actionBar = getActionBarImpl();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getString(R.string.menu_item_about));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    protected void init() {
        appName = (TextView) findViewById(R.id.app_name);
        appVersion = (TextView) findViewById(R.id.app_version);
        appDetail = (TextView) findViewById(R.id.app_detail);

        appVersion.setText(Utils.getApplicationVersionName(getApplicationContext()) + "." + Utils.getApplicationVersionCode(getApplicationContext()));

        TextView appUrl = (TextView) findViewById(R.id.app_url);
        String url = getString(R.string.app_url);
        SpannableString sp = new SpannableString(url);
        sp.setSpan(new URLSpan(url), 0, url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        appUrl.setText(sp);
        appUrl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.openUrlInsideApp(AboutActivity.this, getString(R.string.app_url));
                Utils.overridePendingTransitionRight2Left(AboutActivity.this);
            }
        });
    }
}
