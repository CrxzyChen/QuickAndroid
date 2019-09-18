package com.example.crxzy.centertainment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class ClassOnePageBase {
    private final AppCompatActivity mContext; //主Activity实列
    public final View mView;//当前控制器对应一级页面视图
    private final LinearLayout mMainContainer;//主视图中的一级页面容器
    private final LinearLayout.LayoutParams mParams;//主视图中的一级页面容器的布局文件
    private final String mCurrentPageName;
    private ArrayList <View> mNavItemList;//顶部导航栏List
    private ViewPager mViewPager;//一级页面中ViewPager视图
    private List <View> mClassSecondPageViewList;//该一级页面下的二级页面视图List
    private int mCurrentClassSecendPage = 0;//默认显示的二级页面
    private Bundle mClassSecondPages;//该一级页面下的二级页面Bundle
    private ArrayList <Object> mClassSecondControllerList;  //存放各二级页面控制器的List

    /***
     * 构造方法
     * @param context
     * @param view
     */
    ClassOnePageBase(AppCompatActivity context, View view, String currentPageName) {
        mContext = context;
        mView = view;
        mCurrentPageName = currentPageName;
        mMainContainer = (LinearLayout) mContext.findViewById (R.id.main_container);
        mParams = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mClassSecondControllerList = new ArrayList <Object> ( );
        onInitiation ( );
    }

    /***
     * 初始化方法，可有子类重构用于初始化
     */
    protected void onInitiation() { }

    /***
     * 显示当前主视图
     */
    public void show() {
        mMainContainer.removeAllViews ( );//将一级页面放置容器清空
        mMainContainer.addView (mView, mParams);//将当前一级页面放置其中
    }

    /***
     * 用于加载二级页面
     * @param classSecondPage
     */
    public void loadClassSecondPage(Bundle classSecondPage) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        mNavItemList = new ArrayList <View> ( );
        mClassSecondPages = classSecondPage;
        final int resId = getResId (mCurrentPageName + "_viewpager", R.id.class);//根据当前一级页面名称生成id字符串，并利用该字符串获取viewpager视图
        mViewPager = mView.findViewById (resId);
        //创建导航栏
        LinearLayout linearLayout = createNavBar ( );

        mClassSecondPageViewList = new ArrayList <View> ( );
        int index = 0;
        for (String pagename : getPageOrder ( )) { //getPageOrder获取页面的排列顺序
            View view = View.inflate (mContext, (int) mClassSecondPages.get (pagename), null);
            String[] currentClassNamaList = this.getClass ( ).getName ( ).split ("\\.");

            /**
             *  实列化二级页面控制器，如果对应类存在则实列化，若不存在这实列化基类
             */
            String className = "" + Character.toUpperCase (pagename.charAt (0)) + pagename.substring (1);//布局文件名首字母大写以对应控制器类
            Class instance;
            try {
                /*
                 * 当前文件完整类名换成需要调用的类名
                 */
                currentClassNamaList[currentClassNamaList.length - 1] = currentClassNamaList[currentClassNamaList.length - 1] + className;
                instance = Class.forName (String.join (".", currentClassNamaList));
            } catch (ClassNotFoundException e) {
                /*
                 * 当一级页面对用控制器类不存在时调用基类
                 */
                currentClassNamaList[currentClassNamaList.length - 1] = "ClassSecondPageBase";
                instance = Class.forName (String.join (".", currentClassNamaList));
            }
            /*
             * 动态实例化控制器类
             */
            Constructor constructor = instance.getDeclaredConstructor (AppCompatActivity.class, View.class, String.class);
            Object controller = constructor.newInstance (mContext, view, pagename);

            mClassSecondPageViewList.add (view);//向二级页面视图容器内添加视图
            mClassSecondControllerList.add (controller);//向二级页面控制器容器内添加视图

            View nav_item = newNavItem (getNavText (pagename));
            final int finalIndex = index;
            nav_item.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem (finalIndex);
                }
            });
            mNavItemList.add (nav_item);
            linearLayout.addView (nav_item);
            index++;
        }
        mViewPager.setAdapter (getPagerAdapter ( ));
        mViewPager.addOnPageChangeListener (getOnPageChangeListener ( ));

        ((TextView) mNavItemList.get (mCurrentClassSecendPage)).setTextColor (ContextCompat.getColor (mContext, R.color.colorPrimary));
    }

    /*
     * 获取页面加载顺序，默认为文件布局文件排列顺序，如需更改可以重载此方法
     */
    protected String[] getPageOrder() {
        String[] strings = new String[mClassSecondPages.size ( )];
        int index = 0;
        for (String pageName : mClassSecondPages.keySet ( )) {
            strings[index] = pageName;
            index++;
        }
        return strings;
    }

    /*
     * 获取二级页面导航栏按钮文字显示，默认为布局文件名，如需加载其他文字可以重载该方法
     */
    protected String getNavText(String s) {
        return s;
    }

    @NonNull
    private ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return new ViewPager.OnPageChangeListener ( ) {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                ((TextView) mNavItemList.get (mCurrentClassSecendPage)).setTextColor (ContextCompat.getColor (mContext, R.color.colorText));
                ((TextView) mNavItemList.get (i)).setTextColor (ContextCompat.getColor (mContext, R.color.colorPrimary));
                mCurrentClassSecendPage = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        };
    }

    @NonNull
    private PagerAdapter getPagerAdapter() {
        return new PagerAdapter ( ) {
            @Override
            public int getCount() {
                return mClassSecondPageViewList.size ( );
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView (mClassSecondPageViewList.get (position)); //获取二级当前页面视图
                Object controller = mClassSecondControllerList.get (position); //获取当前二级页面控制器

                /**
                 * 调用控制器二级内页面加载完毕后方法
                 */
                try {
                    Method method = controller.getClass ( ).getDeclaredMethod ("onShow");
                    method.invoke (controller);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace ( );
                }
                return mClassSecondPageViewList.get (position);
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView ((View) object);
            }

        };
    }

    /***
     * 创建二级页面导航栏
     * @return
     */
    private LinearLayout createNavBar() {
        int index = ((ViewGroup) mView).indexOfChild (mViewPager);
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView (mContext);
        HorizontalScrollView.LayoutParams HSV_params = new HorizontalScrollView.LayoutParams (HorizontalScrollView.LayoutParams.MATCH_PARENT, dip2px (mContext, 40));
        horizontalScrollView.setBackgroundColor (ContextCompat.getColor (mContext, R.color.colorBackground));
        LinearLayout linearLayout = new LinearLayout (mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setOrientation (LinearLayout.HORIZONTAL);
        horizontalScrollView.addView (linearLayout, layoutParams);
        ((ViewGroup) mView).addView (horizontalScrollView, index, HSV_params);
        return linearLayout;
    }

    /***
     * 创建二级页面导航栏选项
     * @param text
     * @return
     */
    @NonNull
    private TextView newNavItem(String text) {
        TextView textView = new TextView (mContext);
        textView.setHeight (dip2px (mContext, 40));

        textView.setTextSize (16);
        textView.setPadding (20, 8, 20, 8);
        textView.setGravity (Gravity.CENTER);
        textView.setText (text);
        return textView;
    }

    private int getResId(String variableName, Class <?> c) {
        try {
            Field idField = c.getDeclaredField (variableName);
            return idField.getInt (idField);
        } catch (Exception e) {
            e.printStackTrace ( );
            return -1;
        }
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources ( ).getDisplayMetrics ( ).density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources ( ).getDisplayMetrics ( ).density;
        return (int) (pxValue / scale + 0.5f);
    }
}
