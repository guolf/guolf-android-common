package cn.guolf.android.common.util;

import android.os.Environment;

/**
 * Author：guolf on 15/11/19 20:44
 * Email ：guo@guolingfa.cn
 */
public class CommonUtils {

    /**
     * 是否有SD卡
     *
     * @return
     */
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取应用运行的最大内存
     *
     * @return
     */
    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory() / 1024;
    }
}
