package cn.guolf.android.common.updatehelper;

import org.json.JSONException;
import org.json.JSONObject;

import cn.guolf.android.common.util.log.LogUtils;

/**
 * Author：guolf on 16/3/23 16:25
 * Email ：guo@guolingfa.cn
 */
public class DefaultUpdateInfoParser implements AbsUpdateInfoParser {

    @Override
    public UpdateInfoBean parse(String info) {

        UpdateInfoBean bean = new UpdateInfoBean();
        try {
            JSONObject json = new JSONObject(info);
            bean.setFlag(json.getBoolean("flag"));
            bean.setVersionName(json.getString("version_name"));
            bean.setVersionCode(json.getInt("version_code"));
            bean.setDownUrl(json.getString("apkUrl"));
            bean.setWhatsNew(json.getString("msg"));
            bean.setSha1(json.getString("sha1"));
            return bean;
        } catch (JSONException ex) {
            LogUtils.i(ex);
        }
        return null;
    }
}
