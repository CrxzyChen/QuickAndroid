package com.example.crxzy.centertainment.system;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ThirdPageBase extends PageBase {

    public ThirdPageBase(ActivityBase context, View view, QuickPageModel.Page pageModel) {
        super (context, view, pageModel);
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
