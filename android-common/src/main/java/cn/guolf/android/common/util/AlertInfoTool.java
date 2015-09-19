package cn.guolf.android.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Author：guolf on 9/19/15 10:43
 * Email ：guo@guolingfa.cn
 * 确认框
 */
public class AlertInfoTool extends Activity {

    public static void confirm(Context context, String title, String msg,
                               final AlertInfoToolOper alertInfoToolOper) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (alertInfoToolOper != null) {
                    alertInfoToolOper.operate();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public interface AlertInfoToolOper {
        void operate();
    }
}
