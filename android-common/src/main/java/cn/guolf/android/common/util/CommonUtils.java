package cn.guolf.android.common.util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Author：guolf on 15/11/19 20:44
 * Email ：guo@guolingfa.cn
 */
public class CommonUtils {

    /**
     * 是否有SD卡
     *
     * @return
     */
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取应用运行的最大内存
     *
     * @return
     */
    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory() / 1024;
    }

    /**
     * 根据指定日期格式获取当前时间
     *
     * @param dateFormat 时间格式（yyyy-MM-dd hh:mm:ss）
     * @return
     */
    public static String getCurrentDate(String dateFormat) {
        Date d = new Date();
        String currentDate = new SimpleDateFormat(dateFormat).format(d.getTime());
        return currentDate;
    }

    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * use to show datepicker
     *
     * @param mContext
     * @param format    of the date format
     * @param mTextView in which you have to set selected date
     */
    public static void showDatePickerDialog(final Context mContext,
                                            final String format, final TextView mTextView) {
        final Calendar dateTime = Calendar.getInstance();
        new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
                dateTime.set(year, monthOfYear, dayOfMonth);

                mTextView.setText(dateFormatter.format(dateTime.getTime())
                        .toString());
            }
        }, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH),
                dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * show timepicker
     *
     * @param mContext
     * @param mTextView formar of the time
     * @return show timepicker
     */
    public static void showTimePickerDialog(final Context mContext,
                                            final TextView mTextView) {
        final Calendar dateTime = Calendar.getInstance();
        new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
                dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateTime.set(Calendar.MINUTE, minute);

                mTextView.setText(timeFormatter.format(dateTime.getTime())
                        .toString());
            }
        }, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE),
                false).show();
    }
}
