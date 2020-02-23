package com.example.crxzy.centertainment.system;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Tool;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SecondPageBase extends PageBase {
    private List <View> mSecondPageViewList = new ArrayList <> ( );//该一级页面下的二级页面视图List
    private ViewPager mViewPager;//一级页面中ViewPager视图
    private Map <String, String[]> mThirdPageMap = new LinkedHashMap <> ( );
    private ViewGroup mTopNavArea;

    public SecondPageBase(ActivityBase context, View view, QuickPageModel.Page pageModel) {
        super (context, view, pageModel);
    }


    @Override
    public void onInitiation() {
        initThirdPageMap (mThirdPageMap);
        if (mPageModel.mChildPages.size ( ) != 0) {
            loadTopNav ( );
            loadViewPager ( );
            selectPage (0);
        }
    }

    @Override
    public void onShow() {
        if(mContext.mIsAutoTitle){
        }
    }

    private void loadViewPager() {
        mViewPager = mView.findViewById (Tool.getResId (mPageModel.mFileName + "_container", R.id.class));
        for (String key : mThirdPageMap.keySet ( )) {
            mSecondPageViewList.add (mPageModel.getChild (key).mView);
        }
        mViewPager.setAdapter (new SecondPagerAdapter ( ));
        mViewPager.addOnPageChangeListener (new SecondPagerChangeListener ( ));
    }

    class SecondPagerAdapter extends android.support.v4.view.PagerAdapter {
        @Override
        public int getCount() {
            return mSecondPageViewList.size ( );
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView (mSecondPageViewList.get (position)); //获取二级当前页面视图
            return mSecondPageViewList.get (position);
        }

        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView ((View) object);
        }

    }

    class SecondPagerChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
            selectPage (i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

    private void loadTopNav() {
        mTopNavArea = mView.findViewById (Tool.getResId (mPageModel.mFileName + "_nav_area", R.id.class));//获取主页面底部导航栏容器
        int index = 0;
        for (String key : mThirdPageMap.keySet ( )) {
            addTopNavItem (index, Objects.requireNonNull (mThirdPageMap.get (key)));
            mPageModel.getChild (key).setPageName (Objects.requireNonNull (mThirdPageMap.get (key))[0]);
            mPageModel.setKeyIndex (key, index++);
        }
    }

    private void addTopNavItem(int key, String[] topNavItemContent) {
        TextView textView = new TextView (mContext);
        textView.setHeight (Tool.dip2px (mContext, 40));

        textView.setTextSize (16);
        textView.setPadding (20, 8, 20, 8);
        textView.setGravity (Gravity.CENTER);
        textView.setText (topNavItemContent[0]);
        textView.setTag (key);
        textView.setTextColor (mContext.getColor (R.color.colorText));
        mTopNavArea.addView (textView);

        textView.setOnClickListener (new TopNavItemClickListener ( ));
    }

    @Override
    public void selectPage(int index) {
        mViewPager.setCurrentItem (index);

        TextView currentSelectItem = (TextView) mTopNavArea.getChildAt (mPageModel.currentChildIndex);
        currentSelectItem.setTextColor (mContext.getColor (R.color.colorText));

        TextView targetItem = (TextView) mTopNavArea.getChildAt (index);
        targetItem.setTextColor (mContext.getColor (R.color.colorPrimaryDark));
        super.selectPage (index);
    }

    class TopNavItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            selectPage ((int) v.getTag ( ));
        }
    }

    public void initThirdPageMap(Map <String, String[]> mThirdPageMap) {
        for (String key : mPageModel.mChildPages.keySet ( )) {
            mThirdPageMap.put (key, new String[]{key});
        }
    }
}
