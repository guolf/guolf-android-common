package cn.guolf.android.demo;

import android.app.DownloadManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * Author：guolf on 9/19/15 11:06
 * Email ：guo@guolingfa.cn
 */
public class DownloadManagerDemo extends BaseActivity {

    public static final String     DOWNLOAD_FOLDER_NAME = "Trinea";
    public static final String     DOWNLOAD_FILE_NAME   = "MeiLiShuo.apk";

    public static final String     APK_URL              = "http://img.meilishuo.net/css/images/AndroidShare/Meilishuo_3.6.1_10006.apk";
    public static final String     KEY_NAME_DOWNLOAD_ID = "downloadId";

    private Button                 downloadButton;
    private ProgressBar downloadProgress;
    private TextView               downloadTip;
    private TextView               downloadSize;
    private TextView downloadPrecent;
    private Button downloadCancel;

    private DownloadManager downloadManager;

}
