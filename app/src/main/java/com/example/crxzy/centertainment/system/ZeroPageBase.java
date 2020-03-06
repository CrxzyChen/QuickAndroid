package com.example.crxzy.centertainment.system;

import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Tool;

import java.util.Objects;

public class ZeroPageBase extends PageBase {
    private LinearLayout mLeftNavArea;
    private RelativeLayout mLeftWindow;
    LinearLayout mToolbarContainer;
    public DrawerLayout mMainLayout;

    public ZeroPageBase(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void onInitiation() {
        loadMainWindow ( );
        loadLeftWindow ( );
        mPageModel.mIsInitialize =true;
        selectPage (0);
    }

    @Override
    public void selectPage(int index) {
        String key = mPageModel.getKey (index);
        RelativeLayout relativeLayout = mActivity.findViewById (R.id.root_container);
        relativeLayout.removeAllViews ( );
        relativeLayout.addView (Objects.requireNonNull (mPageModel.mChildPages.get (key)).mView);

        ViewGroup currentSelectItem = (ViewGroup) mLeftNavArea.getChildAt (mPageModel.currentChildIndex);
        ((TextView) currentSelectItem.getChildAt (0)).setTextColor (mActivity.getColor (R.color.black));
        ((TextView) currentSelectItem.getChildAt (1)).setTextColor (mActivity.getColor (R.color.black));

        ViewGroup targetItem = (ViewGroup) mLeftNavArea.getChildAt (index);
        ((TextView) targetItem.getChildAt (0)).setTextColor (mActivity.getColor (R.color.colorPrimaryDark));
        ((TextView) targetItem.getChildAt (1)).setTextColor (mActivity.getColor (R.color.colorPrimaryDark));

        if (mLeftWindow.getVisibility ( ) == View.VISIBLE) {
            mMainLayout.closeDrawer (mLeftWindow);
        }
        super.selectPage (index);
    }

    @Override
    public void onShow() {

    }

    public boolean closeDrawer() {
        if (mLeftWindow.getVisibility ( ) == View.VISIBLE) {
            mMainLayout.closeDrawer (mLeftWindow);
            return true;
        }
        return false;
    }

    private void loadMainWindow() {
        mMainLayout = mActivity.findViewById (R.id.root_layout);
        mToolbarContainer = mActivity.findViewById (R.id.root_toolbar_container);
    }

    private void loadLeftWindow() {
        mLeftWindow = mView.findViewById (R.id.main_layout_left);
        mLeftNavArea = mView.findViewById (R.id.root_layout_left_nav_area);
        for (String key : mPageMap.keySet ( )) {
            addSubPageNav (key, Objects.requireNonNull (mPageMap.get (key)));
        }
    }

    private void addSubPageNav(String key, String[] itemContent) {
        LinearLayout item = new LinearLayout (mActivity);
        item.setOrientation (LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams itemLayoutParam = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, Tool.dip2px (mActivity, 50));
        item.setBackground (mActivity.getDrawable (R.drawable.drawer_item));
        item.setLayoutParams (itemLayoutParam);
        item.setTag (key);

        TextView icon = new TextView (mActivity);
        LinearLayout.LayoutParams iconLayoutParam = new LinearLayout.LayoutParams (0, RelativeLayout.LayoutParams.MATCH_PARENT, 1);
        icon.setLayoutParams (iconLayoutParam);
        icon.setText (itemContent[1]);
        icon.setGravity (Gravity.CENTER);
        icon.setTextColor (mActivity.getColor (R.color.black));
        item.addView (icon);

        TextView text = new TextView (mActivity);
        LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams (0, RelativeLayout.LayoutParams.MATCH_PARENT, 4);
        text.setText (itemContent[0]);
        text.setGravity (Gravity.CENTER_VERTICAL);
        text.setLayoutParams (textLayoutParam);
        text.setTextColor (mActivity.getColor (R.color.black));
        item.addView (text);
        mLeftNavArea.addView (item);
        item.setOnClickListener (new LeftNavItemClickListener ( ));
    }


    class LeftNavItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            selectPage (mPageModel.getIndex ((String) v.getTag ( )));
        }
    }
}
