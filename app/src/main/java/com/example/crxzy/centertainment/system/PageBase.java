package com.example.crxzy.centertainment.system;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

abstract public class PageBase {
    public final MainActivity mActivity; //主Activity实列
    protected View mView;//当前控制器对应一级页面视图
    public QuickPageModel.Page mPageModel;
    Map <String, String[]> mPageMap = new LinkedHashMap <> ( );
    public MainApplication mApp;


    PageBase(MainActivity activity, QuickPageModel.Page pageModel) {
        mActivity = activity;
        mView = pageModel.mView;
        mPageModel = pageModel;
        mApp = (MainApplication) activity.getApplication ( );
        setPageMap (mPageMap);
    }

    public void setPageMap(Map <String, String[]> pageMap) {
        for (String key : mPageModel.mChildPages.keySet ( )) {
            mPageMap.put (key, new String[]{key});
        }
    }

    public void show() {
        onShow ( );
        try {
            if (mPageModel.mChildPages.size ( ) != 0) {
                QuickPageModel.Page child = mPageModel.getChild (mPageModel.currentChildIndex);
                Object controller = child.mController;
                Method method = controller.getClass ( ).getMethod ("show");
                method.invoke (controller);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace ( );
        }
    }

    public void onHeaderInitialize(View header) {
    }

    public void onHeaderShow(View header) {
    }

    public void selectPage(int index) {
        mPageModel.currentChildIndex = index;
        try {
            Object controller = mPageModel.getChild (index).mController;
            if (!mPageModel.getChild (index).mIsInitialize) {
                Method method = controller.getClass ( ).getMethod ("onInitiation");
                method.invoke (controller);
                mPageModel.getChild (index).mIsInitialize = true;
            }
            if (mPageModel.mIsInitialize) {
                Method method = controller.getClass ( ).getMethod ("show");
                method.invoke (controller);
                showHeader ( );
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace ( );
        }

    }

    private void showHeader() {
        QuickPageModel.Page pageOwner = getHeader ( );
        if (pageOwner != null) {
            try {
                if (!pageOwner.mIsHeaderInitialize) {
                    Method method = pageOwner.mController.getClass ( ).getMethod ("onHeaderInitialize", View.class);
                    method.invoke (pageOwner.mController, pageOwner.mHeader);
                }
                Method method = pageOwner.mController.getClass ( ).getMethod ("onHeaderShow", View.class);
                method.invoke (pageOwner.mController, pageOwner.mHeader);

                LinearLayout.LayoutParams headerParam = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                mActivity.mRootPage.mToolbarContainer.removeAllViews ( );
                mActivity.mRootPage.mToolbarContainer.addView (pageOwner.mHeader, headerParam);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace ( );
            }

        } else {
            mActivity.mRootPage.mToolbarContainer.setVisibility (View.GONE);
        }
    }

    public QuickPageModel.Page getHeader() {
        try {
            if (mPageModel.mHeaderModel == QuickPageModel.HEADER_MODEL_NONE) {
                return null;
            }

            if (mPageModel.mChildPages.size ( ) != 0) {
                Object controller = mPageModel.getChild (mPageModel.currentChildIndex).mController;
                Method method = controller.getClass ( ).getMethod ("getHeader");
                QuickPageModel.Page headerOwner = (QuickPageModel.Page) method.invoke (controller);

                if (headerOwner != null) {
                    return headerOwner;
                } else if (mPageModel.mHeader != null) {
                    return mPageModel;
                } else {
                    QuickPageModel.Page parents;
                    parents = mPageModel;
                    while (parents.mParent != null || parents.mHeaderModel == QuickPageModel.HEADER_MODEL_NONE) {
                        parents = parents.mParent;
                        if (Objects.requireNonNull (parents).mHeader != null) {
                            headerOwner = parents;
                            break;
                        }
                    }
                    return headerOwner;
                }
            } else {
                return null;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace ( );
            return null;
        }
    }

    abstract public void onInitiation();

    abstract public void onShow();

    public static abstract class PageHandler extends Handler {
        protected WeakReference <PageBase> mOuterClass;

        public PageHandler(PageBase page) {
            mOuterClass = new WeakReference <> (page);
        }

        @Override
        abstract public void handleMessage(Message msg);
    }
}
