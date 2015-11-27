package cn.guolf.android.common.http;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import cn.guolf.android.common.util.log.LogUtils;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Author：guolf on 15/11/27 14:37
 * Email ：guo@guolingfa.cn
 */
public class ProgressResponseBody extends ResponseBody {

    //实际的待包装响应体
    private final ResponseBody responseBody;
    //进度回调接口
    private final ProgressResponseListener progressListener;
    //包装完成的BufferedSource
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, ProgressResponseListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() throws IOException {
        if (bufferedSource == null) {
            //包装
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            //当前读取字节数
            long totalBytesRead = 0L;
            @Override public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                //回调，如果contentLength()不知道长度，会返回-1
                //LogUtils.i("totalBytesRead:"+totalBytesRead + ",length:"+responseBody.contentLength() +",bytesRead:"+bytesRead);
                progressListener.onResponseProgress(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }
}
