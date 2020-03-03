package com.example.crxzy.centertainment.system;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Tool;

import java.util.Map;
import java.util.Objects;

public class FirstPageBase extends PageBase {
    private LinearLayout mBottomNavArea;

    public FirstPageBase(MainActivity activity,  QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void setPageMap(Map <String, String[]> pageMap) {
        for (String key : mPageModel.mChildPages.keySet ( )) {
            pageMap.put (key, new String[]{"#", key});
        }
    }

    @Override
    public void onInitiation() {
        if (mPageModel.mChildPages.size ( ) != 0) {
            loadBottomNav ( );
            selectPage (mPageModel.currentChildIndex);
        }
    }

    @Override
    public void onShow() {
    }

    @Override
    public void selectPage(int index) {
        ViewGroup view = mView.findViewById (Tool.getResId (mPageModel.mFileName + "_container", R.id.class));
        view.removeAllViews ( );
        view.addView (mPageModel.getChild (index).mView);

        ViewGroup currentSelectItem = (ViewGroup) mBottomNavArea.getChildAt (mPageModel.currentChildIndex);
        ((TextView) currentSelectItem.getChildAt (0)).setTextColor (mActivity.getColor (R.color.colorText));
        ((TextView) currentSelectItem.getChildAt (1)).setTextColor (mActivity.getColor (R.color.colorText));

        ViewGroup targetItem = (ViewGroup) mBottomNavArea.getChildAt (index);
        ((TextView) targetItem.getChildAt (0)).setTextColor (mActivity.getColor (R.color.colorPrimaryDark));
        ((TextView) targetItem.getChildAt (1)).setTextColor (mActivity.getColor (R.color.colorPrimaryDark));

        super.selectPage (index);

    }

    private void loadBottomNav() {
        mBottomNavArea = mView.findViewById (Tool.getResId (mPageModel.mFileName + "_nav_area", R.id.class));//获取主页面底部导航栏容器
        for (String key : mPageMap.keySet ( )) {
            addBottomNavItem (key, Objects.requireNonNull (mPageMap.get (key)));
        }
    }


    private void addBottomNavItem(String key, String[] bottomNavItemContent) {
        /*
         * 添加外层线性布局
         */
        final LinearLayout tabLayout = new LinearLayout (mActivity);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams (0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        tabLayout.setOrientation (LinearLayout.VERTICAL);
        tabLayout.setPadding (8, 8, 8, 8);
        /*
         * 添加Icon域
         */
        TextView tabIcon = new TextView (mActivity);
        tabIcon.setTextAlignment (TextView.TEXT_ALIGNMENT_CENTER);
        LinearLayout.LayoutParams tabIconParams = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        tabIcon.setText (bottomNavItemContent[1]);
        tabIcon.setTextColor (mActivity.getColor (R.color.colorText));
        tabIcon.setPadding (0, Tool.dip2px (mActivity, 2), 0, 0);
        /*
         * 添加文字域
         */
        TextView tabText = new TextView (mActivity);
        LinearLayout.LayoutParams tabTextParams = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        tabTextParams.gravity = Gravity.CENTER;
        tabText.setTextColor (mActivity.getColor (R.color.colorText));
        tabText.setText (bottomNavItemContent[0]);
        tabText.setGravity (Gravity.CENTER);
        /*
         * 将文字域和图片域放入外层线性布局并将外层线性布局加入底部导航栏容器
         */
        tabLayout.addView (tabIcon, tabIconParams);
        tabLayout.addView (tabText, tabTextParams);
        tabLayout.setTag (key);
        tabLayout.setBackground (mActivity.getDrawable (R.drawable.manga_self_card_backgroud));

        mBottomNavArea.addView (tabLayout, linearLayoutParams);
        /*
         * 添加底部导航栏点击事件
         */
        tabLayout.setOnClickListener (new BottomNavItemClickListener ( ));
    }

    class BottomNavItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            selectPage (mPageModel.getIndex ((String) v.getTag ( )));
        }
    }
}
