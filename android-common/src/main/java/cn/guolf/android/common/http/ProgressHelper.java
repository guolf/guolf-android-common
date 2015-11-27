package cn.guolf.android.common.http;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Author：guolf on 15/11/27 14:32
 * Email ：guo@guolingfa.cn
 */
public class ProgressHelper {

    public static OkHttpClient addProgressResponseListener(OkHttpClient client,final ProgressResponseListener progressListener){
        OkHttpClient clone = client.clone();
        // 增加拦截器
        clone.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        });
        return clone;
    }
}
