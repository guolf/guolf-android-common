package cn.guolf.android.common.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Author：guolf on 15/11/19 20:59
 * Email ：guo@guolingfa.cn
 */
public class PhoneUtils {

    /**
     * 调用系统发短信界面
     *
     * @param activity
     * @param phoneNumber 手机号
     * @param smsContent  短信内容
     */
    public static void sendMessage(Context activity, String phoneNumber,
                                   String smsContent) {
        if (TextUtils.isEmpty(phoneNumber)) {
            throw new RuntimeException("手机号码不能为空");
        }
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", smsContent);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(it);
    }

    /**
     * 调用系统拨打号码界面
     *
     * @param context
     * @param phoneNumber
     */
    public static void callPhones(Context context, String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            throw new RuntimeException("手机号码不能为空");
        }
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
