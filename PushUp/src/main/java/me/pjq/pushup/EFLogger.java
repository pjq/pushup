package me.pjq.pushup;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

import java.io.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * The log util.
 *
 * @author Jianqing.Peng
 * @since 1.0
 */
public class EFLogger {
    @SuppressWarnings("unused")
    private static final String TAG = EFLogger.class.getSimpleName();
    private static final boolean DEBUG = ApplicationConfig.INSTANCE.DEBUG();
    private static EFLogger mInstance = null;

    public static final int LOG_LEVEL_INFO = 0;
    public static final int LOG_LEVEL_DEBUG = 1;
    public static final int LOG_LEVEL_EXCEPTION = 2;
    public static final int LOG_LEVEL_CRASH = 3;
    public static final int LOG_LEVEL_LOC = 4;
    public static final int LOG_LEVEL_SPEED = 5;

    private static String LOG_PATH = "";
    private static final String LOG_INFO_FILENAME = "MYWORDS_I";
    private static final String LOG_DEBUG_FILENAME = "MYWORDS_D";
    private static final String LOG_EXCEPTION_FILENAME = "MYWORDS_E";
    public static final String LOG_CRASH_FILENAME = "MYWORDS_C";
    private static final String LOG_LOC_FILENAME = "LOC";
    private static final String LOG_SPEED_FILENAME = "SPE";
    private static final String LOG_FILE_EXTNAME = ".log";

    public static final int CRASH_LOG_MAX_FILE_LENGTH = 2 * 1024;
    public static final int EXCEPTION_LOG_MAX_FILE_LENGTH = 500 * 1024;
    public static final int LOC_LOG_MAX_FILE_LENGTH = 10 * 1024;
    public static final int SPE_LOG_MAX_FILE_LENGTH = 10 * 1024;

    private Object FILE_LOCK = new Object();

    public static EFLogger init() {
        if (mInstance == null) {
            mInstance = new EFLogger();
        }

        return mInstance;
    }

    private EFLogger() {
        LOG_PATH = createLogPath();
    }

    /**
     * @return
     */
    private String createLogPath() {
        File dir = new File(LocalPathResolver.getLogDir());

        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }

        return dir.getAbsolutePath();
    }

    public static void d(String tag, String message) {
        mInstance.d_(tag, message);
    }

    private void d_(String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
            saveLogToFile(String.format("%s: %s", tag, message),
                    LOG_LEVEL_DEBUG);
        }
    }

    public static void d(String tag, Throwable ex) {
        mInstance.d_(tag, ex);
    }

    private void d_(String tag, Throwable ex) {
        if (DEBUG) {
            Log.d(tag, "Exception:", ex);
            saveLogToFile(String.format("%s: %s", tag, getDebugReport(ex)),
                    LOG_LEVEL_DEBUG);
        }
    }

    public static void i(String tag, String message) {
        mInstance.i_(tag, message);
    }

    private void i_(String tag, String message) {
        if (DEBUG) {
            if (null == message) {
                return;
            }

            Log.i(tag, message);
            try {
                String str = String.format("%s: %s", tag, message);
                saveLogToFile(str, LOG_LEVEL_INFO);
            } catch (OutOfMemoryError oom) {

            }
        }

    }

    public static void e(String tag, String message) {
        mInstance.e_(tag, message);
    }

    private void e_(String tag, String message) {
        if (DEBUG) {
            if (null == message) {
                return;
            }

            Log.e(tag, message);
            try {
                saveLogToFile(String.format("%s: %s", tag, message),
                        LOG_LEVEL_EXCEPTION);
            } catch (OutOfMemoryError oom) {

            }
        }

    }

    public void r(String tag, String message) {
        mInstance.r_(tag, message);
    }

    private void r_(String tag, String message) {
        if (DEBUG) {
            if (null == message) {
                return;
            }

            Log.e(tag, message);
            try {
                saveLogToFile(String.format("%s: %s", tag, message),
                        LOG_LEVEL_EXCEPTION);
            } catch (OutOfMemoryError oom) {

            }
        }
    }

    public static void e(String tag, Throwable ex) {
        mInstance.e_(tag, ex);
    }

    private void e_(String tag, Throwable ex) {
        if (DEBUG) {

            Log.e(tag, "Exception:", ex);
            try {
                saveLogToFile(String.format("%s: %s", tag, getDebugReport(ex)),
                        LOG_LEVEL_EXCEPTION);
            } catch (OutOfMemoryError oom) {

            }
        }

    }

    public static void v(String tag, String message) {
        mInstance.v_(tag, message);
    }

    private void v_(String tag, String message) {
        if (DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void w(String tag, String message) {
        mInstance.w_(tag, message);
    }

    private void w_(String tag, String message) {
        if (DEBUG) {
            Log.w(tag, message);
        }
    }


    /**
     * Get the crash report
     *
     * @param exception
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDebugReport(Throwable exception) {
        NumberFormat theFormatter = new DecimalFormat("#0.");
        StringBuilder theErrReport = new StringBuilder();
        theErrReport.append(MyApplication.getContext().getPackageName()
                + " generated the following exception:\n");
        if (exception != null) {
            theErrReport.append(exception.toString() + "\n\n");
            // stack trace
            StackTraceElement[] theStackTrace = exception.getStackTrace();
            if (theStackTrace.length > 0) {
                theErrReport.append("======== Stack trace =======\n");
                int length = theStackTrace.length;
                for (int i = 0; i < length; i++) {
                    theErrReport.append(theFormatter.format(i + 1) + "\t"
                            + theStackTrace[i].toString() + "\n");
                }
                theErrReport.append("=====================\n\n");
            }
            Throwable theCause = exception.getCause();
            if (theCause != null) {
                theErrReport.append("======== Cause ========\n");
                theErrReport.append(theCause.toString() + "\n\n");
                theStackTrace = theCause.getStackTrace();
                int length = theStackTrace.length;
                for (int i = 0; i < length; i++) {
                    theErrReport.append(theFormatter.format(i + 1) + "\t"
                            + theStackTrace[i].toString() + "\n");
                }
                theErrReport.append("================\n\n");
            }
            PackageManager pm = MyApplication.getContext()
                    .getPackageManager();
            PackageInfo pi;
            try {
                pi = pm.getPackageInfo(MyApplication.getContext()
                        .getPackageName(), 0);
            } catch (NameNotFoundException e) {
                pi = new PackageInfo();
                pi.versionName = "unknown";
                pi.versionCode = 0;
            }
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            theErrReport.append("======== Environment =======\n");
            theErrReport.append("Time=" + format.format(now) + "\n");
            theErrReport.append("Device=" + Build.FINGERPRINT + "\n");
            try {
                Field mfField = Build.class.getField("MANUFACTURER");
                theErrReport.append("Manufacturer=" + mfField.get(null) + "\n");
            } catch (SecurityException e) {
            } catch (NoSuchFieldException e) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
            theErrReport.append("Model=" + Build.MODEL + "\n");
            theErrReport.append("Product=" + Build.PRODUCT + "\n");
            theErrReport.append("App="
                    + MyApplication.getContext().getPackageName()
                    + ", version " + pi.versionName + " (build "
                    + pi.versionCode + ")\n");
            theErrReport.append("=========================\nEnd Report");
        } else {
            theErrReport.append("the exception object is null\n");
        }

        return theErrReport.toString();
    }

    public static void saveCrashExceptionLog(String tag, Throwable exception) {
        mInstance.saveCrashExceptionLog_(tag, exception);
    }

    private void saveCrashExceptionLog_(String tag, Throwable exception) {
        String report = getDebugReport(exception);
        if (DEBUG) {
            Log.d(tag, report);
            saveLogToFile(report, LOG_LEVEL_CRASH);
        } else {
            saveLogToFile(report, LOG_LEVEL_CRASH);
        }
    }

    /**
     * @param tag
     * @param exception
     */
    public void saveCrashExceptionLog(String tag, String log, int logLevel) {
        if (DEBUG) {
            Log.d(tag, log);
            saveLogToFile(log, logLevel);
        }

    }

    @SuppressLint("SimpleDateFormat")
    public void saveLogToFile(String message, int mode) {
        String text = message;
        if (MyApplication.isSDCardAvailable()) {
            synchronized (FILE_LOCK) {
                LOG_PATH = createLogPath();
                switch (mode) {
                    case LOG_LEVEL_INFO:
                    case LOG_LEVEL_DEBUG:
                        if (DEBUG) {
                            String dfullName = "";
                            if (mode == LOG_LEVEL_INFO) {
                                dfullName = LOG_PATH + File.separator
                                        + LOG_INFO_FILENAME + LOG_FILE_EXTNAME;
                            } else {
                                dfullName = LOG_PATH + File.separator
                                        + LOG_DEBUG_FILENAME + LOG_FILE_EXTNAME;
                            }
                            String currentTimeString = new SimpleDateFormat(
                                    "yyyy.MM.dd HH:mm:ss ").format(new Date());
                            text = currentTimeString + message;
                            writeFile(dfullName, text);
                        }
                        break;
                    case LOG_LEVEL_EXCEPTION:
                        String efullName = LOG_PATH + File.separator
                                + LOG_EXCEPTION_FILENAME + LOG_FILE_EXTNAME;
                        File efile = new File(efullName);
                        if (efile.exists() && efile.isFile()) {
                            if (efile.length() >= EXCEPTION_LOG_MAX_FILE_LENGTH) {
                                String newEFileName = LOG_PATH + File.separator
                                        + LOG_EXCEPTION_FILENAME + "_B"
                                        + LOG_FILE_EXTNAME;
                                File bfile = new File(newEFileName);
                                if (bfile.exists() && bfile.isFile()) {
                                    deleteFile(newEFileName);
                                }
                                efile.renameTo(new File(newEFileName));
                            }
                        }
                        String currentTimeString = new SimpleDateFormat(
                                "yyyy.MM.dd HH:mm:ss ").format(new Date());
                        try {
                            text = currentTimeString + message;
                        } catch (OutOfMemoryError e) {
                            break;
                        }
                        writeFile(efullName, text);
                        break;

                    case LOG_LEVEL_CRASH:
                        String fullName = LOG_PATH + File.separator
                                + LOG_CRASH_FILENAME + LOG_FILE_EXTNAME;
                        fullName = LOG_PATH
                                + File.separator
                                + LOG_CRASH_FILENAME
                                + "_"
                                + new SimpleDateFormat("yyyyMMddHHmmss")
                                .format(new Date()) + LOG_FILE_EXTNAME;
                        File file = new File(fullName);
                        if (file.exists() && file.isFile()) {
                            if (file.length() >= CRASH_LOG_MAX_FILE_LENGTH) {
                                String newFileName = LOG_PATH
                                        + File.separator
                                        + LOG_CRASH_FILENAME
                                        + new SimpleDateFormat("yyyyMMddHHmmss")
                                        .format(new Date())
                                        + LOG_FILE_EXTNAME;
                                file.renameTo(new File(newFileName));
                            }
                        }
                        writeFile(fullName, message);
                        break;

                    case LOG_LEVEL_LOC:
                        String lfullName = LOG_PATH + File.separator
                                + LOG_LOC_FILENAME + LOG_FILE_EXTNAME;
                        File lfile = new File(lfullName);
                        if (lfile.exists() && lfile.isFile()) {
                            if (lfile.length() >= LOC_LOG_MAX_FILE_LENGTH) {
                                break;
                            }
                        }
                        writeFile(lfullName, message);
                        break;

                    case LOG_LEVEL_SPEED:
                        String sfullName = LOG_PATH + File.separator
                                + LOG_SPEED_FILENAME + LOG_FILE_EXTNAME;
                        File sfile = new File(sfullName);
                        if (sfile.exists() && sfile.isFile()) {
                            if (sfile.length() >= SPE_LOG_MAX_FILE_LENGTH) {
                                break;
                            }
                        }

                        writeFile(sfullName, message);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * save data to local file
     *
     * @param data
     */
    private void writeFile(String fileName, String data) {
        BufferedWriter buf = null;
        try {
            buf = new BufferedWriter(new FileWriter(fileName, true));
            buf.write(data, 0, data.length());
            buf.newLine();

            if (buf != null) {
                buf.close();
                buf = null;
            }
        } catch (OutOfMemoryError oom) {

        } catch (Exception e) {
        } finally {
            try {
                if (buf != null) {
                    buf.close();
                    buf = null;
                }
            } catch (IOException e) {
            }
        }

    }

    public String readFromFileByLevel(int mode) {
        StringBuffer sbuffer = new StringBuffer();
        if (MyApplication.isSDCardAvailable()) {
            synchronized (FILE_LOCK) {
                LOG_PATH = createLogPath();
                File dir = new File(LOG_PATH);
                File[] files = dir.listFiles();
                int length = files.length;
                String name = "";
                switch (mode) {
                    case LOG_LEVEL_LOC:
                        name = LOG_LOC_FILENAME;
                        break;
                    case LOG_LEVEL_SPEED:
                        name = LOG_SPEED_FILENAME;
                        break;
                    default:
                        break;
                }
                for (int i = 0; i < length; i++) {
                    if (files[i].getName().startsWith(name)) {
                        String fullName = files[i].getAbsolutePath();
                        String log = readFileToString(fullName);
                        sbuffer.append(log + "\n");
                    }
                }
            }
        }
        return sbuffer.toString();
    }

    public byte[] readFileToByte(String fullName) {
        byte[] data = null;
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(fullName));
            out = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            data = out.toByteArray();
        } catch (Exception e) {
            data = null;
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (IOException ie) {
            }
        }
        return data;
    }

    public String readFileToString(String fullName) {
        StringBuffer sbuffer = new StringBuffer();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(fullName));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sbuffer.append(line + "\n");
            }
        } catch (Exception e) {
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException ex) {
            }
        }
        return sbuffer.toString();
    }

    /**
     * delete file.
     *
     * @param fullFileName
     */
    public void deleteFile(String fullFileName) {
        if (MyApplication.isSDCardAvailable()) {
            synchronized (FILE_LOCK) {
                File file = new File(fullFileName);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    public void deleteFileByMode(int mode) {
        if (MyApplication.isSDCardAvailable()) {
            synchronized (FILE_LOCK) {
                String name = "";
                switch (mode) {
                    case LOG_LEVEL_INFO:
                        name = LOG_INFO_FILENAME;
                        break;
                    case LOG_LEVEL_DEBUG:
                        name = LOG_DEBUG_FILENAME;
                        break;
                    case LOG_LEVEL_EXCEPTION:
                        name = LOG_EXCEPTION_FILENAME;
                        break;
                    case LOG_LEVEL_CRASH:
                        name = LOG_CRASH_FILENAME;
                        break;
                    case LOG_LEVEL_LOC:
                        name = LOG_LOC_FILENAME;
                        break;
                    case LOG_LEVEL_SPEED:
                        name = LOG_SPEED_FILENAME;
                        break;
                    default:
                        break;
                }
                if (!name.equals("")) {
                    LOG_PATH = createLogPath();
                    File dir = new File(LOG_PATH);
                    File[] files = dir.listFiles();
                    int length = files.length;
                    for (int i = 0; i < length; i++) {
                        if (files[i].getName().startsWith(name)) {
                            File f = files[i];
                            f.delete();
                        }
                    }
                }
            }
        }
    }

    public boolean hasErrLogFile() {
        boolean hasFile = false;
        if (MyApplication.isSDCardAvailable()) {
            LOG_PATH = createLogPath();
            File dir = new File(LOG_PATH);
            File[] files = dir.listFiles();
            int count = 0;
            int length = files.length;
            for (int i = 0; i < length; i++) {
                if (files[i].getName().startsWith(LOG_CRASH_FILENAME)) {
                    count++;
                }
            }
            if (count > 0) {
                hasFile = true;
            }
        }
        return hasFile;
    }

    public static void d(String tag, String string, Throwable t) {
        mInstance.d_(tag, string, t);
    }

    private void d_(String tag, String string, Throwable t) {
        if (DEBUG) {
            Log.d(tag, string, t);
            saveLogToFile(String.format("%s: %s", tag, string),
                    LOG_LEVEL_DEBUG);
        }
    }

    public static void w(String tag, String string, Exception e) {
        mInstance.w_(tag, string, e);
    }

    private void w_(String tag, String string, Exception e) {
        if (DEBUG) {
            Log.w(tag, string, e);
            saveLogToFile(String.format("%s: %s", tag, e),
                    LOG_LEVEL_DEBUG);
        }
    }

    public static void e(String tag, String string, Exception e) {
        mInstance.e_(tag, string, e);
    }

    private void e_(String tag, String string, Exception e) {
        if (DEBUG) {
            Log.e(tag, string, e);
            saveLogToFile(String.format("%s: %s", tag, e),
                    LOG_LEVEL_DEBUG);
        }
    }

}
