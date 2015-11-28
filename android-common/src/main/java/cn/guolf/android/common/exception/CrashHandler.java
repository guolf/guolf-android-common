package cn.guolf.android.common.exception;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import cn.guolf.android.common.util.AppUtils;
import cn.guolf.android.common.util.log.LogUtils;

/**
 * Author：guolf on 9/19/15 15:41
 * Email ：guo@guolingfa.cn
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler INSTANCE;
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LogUtils.e("app crash：" + ex);
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (Exception ex2) {

            } finally {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(10);
            }
        }
    }

    /**
     * 自定义异常处理
     *
     * @param ex
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        final String msg = ex.getLocalizedMessage();

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序出现异常,即将关闭该页面,请联系我们,我们会尽快处理,给你带来的不便请原谅!", Toast.LENGTH_LONG).show();
                save();
                Looper.loop();
            }

            private void save() {
                String fileName = "crash-" + new Date().getTime() + ".txt";
                File file = new File(Environment.getExternalStorageDirectory()
                        + "/log");
                if (!file.exists()) {
                    file.mkdirs();
                }
                File file1 = new File(file, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file1, true);
                    fos.write(getPhoneInfo().getBytes());
                    fos.write(msg.getBytes());
                    for (int i = 0; i < ex.getStackTrace().length; i++) {
                        fos.write(ex.getStackTrace()[i].toString().getBytes());
                    }

                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                }
            }

        }.start();

        return true;
    }

    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public String getPhoneInfo() {
        StringBuilder result = new StringBuilder();
        result.append("VersionCode=").append(AppUtils.getVerCode(mContext)).append('\n');
        result.append("VersionName=").append(AppUtils.getVerName(mContext)).append('\n');
        result.append("BOARD=").append(Build.BOARD).append('\n');
        result.append("BRAND=").append(Build.BRAND).append('\n');
        result.append("CPU_ABI=").append(Build.CPU_ABI).append('\n');
        result.append("DEVICE=").append(Build.DEVICE).append('\n');
        result.append("DISPLAY=").append(Build.DISPLAY).append('\n');
        result.append("FINGERPRINT=").append(Build.FINGERPRINT).append('\n');
        result.append("HOST=").append(Build.HOST).append('\n');
        result.append("ID=").append(Build.ID).append('\n');
        result.append("MANUFACTURER=").append(Build.MANUFACTURER).append('\n');
        result.append("MODEL=").append(Build.MODEL).append('\n');
        result.append("PRODUCT=").append(Build.PRODUCT).append('\n');
        result.append("TAGS=").append(Build.TAGS).append('\n');
        result.append("TIME=").append(Build.TIME).append('\n');
        result.append("TYPE=").append(Build.TYPE).append('\n');
        result.append("USER=").append(Build.USER).append('\n');
        result.append("VERSION.CODENAME=").append(Build.VERSION.CODENAME).append('\n');
        result.append("VERSION.INCREMENTAL=").append(Build.VERSION.INCREMENTAL).append('\n');
        result.append("VERSION.RELEASE=").append(Build.VERSION.RELEASE).append('\n');
        result.append("VERSION.SDK_INT=").append(Build.VERSION.SDK_INT).append('\n');

        return result.toString();
    }
}
