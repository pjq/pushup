package me.pjq.pushup.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import com.tencent.mm.sdk.openapi.*;
import net.sourceforge.simcpux.Constants;
import net.sourceforge.simcpux.SendToWXActivity;
import net.sourceforge.simcpux.Util;

/**
 * Created by pjq on 12/21/13.
 */
public class WeChatUtils {
    private static final int THUMB_SIZE = 150;

    private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private static WeChatUtils instance;
    private IWXAPI api;

    private WeChatUtils(Context context) {
        api = WXAPIFactory.createWXAPI(context, Constants.APP_ID, false);
    }

    public static WeChatUtils getInstance(Context context) {
        if (null == instance) {
            instance = new WeChatUtils(context);
        }

        return instance;
    }


    public void register() {
        api.registerApp(Constants.APP_ID);
    }

    public IWXAPI getIWXAPI() {
        return api;
    }

    public void send(boolean sendToTimeline) {
        SendMessageToWX.Req req = appendReq;
        req.scene = sendToTimeline && isSupportTimeline() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    private SendMessageToWX.Req appendReq;

    public SendMessageToWX.Req createAppendReq(Bitmap bitmap, String subject, String text, String path) {
        Bitmap bmp = bitmap;
        WXImageObject imgObj = new WXImageObject(bmp);

        WXMediaMessage msg = new WXMediaMessage();
        msg.title = subject;
        msg.description = text;
        msg.mediaObject = imgObj;

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);  // 设置缩略图

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("appdata");
        req.message = msg;
        req.message.title = subject;
        req.message.description = text;

        this.appendReq = req;
        return req;
    }

    public SendMessageToWX.Req createAppendReq2(String subject, String text, String path) {
        final WXAppExtendObject appdata = new WXAppExtendObject();
        appdata.filePath = path;
        appdata.extInfo = "this is ext info";

        final WXMediaMessage msg = new WXMediaMessage();
        msg.setThumbImage(Util.extractThumbNail(path, 150, 150, true));
        msg.title = subject;
        msg.description = text;
        msg.mediaObject = appdata;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("appdata");
        req.message = msg;

        this.appendReq = req;

        return req;
    }

    public void share(boolean sendToTimeline) {
        SendMessageToWX.Req req = appendReq;
        req.scene = sendToTimeline && isSupportTimeline() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


    public boolean isSupportTimeline() {
        int wxSdkVersion = api.getWXAppSupportAPI();
        if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
            return true;
//            Toast.makeText(WXEntryActivity.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline supported", Toast.LENGTH_LONG).show();
        } else {
            return false;
//            Toast.makeText(WXEntryActivity.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline not supported", Toast.LENGTH_LONG).show();
        }

    }

    public void showDemo(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SendToWXActivity.class);
        context.startActivity(intent);
    }
}
