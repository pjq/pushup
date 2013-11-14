package me.pjq.pushup.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import com.squareup.otto.Bus;
import me.pjq.pushup.*;
import me.pjq.pushup.activity.CommonWebviewActivity;
import me.pjq.pushup.activity.WebViewActivity;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by pengjianqing on 11/8/13.
 */
public class Utils {
//    public static void overridePendingTransitionRight2Left(Activity activity) {
//        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//    }
//
//    public static void overridePendingTransitionLeft2Right(Activity activity) {
//        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//    }

    public static void share(Context context, String subject, String text, String filePath) {
        long start = System.currentTimeMillis();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent,
                context.getString(R.string.app_name)));
        long usetime = System.currentTimeMillis() - start;

        EFLogger.i(TAG, "use time " + usetime);
    }

    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * Check if the same day
     *
     * @param lastTime
     * @param now
     * @return
     */
    public static boolean isSameDay(long lastTime, long now) {
        Date lastDate = new Date(lastTime);
        Date nowDate = new Date(now);
        String lastDateString = sDateFormat.format(lastDate);
        String nowDateString = sDateFormat.format(nowDate);

        if (lastDateString.equalsIgnoreCase(nowDateString)) {
            return true;
        } else {
            return false;
        }
    }


    public static String getTheDayKey(long time) {
        boolean usemillions = true;

        if (usemillions) {
            return String.valueOf(time);
        }

        String dateKey = sDateFormat.format(time);
        return dateKey;
    }

    private static final SimpleDateFormat sDateKeyFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String time2DateKey(String millionsecond) {
        Date date = new Date(Long.valueOf(millionsecond));
        String dateKey = sDateKeyFormat.format(date);

        return dateKey;
    }

    public static void sendUpdateMsg() {
        Bus bus = ServiceProvider.getBus();
        bus.post(new UpdateMsg());
    }


    private static final String TAG = Utils.class.getSimpleName();

    public static int dpToPixels(Context context, int dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    public static void unBindDrawables(Activity activity,
                                       int layoutContainerResId) {
        unBindDrawables(activity.findViewById(layoutContainerResId));
    }

    /**
     * Unbind all the drawables.
     *
     * @param view
     */
    public static void unBindDrawables(View view) {
        if (view != null) {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }

            if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unBindDrawables(((ViewGroup) view).getChildAt(i));
                }

                ((ViewGroup) view).removeAllViews();
            }
        }
    }

    /**
     * Check whether the network is available.
     *
     * @param context
     * @return true if the network is available.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isWifiActive(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info;
        if (connectivity != null) {
            info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if ((info[i].getTypeName().equalsIgnoreCase("WIFI") || info[i]
                            .getTypeName().equalsIgnoreCase("WI FI"))
                            && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Check the network is available and not wifi,maybe 2G or 3G data
     * connection.
     *
     * @param context
     * @return true if is not wifi.
     */
    public static boolean isNetworkAvailableAndNotWifi(Context context) {
        return isNetworkAvailable(context) && !isWifiActive(context);
    }

    /**
     * Used to delete a directory.
     *
     * @param filepath
     * @throws java.io.IOException
     */
    public static void delDirectory(String filepath) throws IOException {
        try {
            File f = new File(filepath);
            if (f.exists() && f.isDirectory()) {
                if (f.listFiles().length == 0) {
                    f.delete();
                } else {
                    File delFile[] = f.listFiles();
                    int i = delFile.length;
                    for (int j = 0; j < i; j++) {
                        if (delFile[j].isDirectory()) {
                            delDirectory(delFile[j].getAbsolutePath());
                        }

                        delFile[j].delete();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int random(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }

    /**
     * create a rendom boolean true/false
     *
     * @return
     */
    public static boolean randomBoolean() {
        int s = random(1, 2);

        if (s == 1) {
            return true;
        } else {
            return false;
        }
    }


    private static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String getFileMD5String(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = in.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
                file.length());
        messagedigest.update(byteBuffer);
        return bufferToHex(messagedigest.digest());
    }

    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    private static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }


    public static String getApplicationVersionName(Context context) {
        try {
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            String versionName = pinfo.versionName;
            return versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public static String getApplicationPackageName(Context context) {
        try {
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pinfo.packageName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private static int s_appVersionCode = Integer.MAX_VALUE;

    public static int getApplicationVersionCode(Context contextWrapper) {
        if (s_appVersionCode != Integer.MAX_VALUE)
            return s_appVersionCode;
        try {
            PackageInfo pinfo = contextWrapper.getPackageManager()
                    .getPackageInfo(contextWrapper.getPackageName(), 0);
            s_appVersionCode = pinfo.versionCode;
            return s_appVersionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return Integer.MAX_VALUE;
        }
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static String getIMEI() {
        TelephonyManager tm = (TelephonyManager) MyApplication
                .getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if (TextUtils.isEmpty(imei)) {
            imei = "123456789_" + getAndroidID();
        }
        return imei;
    }

    public static String getIMSI() {
        TelephonyManager tm = (TelephonyManager) MyApplication
                .getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = tm.getSimSerialNumber();
        if (TextUtils.isEmpty(imsi)) {
            imsi = "123456789";
        }
        return imsi;
    }

    public static String getDeviceCode() {
        return getIMEI() + getAndroidID();
    }

    public static String getAppName() {
        return MyApplication.getContext().getString(R.string.app_name);
    }

    private static String getAndroidID() {
        return Settings.Secure.getString(MyApplication.getContext()
                .getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Get MCC-MNC
     *
     * @return
     */
    public static String getMccMnc() {
        String mccMnc = "";

        TelephonyManager telMgr = (TelephonyManager) MyApplication
                .getContext().getSystemService(Context.TELEPHONY_SERVICE);
        mccMnc = (TextUtils.isEmpty(telMgr.getSimOperator()) ? "nodata"
                : telMgr.getSimOperator());

        return mccMnc;
    }


    public static int string2Int(String text) {
        int value = 0;
        try {
            value = Integer.valueOf(text);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    public static String getYear_MonthString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(System.currentTimeMillis());
    }

    public static String getDateString() {
        SimpleDateFormat format = new SimpleDateFormat("dd");
        return format.format(System.currentTimeMillis());
    }

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZZZ";

    public static String getMillsTimeString() {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        return format.format(System.currentTimeMillis());
    }

    public static String getMillsTimeString(long nTime) {
        Date date = new Date(nTime);
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        return format.format(date);
    }

    public static Bitmap readBitmapFromLocal(Context context, String url, boolean fromAssets) {
        EFLogger.i("Utils", "readBitmapFromLocal,url=" + url + " fromAssets:" + fromAssets);
        MyApplication myWordsApplication = (MyApplication) context.getApplicationContext();
//        ImageCache bitmapCache = myWordsApplication.mImageCache;
//        Bitmap bitmap = bitmapCache.getBitmapFromMemCache(url);
//        if (null != bitmap) {
//            return bitmap;
//        }
        Bitmap bitmap = null;

        InputStream inputStream = null;
        try {
            if (fromAssets) {
                inputStream = context.getAssets().open(url);
            } else {
//                File file = new File(new URI(url));
                inputStream = new FileInputStream(url);
            }
            bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap = comp(bitmap);
//            if (null != bitmap) {
//                myWordsApplication.mImageCache.addBitmapToCache(url, bitmap, Constants.ASSET_UNIT_IMAGE_COMPRESS_RATE);
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                //omit
            }
        }
        return bitmap;
    }

    public static long getMillisTime() {
        return System.currentTimeMillis();
    }

    public static String getTimeReadable(int durationSecond) {
        int ss = durationSecond;

        // get hour
        int hour = durationSecond / 3600;
        String hourString = "";
        if (hour > 0) {
            ss = durationSecond % 3600;

            if (hour < 10) {
                hourString = "0" + hour;
            } else {
                hourString = "" + hour;
            }
        }

        int minute = ss / 60;
        int second = ss % 60;
        String minuteString = "";
        String sencodString = "";

        if (minute < 10) {
            minuteString = "0" + minute;
        } else {
            minuteString = "" + minute;
        }
        if (second < 10) {
            sencodString = "0" + second;
        } else {
            sencodString = "" + second;
        }

        String readableTime = minuteString + ":" + sencodString;
        if (hour > 0) {
            readableTime = hourString + ":" + readableTime;
        }

        return readableTime;
    }

    public static void closeCursor(Cursor cursor) {
        if (null == cursor) {
            return;
        }

        cursor.close();
        cursor = null;
    }

    /**
     * Get the application internal data/data path like this:/data/data/com.ef.efekta12/
     *
     * @param context
     * @return
     */
    public static String getInternalDataDataPath(Context context) {
        PackageManager m = context.getPackageManager();
        String s = context.getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            EFLogger.w("Utils", "Error Package name not found ", e);
            return null;
        }

        return s;
    }


    /**
     * Print the mem info.
     *
     * @param TAG
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void printMemInfo(String TAG) {
        if (!ApplicationConfig.INSTANCE.DEBUG_LOG()) {
            return;
        }

        TAG += ":meminfo";
        EFLogger.i(TAG, "printMemInfo:");

        EFLogger.i(TAG, "Max heap size = " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "M");
        EFLogger.i(TAG, "Allocate heap size = " + android.os.Debug.getNativeHeapAllocatedSize() / 1024 + "K");

        Method _readProclines = null;
        try {
            Class procClass;
            procClass = Class.forName("android.os.Process");
            Class parameterTypes[] = new Class[]{String.class, String[].class, long[].class};
            _readProclines = procClass.getMethod("readProcLines", parameterTypes);
            Object arglist[] = new Object[3];
            final String[] mMemInfoFields = new String[]{"MemTotal:", "MemFree:", "Buffers:", "Cached:"};
            long[] mMemInfoSizes = new long[mMemInfoFields.length];
            mMemInfoSizes[0] = 30;
            mMemInfoSizes[1] = -30;
            arglist[0] = new String("/proc/meminfo");
            arglist[1] = mMemInfoFields;
            arglist[2] = mMemInfoSizes;
            if (_readProclines != null) {
                _readProclines.invoke(null, arglist);
                for (int i = 0; i < mMemInfoSizes.length; i++) {
                    EFLogger.i(TAG, mMemInfoFields[i] + " = " + mMemInfoSizes[i] / 1024 + "M");
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static long getMaxHeapSize() {
        long maxHeapSize = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        return maxHeapSize;
    }


    public static String readInputStream(InputStream inputStream) {
        if (null == inputStream) {
            return null;
        }

        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));

        String line = "";
        StringBuffer stringBuffer = new StringBuffer();

        try {
            while (null != (line = r.readLine())) {
                stringBuffer.append(line);
            }

            inputStream.close();
            inputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuffer.toString();
    }

    public static void openUrl(Activity activity, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse(url));
        activity.startActivity(i);
    }

    public static void openUrlInsideApp(Activity activity, String url) {
        Intent intent = new Intent(activity, CommonWebviewActivity.class);
        intent.putExtra(WebViewActivity.KEY_URL, url);
        activity.startActivity(intent);
    }

    public static void moveEditTextSelectionToEnd(EditText editText) {
        Editable editable = editText.getEditableText();
        if (editable.length() > 1) {
            Selection.setSelection(editable, editable.length());
        }
    }


    public static boolean isBadDevice() {
        long maxHeapSize = Utils.getMaxHeapSize();
        boolean isbad = true;

        if (maxHeapSize >= 60) {
            isbad = false;
        } else if (maxHeapSize >= 40) {
            isbad = false;
        } else if (maxHeapSize <= 20) {
            isbad = true;
        }

        return isbad;
    }

    public static void overridePendingTransitionLeft2Right(Activity activity) {
//        activity.overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static void overridePendingTransitionRight2Left(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void setStudyViewPagerLimit(ViewPager viewPager, int arraySize) {
        if (isBadDevice()) {
            viewPager.setOffscreenPageLimit(1);
        } else {
            viewPager.setOffscreenPageLimit(arraySize);
        }
    }

    public static final int MAX_BITMAP_SIZE = 20;//k

    /**
     * Compress the bitmap
     *
     * @param image
     * @return
     */
    public static Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);

        int options = 100;
        int maxSize = MAX_BITMAP_SIZE;//k
        while (baos.toByteArray().length / 1024 > maxSize) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 80f;
        float ww = 80f;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);
    }

    private static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > MAX_BITMAP_SIZE) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);

        return bitmap;
    }


    /**
     * Create the encrypt full file path like this,just use the md5 to replace the filename:
     * /storage/emulated/0/ef/mywords/efoffline/offline-package/Level A - Unit 3_383/sounds/Where__at_what_point_did_I_go_wrong_in_my_calculat.mp3 > /storage/emulated/0/ef/mywords/efoffline/offline-package/Level A - Unit 3_383/sounds/cf3ce676bf20a2e8b10771221556123f
     *
     * @param path
     * @return
     */
    public static String getEncryptResourcePath(String path) {
        if (!Constants.ENABLE_ENCRYPT_RESOURCE_PATH_FILE) {
            return path;
        }

        if (TextUtils.isEmpty(path)) {
            return path;
        }

        String newPath = path;
        if (isResourceFile(path)) {
            int index = path.lastIndexOf(".");
            if (index > 0) {
                newPath = path.substring(0, index);
            }

            File file = new File(newPath);
            String name = file.getName();
            String parentPath = file.getParent();
            EFLogger.i(TAG, "name=" + name + ",parentPath=" + parentPath);
            String md5Path = parentPath + File.separator + getMD5String(name);
            EFLogger.i(TAG, "md5Path=" + md5Path);
            newPath = md5Path;
        }

        return newPath;
    }

    /**
     * Check whether it is the resource file that need encrypt the file path.
     *
     * @param path
     * @return
     */
    public static boolean isResourceFile(String path) {
        if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".mp3")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Encrypt the rescource file that is matched by #isResourceFile  in the specific path.
     *
     * @param path
     */
    public static void encryptResourceFilePath(String path) {
        if (!Constants.ENABLE_ENCRYPT_RESOURCE_PATH_FILE) {
            return;
        }
        File file = new File(path);

        if (file.isDirectory()) {
            if (null != file) {
                for (File item : file.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return true;
                    }
                })) {
                    encryptResourceFilePath(item.getAbsolutePath());
                }
            }

        } else {
            String absolutePath = file.getAbsolutePath();
            if (isResourceFile(absolutePath)) {
                String absolutePathTo = Utils.getEncryptResourcePath(absolutePath);
                EFLogger.i(TAG, "rename " + absolutePath + ">" + absolutePathTo);
                file.renameTo(new File(absolutePathTo));
            }
        }
    }

    public static ArrayList<Integer> randomColor() {
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(android.R.color.holo_blue_dark);
        colors.add(android.R.color.holo_red_dark);
        colors.add(android.R.color.holo_green_dark);
        colors.add(android.R.color.holo_orange_dark);

        return randomColor(colors);

    }

    public static ArrayList<Integer> randomColor(ArrayList<Integer> arrayList) {

        ArrayList<Integer> arrayList2 = new ArrayList<Integer>();
        ArrayList<Integer> arrayListCopy = new ArrayList<Integer>(arrayList);
        Random random = new Random();
        int size = arrayListCopy.size();

        while (size > 0) {
            int randomNum = random.nextInt(arrayListCopy.size());
            arrayList2.add(arrayListCopy.get(randomNum));
            arrayListCopy.remove(randomNum);
            size = arrayListCopy.size();
        }

        return arrayList2;
    }

    public static ArrayList<Object> randomSort(ArrayList<Object> arrayList) {
        ArrayList<Object> arrayList2 = new ArrayList<Object>();
        ArrayList<Object> arrayListCopy = new ArrayList<Object>(arrayList);
        Random random = new Random();
        int size = arrayListCopy.size();

        while (size > 0) {
            int randomNum = random.nextInt(arrayListCopy.size());
            arrayList2.add(arrayListCopy.get(randomNum));
            arrayListCopy.remove(randomNum);
            size = arrayListCopy.size();
        }

        return arrayList2;
    }
}
