package cn.guolf.android.common.http;

/**
 * Author：guolf on 15/11/27 14:38
 * Email ：guo@guolingfa.cn
 */
public interface ProgressResponseListener {
    void onResponseProgress(long bytesRead, long contentLength, boolean done);
}
