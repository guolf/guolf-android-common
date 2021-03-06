package cn.guolf.android.common.updatehelper;

public interface AbsUpdateInfoParser {
    /**
     * 把查询到的更新信息解析为JavaBean.请根据实际情况实现此方法.
     *
     * @param info 从服务器获取的信息
     * @return 存储了更新信息的JavaBean, 如果解析出错, 可以返回null
     */
    UpdateInfoBean parse(String info);

}
