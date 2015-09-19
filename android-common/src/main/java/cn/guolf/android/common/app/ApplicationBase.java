package cn.guolf.android.common.app;

import android.app.Application;

import cn.guolf.android.common.exception.CrashHandler;

/**
 * Author：guolf on 9/19/15 15:40
 * Email ：guo@guolingfa.cn
 */
public class ApplicationBase extends Application {

    public static ApplicationBase mApp;
    public boolean isDebuger = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        initErrorHandler();
    }

    public void onLowMemory() {
        System.gc();
        super.onLowMemory();
    }

    private void initErrorHandler() {
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(this);
    }
}
