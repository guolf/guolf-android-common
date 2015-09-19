package cn.guolf.android.common.updatehelper;

/**
 * Author：guolf on 9/19/15 16:20
 * Email ：guo@guolingfa.cn
 */
public class UpdateInfoBean {
    private String versionCode = "0";
    private String versionName = "", whatsNew = "", downUrl = "";


    /**
     * @return 如果versionCode非法, 返回0
     */
    public int getVersionCode() {
        int code = 0;
        try {
            code = Integer.valueOf(versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getWhatsNew() {
        return whatsNew;
    }

    public void setWhatsNew(String whatsNew) {
        this.whatsNew = whatsNew;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }
}
