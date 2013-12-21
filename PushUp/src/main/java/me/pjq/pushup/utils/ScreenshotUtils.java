package me.pjq.pushup.utils;

/**
 * Created by pjq on 11/9/13.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;
import me.pjq.pushup.LocalPathResolver;

public class ScreenshotUtils {
    /**
     * @param pActivity
     * @return bitmap
     */
    public static Bitmap takeScreenShot(Activity pActivity) {
        Bitmap bitmap = null;
        View view = pActivity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        bitmap = view.getDrawingCache();

        Rect frame = new Rect();
        view.getWindowVisibleDisplayFrame(frame);
        int stautsHeight = frame.top;
        //stautsHeight = 0;

        int width = pActivity.getWindowManager().getDefaultDisplay().getWidth();
        int height = pActivity.getWindowManager().getDefaultDisplay().getHeight();
        bitmap = Bitmap.createBitmap(bitmap, 0, stautsHeight, width, height - stautsHeight);
        return bitmap;
    }


    /**
     * @param pBitmap
     */
    private static boolean savePic(Bitmap pBitmap, String strName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strName);
            if (null != fos) {
                pBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                return true;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getshotFilePath() {
        String imagePath = LocalPathResolver.getCachePath("images");

        File file = new File(imagePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        return imagePath + System.currentTimeMillis() + ".png";
    }

    public static String getshotFilePathByDay() {
        String imagePath = LocalPathResolver.getCachePath("images");

        File file = new File(imagePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        return imagePath + Utils.time2DateKey("" + System.currentTimeMillis()) + ".png";
    }

    /**
     * @param pActivity
     */
    public static boolean shotBitmap(Activity pActivity, String filePath) {
        return ScreenshotUtils.savePic(takeScreenShot(pActivity), filePath);
    }

    public static Bitmap shotBitmap2(Activity pActivity, String filePath) {
        Bitmap bitmap = takeScreenShot(pActivity);
        ScreenshotUtils.savePic(bitmap, filePath);
        return bitmap;
    }
}
