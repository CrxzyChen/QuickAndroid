package com.example.crxzy.centertainment.system;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

import com.example.crxzy.centertainment.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QuickPageModel {
    public static final int HEADER_MODEL_NONE = 0;
    public static final int HEADER_MODEL_INHERIT = 1;

    private Page mRoot = new Page (0, "index", "index", null, Setting.autoload.layout);
    private MainActivity mActivity;
    private List <String[]> mHeaderResourceList = new ArrayList <> ( );

    Page getRoot() {
        return mRoot;
    }

    public class Page {
        int mResId;
        int mPageLevel;
        int currentChildIndex = 0;
        int mHeaderModel = HEADER_MODEL_INHERIT;
        boolean isInitialize = false;
        String mFileName;
        String mPageName;
        String mAliasName;
        String mControllerClassName;
        String mControllerProtoClassName;
        View mView;
        View mHeader;
        Object mController;
        Page mParent;
        Map <String, Page> mChildPages = new HashMap <> ( );
        private Map <String, Integer> mKeyToIndex = new HashMap <> ( );
        private SparseArray <String> mIndexToKey = new SparseArray <> ( );

        Page(int pageLevel, String pageName, String fileName, Page parent, int resId) {
            mPageLevel = pageLevel;
            mAliasName = mPageName = pageName;
            mFileName = fileName;
            mResId = resId;
            mParent = parent;
            String[] mFileNameArray = mFileName.split ("_");
            String className = mFileNameArray[mFileNameArray.length - 1];
            mFileNameArray[mFileNameArray.length - 1] = "" + Character.toUpperCase (className.charAt (0)) + className.substring (1);
            String[] currentClassNameList = this.getClass ( ).getName ( ).split ("\\.");
            String[] targetClassNameList = new String[currentClassNameList.length + mFileNameArray.length - 1];
            System.arraycopy (currentClassNameList, 0, targetClassNameList, 0, currentClassNameList.length - 2);
            targetClassNameList[currentClassNameList.length - 2] = "controllers";
            System.arraycopy (mFileNameArray, 0, targetClassNameList, currentClassNameList.length - 1, mFileNameArray.length);

            mControllerClassName = String.join (".", targetClassNameList);
            if (mPageLevel == 0) {
                currentClassNameList[currentClassNameList.length - 1] = "ZeroPageBase";
                mControllerProtoClassName = String.join (".", currentClassNameList);
            } else if (mPageLevel == 1) {
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

        Page getChild(int index) {
            return getChild (mIndexToKey.get (index));
        }

        void addChild(String key, Page page) {
            mChildPages.put (key, page);
        }

        Page getChild(String key) {
            return mChildPages.get (key);
        }

        void loadView() {
            if (mPageName.equals ("index")) {
                mView = mActivity.getWindow ( ).getDecorView ( );
            } else {
                mView = LayoutInflater.from (mActivity).inflate (mResId, null);
            }
            for (String key : mChildPages.keySet ( )) {
                Objects.requireNonNull (mChildPages.get (key)).loadView ( );
            }
        }

        void loadController() {
            try {
                Constructor constructor;
                try {
                    Class <?> _class = Class.forName (mControllerClassName);
                    constructor = _class.getConstructor (MainActivity.class, Page.class);
                } catch (ClassNotFoundException e) {
                    Class <?> _class = Class.forName (mControllerProtoClassName);
                    constructor = _class.getConstructor (MainActivity.class, Page.class);
                }
                mController = constructor.newInstance (mActivity, this);
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
                e.printStackTrace ( );
            }

            for (String key : mChildPages.keySet ( )) {
                Objects.requireNonNull (mChildPages.get (key)).loadController ( );
            }
        }

        void loadPageMap() {
            int index = 0;
            for (String key : ((PageBase) this.mController).mPageMap.keySet ( )) {
                setKeyIndex (key, index++);
                this.getChild (key).mAliasName = Objects.requireNonNull (((PageBase) this.mController).mPageMap.get (key))[0];
                this.getChild (key).loadPageMap ( );
            }
        }

        void setKeyIndex(String key, int index) {
            mKeyToIndex.put (key, index);
            mIndexToKey.put (index, key);
        }

        String getKey(int index) {
            return Objects.requireNonNull (mIndexToKey.get (index));
        }

        int getIndex(String tag) {
            return Objects.requireNonNull (mKeyToIndex.get (tag));
        }

        void loadHeader() {
            for (String[] headerResource : mHeaderResourceList) {
                int resId = Integer.parseInt (headerResource[0]);
                Page page = mRoot;
                if (headerResource.length == 1) {
                    page.mHeader = LayoutInflater.from (mActivity).inflate (resId, null);
                } else {
                    for (int index = 1; index < headerResource.length; index++) {
                        if (page.mChildPages.keySet ( ).contains (headerResource[index])) {
                            page = page.getChild (headerResource[index]);
                            if (index == headerResource.length - 1) {
                                page.mHeader = LayoutInflater.from (mActivity).inflate (resId, null);
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }

    }

    QuickPageModel(MainActivity activity) {
        mActivity = activity;
        R.layout layout = new R.layout ( );
        Field[] fields = R.layout.class.getDeclaredFields ( );
        for (Field field : fields) {
            String fileName = field.getName ( );
            String[] filenameList = fileName.split ("_");
            try {
                if (filenameList[0].equals (Setting.autoload.keyword.header)) {
                    int resId = (int) field.get (layout);
                    filenameList[0] = Integer.toString (resId);
                    mHeaderResourceList.add (filenameList);
                } else if (Arrays.asList (Setting.autoload.keyword.page).contains (filenameList[0])) {
                    int resId = (int) field.get (layout);
                    Page page = mRoot;
                    for (int index = 0; index < filenameList.length; index++) {
                        if (index == filenameList.length - 1) {
                            page.addChild (filenameList[index], new Page (index + 1, filenameList[index], fileName, page, resId));
                        }
                        page = page.getChild (filenameList[index]);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace ( );
            }
        }
        mRoot.loadView ( );
        mRoot.loadController ( );
        mRoot.loadPageMap ( );
        mRoot.loadHeader ( );
    }

}
