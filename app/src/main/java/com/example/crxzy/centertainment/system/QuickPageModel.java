package com.example.crxzy.centertainment.system;

import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Tool;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QuickPageModel {
    private Page mRoot = new Page (0, "root", R.layout.root);
    private AppCompatActivity mContext;
    private Map <String, String[]> mFirstPageMap;

    public Page getRoot() {
        return mRoot;
    }

    public class Page {
        String mFileName;
        int mResId;
        View mView;
        String mControllerClassName;
        String mControllerProtoClassName;
        Object mController;
        Map <String, Page> mChildPages = new HashMap <> ( );
        int mPageLevel = 0;

        Page(int pageLevel, String fileName, int resId) {
            mPageLevel = pageLevel;
            mFileName = fileName;
            mResId = resId;
            String[] mFileNameArray = mFileName.split ("_");
            String className = mFileNameArray[mFileNameArray.length - 1];
            mFileNameArray[mFileNameArray.length - 1] = "" + Character.toUpperCase (className.charAt (0)) + className.substring (1);
            String[] currentClassNameList = this.getClass ( ).getName ( ).split ("\\.");
            String[] targetClassNameList = new String[currentClassNameList.length + mFileNameArray.length - 1];
            System.arraycopy (currentClassNameList, 0, targetClassNameList, 0, currentClassNameList.length - 2);
            targetClassNameList[currentClassNameList.length - 2] = "controllers";
            System.arraycopy (mFileNameArray, 0, targetClassNameList, currentClassNameList.length - 1, mFileNameArray.length);

            mControllerClassName = String.join (".", targetClassNameList);
            if (mPageLevel == 1) {
                currentClassNameList[currentClassNameList.length - 1] = "FirstPageBase";
                mControllerProtoClassName = String.join (".", currentClassNameList);
            } else if (mPageLevel == 2) {
                currentClassNameList[currentClassNameList.length - 1] = "SecondPageBase";
                mControllerProtoClassName = String.join (".", currentClassNameList);

            } else if (mPageLevel == 3) {
                currentClassNameList[currentClassNameList.length - 1] = "ThirdPageBase";
                mControllerProtoClassName = String.join (".", currentClassNameList);
            }
        }

        void addChild(String key, Page page) {
            mChildPages.put (key, page);
        }

        Page getChild(String key) {
            return mChildPages.get (key);
        }

        void loadView() {
            mView = LayoutInflater.from (mContext).inflate (mResId, null);
            for (String key : mChildPages.keySet ( )) {
                Objects.requireNonNull (mChildPages.get (key)).loadView ( );
            }
        }

        void loadController(Page page) throws ClassNotFoundException {
            if (!mFileName.equals ("root")) {
                Class _class = null;
                try {
                    _class = Class.forName (mControllerClassName);
                } catch (ClassNotFoundException e) {
                    _class = Class.forName (mControllerProtoClassName);
                }
                try {
                    Constructor constructor = _class.getConstructor (AppCompatActivity.class, View.class, Page.class);
                    mController = constructor.newInstance (mContext, mView, page);
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.printStackTrace ( );
                }
            }


            for (String key : mChildPages.keySet ( )) {
                Objects.requireNonNull (mChildPages.get (key)).loadController (mChildPages.get (key));
            }
        }

    }

    QuickPageModel(AppCompatActivity context, Map <String, String[]> firstPageMap) {
        mContext = context;
        mFirstPageMap = firstPageMap;
        R.layout layout = new R.layout ( );
        Field[] fields = R.layout.class.getDeclaredFields ( );
        for (int i = 0, j = 0; i < fields.length; i++) {
            String fileName = fields[i].getName ( );
            String[] filenameList = fileName.split ("_");
            try {
                if (mFirstPageMap.keySet ( ).contains (filenameList[0])) {
                    int resId = (int) fields[i].get (layout);
                    if (filenameList.length == 1) {
                        mRoot.addChild (fileName, new Page (1, fileName, resId));
                    } else if (filenameList.length == 2) {
                        Objects.requireNonNull (mRoot.getChild (filenameList[0])).addChild (filenameList[1], new Page (2, fileName, resId));
                    } else if (filenameList.length == 3) {
                        Objects.requireNonNull (mRoot.getChild (filenameList[0])).getChild (filenameList[1]).addChild (filenameList[2], new Page (3, fileName, resId));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace ( );
            }
        }
        try {
            mRoot.loadView ( );
            mRoot.loadController (mRoot);
        } catch (ClassNotFoundException e) {
            e.printStackTrace ( );
        }
    }

}
