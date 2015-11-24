package cn.guolf.android.common.updatehelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import java.io.File;
import java.security.InvalidParameterException;

import cn.guolf.android.common.R;
import cn.guolf.android.common.http.OkHttpClientManager;
import cn.guolf.android.common.util.log.LogUtils;

/**
 * 应用更新工具类
 * json格式："{\"msg\":\"1、新增XXXX功能\\r\\n2、修复XXXBUG\",\"version_name\":\"5\",\"version_code\":\"5\",\"flag\":true,\"apkUrl\":\"http://10.10.1.1/apk/test.apk\"}";
 * example：
 <code>
     AbsUpdateInfoParser parser = new AbsUpdateInfoParser() {
        @Override
        public UpdateInfoBean parse(String info) {

            UpdateInfoBean bean = new UpdateInfoBean();
            try {
                JSONObject json = new JSONObject(info);
                bean.setFlag(json.getBoolean("flag"));
                bean.setVersionName(json.getString("version_name"));
                bean.setVersionCode(json.getString("version_code"));
                bean.setDownUrl(json.getString("apkUrl"));
                bean.setWhatsNew(json.getString("msg"));
                return bean;
            }catch (JSONException ex){
                LogUtils.i(ex);
            }
            return null;
        }
     };
     UpdateHelper helper = new UpdateHelper(UpdateDemoActivity.this,parser,new UpdateListener(UpdateDemoActivity.this){});
     helper.check("http://10.10.1.1/apk/version.json");
 </code>
 *
 */
public class UpdateHelper {

    private Activity mActivity;
    private PackageHelper mPackageHelper;
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
        mPackageHelper = new PackageHelper(activity);
        mNotificationHelper = new NotificationHelper(activity, mPackageHelper);
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
                        && infoBean.getVersionCode() > mPackageHelper.getLocalVersionCode()) {
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
        msg.append("当前版本:" + mPackageHelper.getLocalVersionName() + "\n\n");
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
        builder.setMessage("是否下载?\n\n最新版本:" + bean.getVersionName() + "\n当前版本:" + mPackageHelper.getLocalVersionName() + "\n更新内容:" + bean.getWhatsNew());
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

    private void downApk(UpdateInfoBean bean) {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setTitle("应用更新");
        mProgressDialog.setMessage("下载中...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(100);
        mProgressDialog.show();
        OkHttpClientManager.getDownloadDelegate().downloadAsyn(bean.getDownUrl(), getDownfilePath(bean), new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, String e) {
                        LogUtils.e("下载失败" + e);
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String path) {
                        LogUtils.i("下载完成," + path);
                        File file = new File(path);
                        mProgressDialog.dismiss();
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
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        return filePath;
    }
}
