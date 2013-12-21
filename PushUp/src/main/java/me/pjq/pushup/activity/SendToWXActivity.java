package me.pjq.pushup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.view.View;
import com.tencent.mm.sdk.openapi.*;
import me.pjq.pushup.R;
import me.pjq.pushup.utils.WeChatUtils;
import net.sourceforge.simcpux.Util;
import net.sourceforge.simcpux.uikit.CameraUtil;

public class SendToWXActivity extends BaseActionBarActivity {

    private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    private WeChatUtils weChatUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weChatUtils = WeChatUtils.getInstance(this);

        setContentView(R.layout.send_to_wx_pushup);
        initView();

        ActionBar actionBar = getActionBarImpl();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getString(R.string.share_to_weixin));
        actionBar.setDisplayHomeAsUpEnabled(true);}

    private void initView() {
        // send to weixin
        findViewById(R.id.share_to_wechat_friends).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                weChatUtils.send(false);
                finish();
            }
        });

        findViewById(R.id.share_to_wechat_moments).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                weChatUtils.send(true);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case 0x101: {
                final WXAppExtendObject appdata = new WXAppExtendObject();
                final String path = CameraUtil.getResultPhotoPath(this, data, SDCARD_ROOT + "/tencent/");
                appdata.filePath = path;
                appdata.extInfo = "this is ext info";

                final WXMediaMessage msg = new WXMediaMessage();
                msg.setThumbImage(Util.extractThumbNail(path, 150, 150, true));
                msg.title = "this is title";
                msg.description = "this is description";
                msg.mediaObject = appdata;

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("appdata");
                req.message = msg;
//                req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                weChatUtils.getIWXAPI().sendReq(req);

                finish();
                break;
            }
            default:
                break;
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
