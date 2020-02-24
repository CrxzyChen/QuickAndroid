package com.example.crxzy.centertainment.system;

import java.util.Map;

public class ThirdPageBase extends PageBase {

    public ThirdPageBase(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void setPageMap(Map <String, String[]> pageMap) {

    }


    /***
     * 初始化方法，可有子类重构用于初始化
     */
    public void onInitiation() {
    }

    /**
     * 二级页面添加到Activity后执行该函数
     */

    public void onShow() {
    }

}
