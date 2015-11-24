package cn.guolf.android.common.updatehelper;

import android.app.Activity;
import android.widget.Toast;

import cn.guolf.android.common.util.AlertInfoTool;
import cn.guolf.android.common.util.log.LogUtils;

/**
 * Author：guolf on 9/19/15 16:19
 * Email ：guo@guolingfa.cn
 */
public abstract class UpdateListener {

    private Activity mActiviy;

    public UpdateListener(Activity act) {
        this.mActiviy = act;
    }

    /**
     * 可以在这提示用户正在检查更新
     */
    public void onCheckStart() {

    }

    /**
     * 如果用户选择下载更新,这个方法会被调用
     */
    public void onDownloadStart() {

    }

    /**
     * 检查完成(没有可用更新,或者检查更新中途出现异常)
     * 例如你是在程序Loading界面进行检查更新,那么现在可以跳过Loading进入程序首页了
     */
    public void onCheckFinish() {

    }

    /**
     * 更新出错
     */
    public void onUpdateError(Exception ex) {
        LogUtils.e("更新出错：" + ex.getMessage());
        Toast.makeText(mActiviy.getApplicationContext(), "更新出错：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 更新事件监听
     */
    public void onUpdateClick(UpdateInfoBean bean) {
        if (bean.isFlag()) {
            AlertInfoTool.alert(mActiviy, "升级提醒", "为了更好的体验，请升级APP", new AlertInfoTool.AlertInfoToolOper() {
                @Override
                public void operate() {
                    mActiviy.finish();
                }
            });
        }
    }
}
