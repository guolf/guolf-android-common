package cn.guolf.android.common.util.log;

import cn.guolf.android.common.util.SystemUtils;

/**
 * Author：guolf on 9/19/15 15:49
 * Email ：guo@guolingfa.cn
 * 日志工具，日志开关：LogUtils.configAllowLog = false;
 */
public class LogUtils {

    // 允许输出日志
    public static boolean configAllowLog = true;
    private static Logger logger;

    static {
        logger = new Logger();
    }

    /**
     * verbose输出
     *
     * @param msg
     * @param args
     */
    public static void v(String msg, Object... args) {
        logger.v(SystemUtils.getStackTrace(), msg, args);
    }

    public static void v(Object object) {
        logger.v(SystemUtils.getStackTrace(), object);
    }


    /**
     * debug输出
     *
     * @param msg
     * @param args
     */
    public static void d(String msg, Object... args) {
        logger.d(SystemUtils.getStackTrace(), msg, args);
    }


    public static void d(Object object) {
        logger.d(SystemUtils.getStackTrace(), object);
    }


    /**
     * info输出
     *
     * @param msg
     * @param args
     */
    public static void i(String msg, Object... args) {
        logger.i(SystemUtils.getStackTrace(), msg, args);
    }

    public static void i(Object object) {
        logger.i(SystemUtils.getStackTrace(), object);
    }


    /**
     * warn输出
     *
     * @param msg
     * @param args
     */
    public static void w(String msg, Object... args) {
        logger.w(SystemUtils.getStackTrace(), msg, args);
    }

    public static void w(Object object) {
        logger.w(SystemUtils.getStackTrace(), object);
    }


    /**
     * error输出
     *
     * @param msg
     * @param args
     */
    public static void e(String msg, Object... args) {
        logger.e(SystemUtils.getStackTrace(), msg, args);
    }

    public static void e(Object object) {
        logger.e(SystemUtils.getStackTrace(), object);
    }


    /**
     * assert输出
     *
     * @param msg
     * @param args
     */
    public static void wtf(String msg, Object... args) {
        logger.wtf(SystemUtils.getStackTrace(), msg, args);
    }

    public static void wtf(Object object) {
        logger.wtf(SystemUtils.getStackTrace(), object);
    }


    /**
     * 打印json
     *
     * @param json
     */
    public static void json(String json) {
        logger.json(SystemUtils.getStackTrace(), json);
    }
}
