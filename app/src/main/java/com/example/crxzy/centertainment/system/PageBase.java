package com.example.crxzy.centertainment.system;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.crxzy.centertainment.MainApplication;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

abstract public class PageBase {
    public final ActivityBase mContext; //主Activity实列
    protected View mView;//当前控制器对应一级页面视图
    protected QuickPageModel.Page mPageModel;
    public MainApplication mApp;
    private Set <Integer> mAlreadyInitiation = new HashSet <> ( );

    PageBase(ActivityBase context, View view, QuickPageModel.Page pageModel) {
        mContext = context;
        mView = view;
        mPageModel = pageModel;
        mApp = (MainApplication) mContext.getApplication ( );
    }

    public void show() {
        try {
            if (mPageModel.mChildPages.size ( ) != 0) {
                Object controller = Objects.requireNonNull (mPageModel.getChild (mPageModel.currentChildIndex)).mController;
                Method method = controller.getClass ( ).getMethod ("show");
                method.invoke (controller);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace ( );
        }
        onShow ( );
    }

    public void selectPage(int index) {
        try {
            if (!mAlreadyInitiation.contains (index)) {
                Object controller = mPageModel.getChild (index).mController;
                Method method = controller.getClass ( ).getMethod ("onInitiation");
                method.invoke (controller);
                mAlreadyInitiation.add (index);
            }
            Object controller = Objects.requireNonNull (mPageModel.getChild (index)).mController;
            Method method = controller.getClass ( ).getMethod ("show");
            method.invoke (controller);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace ( );
        }
        mPageModel.currentChildIndex = index;
    }

    abstract public void onInitiation();

    abstract public void onShow();

    public static abstract class PageHandler extends Handler {
        public WeakReference <PageBase> mOuterClass;

        public PageHandler(PageBase activity) {
            mOuterClass = new WeakReference <> (activity);
        }

        @Override
        abstract public void handleMessage(Message msg);
    }
}
