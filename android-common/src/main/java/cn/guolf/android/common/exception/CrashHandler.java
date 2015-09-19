package cn.guolf.android.common.exception;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import cn.guolf.android.common.app.ApplicationBase;
import cn.guolf.android.common.util.log.LogUtils;

/**
 * Author：guolf on 9/19/15 15:41
 * Email ：guo@guolingfa.cn
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final boolean DEBUG = ApplicationBase.mApp.isDebuger;
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
                Thread.sleep(2000);
            } catch (Exception ex2) {

            } finally {
                // Process.killProcess(Process.myPid());
            }
        }
    }

    /**
     * 自定义异常处理
     *
     * @param ex
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        final String msg = ex.getLocalizedMessage();

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "抱歉，程序出异常了", Toast.LENGTH_LONG)
                        .show();
                Looper.loop();
            }

        }.start();

        return true;
    }

    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
}
