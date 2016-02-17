package cn.guolf.android.common.util;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import cn.guolf.android.common.util.log.LogUtils;

/**
 * Author：guolf on 16/1/28 11:27
 * Email ：guo@guolingfa.cn
 */
public class Base64Utils {

    public static String encodeBase64File(File file){
        if(!file.exists()){
            return null;
        }
        String base64=null;
        try {
            FileInputStream inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
            base64 =  Base64.encodeToString(buffer,Base64.NO_WRAP);
            buffer=null;
        }catch (Exception ex){
            LogUtils.e(ex.toString());
        }
        return base64;
    }

    public static String encodeBase64Bitmap(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        return null;
    }
}
