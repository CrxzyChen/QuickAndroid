package com.example.crxzy.centertainment.system;

import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Tool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class FirstPageBase extends PageBase {
    private LinearLayout mBottomNavArea;
    private Map <String, String[]> mSecondPageMap = new LinkedHashMap <> ( );

    public FirstPageBase(AppCompatActivity context, View view, QuickPageModel.Page pageModel) {
        super (context, view, pageModel);
    }

    @Override
    public void onInitiation() {
        initSecondPageMap (mSecondPageMap);
        loadBottomNav ( );
        String firstPageName = mSecondPageMap.keySet ( ).iterator ( ).next ( );
        selectPage (firstPageName);
    }

    @Override
    public void onShow() {

    }

    private void selectPage(String key) {
        Object controller = Objects.requireNonNull (mPageModel.mChildPages.get (key)).mController;
        Method method = null;
        try {
            method = controller.getClass ( ).getMethod ("onInitiation");
            method.invoke (controller);
            ViewGroup view = mView.findViewById (Tool.getResId (mPageModel.mFileName + "_container", R.id.class));
            view.addView (mPageModel.getChild (key).mView);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace ( );
        }

    }

    private void loadBottomNav() {
        mBottomNavArea = mView.findViewById (Tool.getResId (mPageModel.mFileName + "_nav_area", R.id.class));//获取主页面底部导航栏容器
        for (String key : mSecondPageMap.keySet ( )) {
            addBottomNavItem (key, Objects.requireNonNull (mSecondPageMap.get (key)));
        }
    }

    public void initSecondPageMap(Map <String, String[]> secondPageMap) {
        for (String key : mPageModel.mChildPages.keySet ( )) {
            QuickPageModel.Page child = mPageModel.getChild (key);
            secondPageMap.put (key, new String[]{"#", key});
        }
    }

    private void addBottomNavItem(String key, String[] bottomNavItemContent) {
        /*
         * 添加外层线性布局
         */
        final LinearLayout tabLayout = new LinearLayout (mContext);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams (0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        tabLayout.setOrientation (LinearLayout.VERTICAL);
        tabLayout.setPadding (8, 8, 8, 8);
        /*
         * 添加Icon域
         */
        TextView tabIcon = new TextView (mContext);
        tabIcon.setTextAlignment (TextView.TEXT_ALIGNMENT_CENTER);
        LinearLayout.LayoutParams tabIconParams = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        tabIcon.setText (bottomNavItemContent[0]);
        tabIcon.setPadding (0, Tool.dip2px (mContext, 2), 0, 0);
        /*
         * 添加文字域
         */
        final TextView tabText = (TextView) new TextView (mContext);
        LinearLayout.LayoutParams tabTextParams = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        tabTextParams.gravity = Gravity.CENTER;
        tabText.setText (bottomNavItemContent[1]);
        tabText.setGravity (Gravity.CENTER);
        /*
         * 将文字域和图片域放入外层线性布局并将外层线性布局加入底部导航栏容器
         */
        tabLayout.addView (tabIcon, tabIconParams);
        tabLayout.addView (tabText, tabTextParams);
        tabLayout.setTag (key);
        tabLayout.setBackground (mContext.getDrawable (R.drawable.items_box));

        mBottomNavArea.addView (tabLayout, linearLayoutParams);
        /*
         * 添加底部导航栏点击事件
         */
        tabLayout.setOnClickListener (new BottomNavItemClickListener ( ));
    }

    class BottomNavItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText (mContext, (String) v.getTag ( ), Toast.LENGTH_SHORT).show ( );
        }
    }
}
