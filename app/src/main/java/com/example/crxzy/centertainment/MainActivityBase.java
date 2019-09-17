package com.example.crxzy.centertainment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivityBase extends AppCompatActivity {

    private Bundle mClassOnePages; //key:pagename value:LayoutResourceID
    private Bundle mClassSecondPages; //key:pagename value:LayoutResourceID
    private ArrayList <Object> mClassOneControllerList;  //存放各一级页面控制器的List
    private int mCurrentPageIndex = 0; //默认显示的一级加载页面
    private Bundle mNameToID; //存放各一级页面控制器在mClassOneControllerList中的位置
    private LinearLayout mMainTab;//底部导航栏View
    private LayoutDirectory ld;//布局文件结构类类实例
    public final String mMainLayoutName = "main";//布局文件根命名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.main);

        onInitiation ( );//调用子类初始化方法
    }

    public void onInitiation() {
        try {
            ld = new LayoutDirectory ( );//初始化布局文件结构类
            loadClassOnePage ( );//加载一级页面
            loadClassSecondPage ( );//加载二级页面
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace ( );
        }
    }

    /*
     * 加载一级页面方法
     */
    private void loadClassOnePage() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        mClassOnePages = ld.getClassOnePages ( );//获取所有页面
        mClassOneControllerList = new ArrayList <Object> ( );//创建一级页面控制器List
        mMainTab = (LinearLayout) findViewById (R.id.main_tab);//获取主页面底部导航栏容器
        mNameToID = new Bundle ( );//初始化一级页面控制器索引
        /*
         *    加载底部导航栏
         */
        int index = 0;
        for (String pagename : getPageOrder ( )) {//getPageOrder（）：获取页面的摆放顺序
            initPageController (pagename);//初始化页面控制器
            initTabButton (getTabButtonText (pagename), getTabButtonImageSrc (pagename), index);//初始化底部导航栏
            mNameToID.putInt (pagename, index);//添加一级页面控制器索引
            index++;
        }
        switchClassOnePage (mCurrentPageIndex);//默认显示的一级加载页面
    }

    /*
     * 获取底部导航栏按钮图片资源资源，默认为系统自带图标，如需加载其他图片可以重载该方法
     */
    public int getTabButtonImageSrc(String pagename) {
        return R.mipmap.ic_launcher;
    }

    /*
     * 获取底部导航栏按钮文字显示，默认为布局文件名，如需加载其他文字可以重载该方法
     */
    public String getTabButtonText(String pagename) {
        return pagename;
    }

    /*
     * 获取页面加载顺序，默认为文件布局文件排列顺序，如需更改可以重载此方法
     */
    public String[] getPageOrder() {
        String[] strings = new String[mClassOnePages.size ( )];
        int index = 0;
        for (String pageName : mClassOnePages.keySet ( )) {
            strings[index] = pageName;
            index++;
        }
        return strings;
    }

    /*
     * 加载二级页面方法
     */
    private void loadClassSecondPage() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        mClassSecondPages = ld.getClassSecondPage ( );
        /*
         *  classOnePageToClassSecondPage:
         *      key：<String>[一级页面名]
         *      value: <Bundle>[二级页面Bundle](pagenameToPageId):
         *          key: <String>[二级页面名]
         *          value: <Integer>[二级页面对应布局资源ID]
         */
        Bundle classOnePageToClassSecondPage = new Bundle ( );
        Bundle pagenameToPageId;
        for (String secondPageName : mClassSecondPages.keySet ( )) {
            String[] nameList = secondPageName.split ("\\.");//页面名称格式为[一级页面名].[二级页面名]，分割后nameList[0]为一级页面名，nameList[1]为二级页面名
            pagenameToPageId = (Bundle) classOnePageToClassSecondPage.get (nameList[0]);
            /*
             * 如果二级Bundle为空，则重新建立并放入一级页面Bundle中
             */
            if (null != pagenameToPageId) {
                pagenameToPageId.putInt (nameList[1], (int) mClassSecondPages.get (secondPageName));
            } else {
                pagenameToPageId = new Bundle ( );
                pagenameToPageId.putInt (nameList[1], (int) mClassSecondPages.get (secondPageName));
                classOnePageToClassSecondPage.putBundle (nameList[0], pagenameToPageId);
            }
        }
        /*
         * 调用各一级页面控制器来加载二级页面
         * 掉用方法为loadClassSecondPage（Bundle.class）
         */
        for (String classOnePagename : classOnePageToClassSecondPage.keySet ( )) {
            Object controller = getClassOneControllerByName (classOnePagename);
            Method method = controller.getClass ( ).getMethod ("loadClassSecondPage", Bundle.class);
            method.invoke (controller, classOnePageToClassSecondPage.get (classOnePagename));
        }
    }
    /*
     * 获取索引Index对应的mClassOneControllerList中一级页面控制器
     */
    private Object getClassOneControllerByName(String name) {
        return mClassOneControllerList.get ((int) mNameToID.get (name));
    }
    /*
     * 切换页面方法
     */
    public final void switchClassOnePage(int index) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = mClassOneControllerList.get (index).getClass ( ).getMethod ("show");//调用一级页面控制器中的show方法来改变页面
        method.invoke (mClassOneControllerList.get (index));
        onClassOneChange (index);
    }
    /*
     * 一级页面切换后的回调方法
     */
    private void onClassOneChange(int index) {
        /*
         * 将底部导航栏对应文字颜色进行变更，之前一级页面对应文字变为未选择颜色，当前页面的对应的文字变成选择状态
         */
        LinearLayout currentChild = (LinearLayout) mMainTab.getChildAt (mCurrentPageIndex);
        LinearLayout nextCurrentChild = (LinearLayout) mMainTab.getChildAt (index);
        TextView currentChildText = (TextView) currentChild.getChildAt (1);
        TextView nextChildText = (TextView) nextCurrentChild.getChildAt (1);
        currentChildText.setTextColor (getColor (R.color.colorText));
        nextChildText.setTextColor (getColor (R.color.colorPrimary));
        mCurrentPageIndex = index;//修改当前页面标识符
    }

    /***
     * 生成底部导航栏按钮
     * @param pagename
     * @param imageResource
     * @param index
     */
    private void initTabButton(String pagename, int imageResource, final int index) {
        /*
         * 添加外层线性布局
         */
        final LinearLayout tabLayout = new LinearLayout (this);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams (0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        tabLayout.setOrientation (LinearLayout.VERTICAL);
        tabLayout.setPadding (8, 8, 8, 8);
        /*
         * 添加图片域
         */
        ImageView tabIcon = (ImageView) new ImageView (this);
        LinearLayout.LayoutParams tabIconParams = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        tabIcon.setImageResource (imageResource);
        tabIcon.setPadding (0, 10, 0, 0);
        /*
         * 添加文字域
         */
        final TextView tabText = (TextView) new TextView (this);
        LinearLayout.LayoutParams tabTextParams = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        tabText.setText (pagename);
        tabText.setGravity (Gravity.CENTER);
        /*
         * 将文字域和图片域放入外层线性布局并将外层线性布局加入底部导航栏容器
         */
        tabLayout.addView (tabIcon, tabIconParams);
        tabLayout.addView (tabText, tabTextParams);
        mMainTab.addView (tabLayout, linearLayoutParams);
        /*
         * 添加底部导航栏点击事件
         */
        tabIcon.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                try {
                    MainActivityBase.this.switchClassOnePage (index);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace ( );
                }
            }
        });
    }

    /***
     * 初始化页面控制器
     * @param pagename
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     */
    private void initPageController(String pagename) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        StringBuilder className = new StringBuilder ( );//拼接字符出用StringBuilder
        /*
         * 将一级页面布局文件名转换成对应控制器名
         */
        for (String subPagename : pagename.split ("_")) {
            subPagename = "" + Character.toUpperCase (subPagename.charAt (0)) + subPagename.substring (1);//布局文件名首字母大写以对应控制器类
            className.append (subPagename);
        }
        //创建一级页面控制器
        View view = View.inflate (this, (int) mClassOnePages.get (pagename), null);//找到一级页面布局文件
        /*
         * 获取项目文件的命名空间，之后动态初始化类需要完整的类名包含命名空间名
         */
        String[] currentClassNamaList = this.getClass ( ).getName ( ).split ("\\.");
        Class instance;
        try {
            /*
             * 当前文件完整类名换成需要调用的类名
             */
            currentClassNamaList[currentClassNamaList.length - 1] = className.toString ( );
            instance = Class.forName (String.join (".", currentClassNamaList));
        } catch (ClassNotFoundException e) {
            /*
             * 当一级页面对用控制器类不存在时调用基类
             */
            currentClassNamaList[currentClassNamaList.length - 1] = "ClassOnePageBase";
            instance = Class.forName (String.join (".", currentClassNamaList));
        }
        /*
         * 动态实例化控制器类
         */
        Constructor constructor = instance.getDeclaredConstructor (AppCompatActivity.class, View.class,String.class);
        Object controller = constructor.newInstance (this, view, pagename);
        mClassOneControllerList.add (controller);
    }
    /*
     * 用于查询布局文件的内部类
     */
    private class LayoutDirectory {

        private Bundle mClassOnePageToID;//一级页面对应ID的Bundle
        private Bundle mClassSecondPageToID;//二级页面对应ID的Bundle


        LayoutDirectory() throws IllegalAccessException {
            mClassOnePageToID = new Bundle ( );
            mClassSecondPageToID = new Bundle ( );
            /*
             * 利用反射方法将R.layout类中所有方法属性查询出来
             *  根布局文件：[布局文件根命名]
             *      一级页面：[布局文件根命名]_[一级页面名]
             *      二级页面:[布局文件根命名]_[一级页面名]_[二级页面名]
             *  根据以上柜子找到所有的一级布局文件和二级布局文件
             */
            R.layout layout = new R.layout ( );
            Field[] fields = R.layout.class.getDeclaredFields ( );
            for (int i = 0, j = 0; i < fields.length; i++) {
                String filename = fields[i].getName ( );
                String[] filenameList = filename.split ("_");
                int fileId = (int) fields[i].get (layout);

                if (filenameList[0].equals (mMainLayoutName)) {
                    if (filenameList.length == 2) {
                        mClassOnePageToID.putInt (filenameList[1], fileId);
                    } else if (filenameList.length == 3) {
                        mClassSecondPageToID.putInt (filenameList[1] + "." + filenameList[2], fileId);
                    }
                }
            }
        }

        Bundle getClassOnePages() {
            return mClassOnePageToID;
        }

        Bundle getClassSecondPage() {
            return mClassSecondPageToID;
        }
    }
}
