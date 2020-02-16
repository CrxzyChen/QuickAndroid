package com.example.crxzy.centertainment.system;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

abstract public class PageBase {
    protected final AppCompatActivity mContext; //主Activity实列
    protected View mView;//当前控制器对应一级页面视图
    protected QuickPageModel.Page mPageModel;

    PageBase(AppCompatActivity context, View view, QuickPageModel.Page pageModel) {
        mContext = context;
        mView = view;
        mPageModel = pageModel;
    }

    abstract public void onInitiation();
    abstract public void onShow() throws NoSuchMethodException;
}
