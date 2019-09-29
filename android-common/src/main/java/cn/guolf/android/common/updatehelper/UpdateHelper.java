package cn.guolf.android.common.updatehelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Request;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.jar.Manifest;

import cn.guolf.android.common.R;
import cn.guolf.android.common.http.OkHttpClientManager;
import cn.guolf.android.common.util.AlertInfoTool;
import cn.guolf.android.common.util.AppUtils;
import cn.guolf.android.common.util.PermissionUtils;
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
        if (TextUtils.isEmpty(sha1) || !file.exists())
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
                if (mActivity.isFinishing())
                    return;
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

        StringBuilder sb = new StringBuilder();
        sb.append(mActivity.getString(R.string.download_whether)).append("\n\n");
        sb.append(mActivity.getString(R.string.download_latest_version)).append(bean.getVersionName()).append("\n");
        sb.append(mActivity.getString(R.string.download_current_version)).append(AppUtils.getVerName(mActivity)).append("\n");
        sb.append(mActivity.getString(R.string.download_content)).append(bean.getWhatsNew());

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(mActivity.getString(R.string.download_discover));
        builder.setMessage(sb.toString());
        builder.setPositiveButton(mActivity.getString(R.string.download_ok), positiveBtnLsnr);
        builder.setNegativeButton(mActivity.getString(R.string.download_cancel), negativeBtnLsnr);
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    private void downApk(final UpdateInfoBean bean) {
        if(!PermissionUtils.check(mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            AlertInfoTool.alert(mActivity, "存储空间权限被禁止", "请计入设置-应用-卫监助手，进入权限页，开启此权限", new AlertInfoTool.AlertInfoToolOper() {
                @Override
                public void operate() {
                    try {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mActivity.startActivity(intent);
                    }catch (Exception ex){}
                }
            });
            return;
        }
        String path = getDownfilePath(bean);
        File destFile = new File(path);
        // 更新文件已存在，sha1校验通过则直接安装。否则删除重新下载
        if (destFile.exists()) {
            if (testSHA1(bean.getSha1(), destFile)) {
                Toast.makeText(mActivity, mActivity.getString(R.string.download_completed_install), Toast.LENGTH_SHORT).show();
                AppUtils.installApk(mActivity, destFile);
                return;
            } else {
                destFile.delete();
            }
        }
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setTitle(mActivity.getString(R.string.download_title));
        mProgressDialog.setMessage(mActivity.getString(R.string.download_msg));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(100);
        mProgressDialog.show();
        OkHttpClientManager.getDownloadDelegate().downloadAsyn(bean.getDownUrl(), path, new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, String e) {
                        LogUtils.e("下载失败" + e);
                        Toast.makeText(mActivity, mActivity.getString(R.string.download_error), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(mActivity, mActivity.getString(R.string.download_check_sha1_error), Toast.LENGTH_SHORT).show();
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
        String fileName = mActivity.getPackageName() + "_" + bean.getVersionCode() + "_" + bean.getVersionName() + ".apk";
        String filePath = Environment.getExternalStorageDirectory() + File.separator + fileName;
        return filePath;
    }
}
