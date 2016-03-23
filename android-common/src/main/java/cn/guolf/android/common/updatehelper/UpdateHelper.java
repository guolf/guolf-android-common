package cn.guolf.android.common.updatehelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.guolf.android.common.R;
import cn.guolf.android.common.http.OkHttpClientManager;
import cn.guolf.android.common.util.AppUtils;
import cn.guolf.android.common.util.log.LogUtils;

/**
 * 应用更新工具类
 * json格式："{\"msg\":\"1、新增XXXX功能\\r\\n2、修复XXXBUG\",\"version_name\":\"5\",\"version_code\":\"5\",\"flag\":true,\"apkUrl\":\"http://10.10.1.1/apk/test.apk\",\"sha1\":\"f63fc713aa78db6c7cd54a960e3dcc4e52a190ce\"}";
 * example：
 * <pre>
 * AbsUpdateInfoParser parser = new AbsUpdateInfoParser() {
 *
 * @Override public UpdateInfoBean parse(String info) {
 *
 * UpdateInfoBean bean = new UpdateInfoBean();
 * try {
 * JSONObject json = new JSONObject(info);
 * bean.setFlag(json.getBoolean("flag"));
 * bean.setVersionName(json.getString("version_name"));
 * bean.setVersionCode(json.getString("version_code"));
 * bean.setDownUrl(json.getString("apkUrl"));
 * bean.setWhatsNew(json.getString("msg"));
 * bean.setSha1(json.getString("sha1"));
 * return bean;
 * }catch (JSONException ex){
 * LogUtils.i(ex);
 * }
 * return null;
 * }
 * };
 * UpdateHelper helper = new UpdateHelper(UpdateDemoActivity.this,parser,new UpdateListener(UpdateDemoActivity.this){});
 * helper.check("http://10.10.1.1/apk/version.json");
 * </pre>
 */
public class UpdateHelper {

    private Activity mActivity;
    private AbsUpdateInfoParser mParser;
    private NotificationHelper mNotificationHelper;

    private ProgressDialog mProgressDialog;

    private UpdateListener mUpdateListener;

    public UpdateHelper(Activity activity, AbsUpdateInfoParser parser, UpdateListener listener) {
        if (activity == null) {
            throw new InvalidParameterException("Param Activity can not be null");
        }
        if (parser == null) {
            throw new InvalidParameterException("Param AbsUpdateInfoParser can not be null");
        }
        if (listener == null) {
            throw new InvalidParameterException("Param UpdateListener can not be null");
        }

        mParser = parser;
        mActivity = activity;
        mUpdateListener = listener;
        mNotificationHelper = new NotificationHelper(activity);
    }

    /**
     * sha1校验
     *
     * @param sha1 源文件SHA1值
     * @param file 下载文件
     * @return 匹配结果
     */
    public static boolean testSHA1(String sha1, File file) {
        if(TextUtils.isEmpty(sha1) || !file.exists())
            return false;
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            LogUtils.e("Exception while getting Digest", e);
            return false;
        }

        InputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            LogUtils.e("Exception while getting FileInputStream", e);
            return false;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            output = String.format("%40s", output).replace(' ', '0');

            LogUtils.e("Test: " + sha1);
            LogUtils.e("Generated: " + output);

            return (sha1.equals(output));
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LogUtils.e("Exception on closing MD5 input stream", e);
            }
        }
    }

    /**
     * 从指定的URL,以HTTP GET 的方式获取更新信息
     *
     * @param url
     */
    public void check(String url) {
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback() {
            @Override
            public void onError(Request request, String error) {
                mUpdateListener.onCheckFinish();
                LogUtils.e(error);
            }

            @Override
            public void onResponse(Object response) {
                UpdateInfoBean infoBean = mParser.parse(response.toString());
                if (infoBean != null
                        && infoBean.getVersionCode() > AppUtils.getVerCode(mActivity)) {
                    showDialog(infoBean);
                } else {
                    mUpdateListener.onCheckFinish();
                }
            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mUpdateListener.onCheckStart();
            }
        });
    }

    private void showDialog(final UpdateInfoBean bean) {
        StringBuffer msg = new StringBuffer();
        msg.append("最新版本:" + bean.getVersionName() + "\n\n");
        msg.append("当前版本:" + AppUtils.getVerName(mActivity) + "\n\n");
        msg.append("更新日志:" + bean.getWhatsNew());

        View rootView = View.inflate(mActivity, R.layout.updatehelper_dialog,
                null);
        TextView textView = (TextView) rootView
                .findViewById(R.id.updatehelper_dialog_tv);
        textView.setText(msg);

        DialogInterface.OnClickListener positiveBtnLsnr = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUpdateListener.onDownloadStart();
                downApk(bean);
            }
        };
        DialogInterface.OnClickListener negativeBtnLsnr = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUpdateListener.onUpdateClick(bean);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("发现新版本");
        builder.setMessage("是否下载?\n\n最新版本:" + bean.getVersionName() + "\n当前版本:" + AppUtils.getVerName(mActivity) + "\n更新内容:" + bean.getWhatsNew());
        builder.setPositiveButton("下载", positiveBtnLsnr);
        builder.setNegativeButton("下次再说", negativeBtnLsnr);
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    /**
     * 修正Android2.x上,包含ScrollView的Dialog总是充满屏幕的问题
     *
     * @param scrollView
     */
    private void fixScrollViewHeight(ScrollView scrollView) {
        int screenHeight = mActivity.getWindowManager().getDefaultDisplay().getHeight();
        LayoutParams lp = scrollView.getLayoutParams();
        lp.height = screenHeight / 3;
        scrollView.setLayoutParams(lp);
    }

    private void downApk(final UpdateInfoBean bean) {
        String path = getDownfilePath(bean);
        File destFile = new File(path);
        if (destFile.exists()) {
            if (testSHA1(bean.getSha1(), destFile)) {
                Toast.makeText(mActivity, "更新文件已下载,快速安装", Toast.LENGTH_SHORT).show();
                AppUtils.installApk(mActivity, destFile);
                return;
            } else {
                destFile.delete();
            }
        }
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setTitle("应用更新");
        mProgressDialog.setMessage("下载中...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(100);
        mProgressDialog.show();
        OkHttpClientManager.getDownloadDelegate().downloadAsyn(bean.getDownUrl(), path, new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, String e) {
                        LogUtils.e("下载失败" + e);
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String path) {
                        LogUtils.i("下载完成," + path);
                        File file = new File(path);
                        if (!bean.isFlag()) {
                            // 如果是强制更新，进度框不隐藏
                            mProgressDialog.dismiss();
                        }
                        if (testSHA1(bean.getSha1(), file)) {
                            AppUtils.installApk(mActivity, file);
                        } else {
                            Toast.makeText(mActivity, "文件校验失败", Toast.LENGTH_SHORT).show();
                        }
                        mNotificationHelper.notifyDownloadFinish(file);
                    }

                    @Override
                    public void onProgress(int progress) {
                        mProgressDialog.setProgress(progress);
                    }

                    @Override
                    public void onBefore(Request request) {
                        super.onBefore(request);
                        LogUtils.i("开始下载。。。");
                    }
                }
        );
    }

    private String getDownfilePath(UpdateInfoBean bean) {
        String fileName = mActivity.getPackageName() + "-" + bean.getVersionCode() + ".apk";
        String filePath = Environment.getExternalStorageDirectory() + File.separator + fileName;
        return filePath;
    }
}
