package com.example.crxzy.centertainment.system;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Tool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

abstract public class ActivityBase extends AppCompatActivity {
    public Context mContext;
    private DrawerLayout mMainLayout;
    public QuickPageModel mQuickPageModel;
    public QuickPageModel.Page mQuickPageModelRoot;
    public Map <String, String[]> mFirstPageMap = new LinkedHashMap <> ( );
    public Set <String> mAlreadyInitiation = new HashSet <> ( );
    LinearLayout mLeftNavArea;
    RelativeLayout mLeftWindow;
    private boolean mBackKeyPressed = false;
    public Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.root);
        onInitiation ( );
    }

    public void onInitiation() {
        mContext = this;
        loadMainWindow ( );

        initFirstPageMap (mFirstPageMap);
        mQuickPageModel = new QuickPageModel (this, mFirstPageMap);
        mQuickPageModelRoot = mQuickPageModel.getRoot ( );
        loadLeftWindow ( );
        selectPage (0);
    }

    public void selectPage(int index) {
        String key = mQuickPageModelRoot.getKey (index);
        try {
            if (!mAlreadyInitiation.contains (key)) {
                Object controller = Objects.requireNonNull (mQuickPageModelRoot.mChildPages.get (key)).mController;
                Method method = controller.getClass ( ).getMethod ("onInitiation");
                method.invoke (controller);
                mAlreadyInitiation.add (key);
            }

            RelativeLayout relativeLayout = findViewById (R.id.root_container);
            relativeLayout.removeAllViews ( );
            relativeLayout.addView (Objects.requireNonNull (mQuickPageModelRoot.mChildPages.get (key)).mView);

            ViewGroup currentSelectItem = (ViewGroup) mLeftNavArea.getChildAt (mQuickPageModelRoot.currentChildIndex);
            ((TextView) currentSelectItem.getChildAt (0)).setTextColor (mContext.getColor (R.color.black));
            ((TextView) currentSelectItem.getChildAt (1)).setTextColor (mContext.getColor (R.color.black));

            ViewGroup targetItem = (ViewGroup) mLeftNavArea.getChildAt (index);
            ((TextView) targetItem.getChildAt (0)).setTextColor (mContext.getColor (R.color.colorPrimaryDark));
            ((TextView) targetItem.getChildAt (1)).setTextColor (mContext.getColor (R.color.colorPrimaryDark));

            mQuickPageModelRoot.currentChildIndex = index;

            if (mLeftWindow.getVisibility ( ) == View.VISIBLE) {
                mMainLayout.closeDrawer (mLeftWindow);
            }

            Object controller = Objects.requireNonNull (mQuickPageModelRoot.mChildPages.get (key)).mController;
            Method method = controller.getClass ( ).getMethod ("show");
            method.invoke (controller);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace ( );
        }
    }

    private void loadLeftWindow() {
        mLeftWindow = findViewById (R.id.main_layout_left);
        mLeftNavArea = findViewById (R.id.root_layout_left_nav_area);
        int index = 0;
        for (String key : mFirstPageMap.keySet ( )) {
            mQuickPageModelRoot.setKeyIndex (key, index++);
            mQuickPageModelRoot.getChild (key).setPageName (Objects.requireNonNull (mFirstPageMap.get (key))[1]);
            addSubPageNav (key, Objects.requireNonNull (mFirstPageMap.get (key)));
        }
    }

    private void addSubPageNav(String key, String[] itemContent) {
        LinearLayout item = new LinearLayout (mContext);
        item.setOrientation (LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams itemLayoutParam = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, Tool.dip2px (mContext, 50));
        item.setBackground (mContext.getDrawable (R.drawable.drawer_item));
        item.setLayoutParams (itemLayoutParam);
        item.setTag (key);

        TextView icon = new TextView (mContext);
        LinearLayout.LayoutParams iconLayoutParam = new LinearLayout.LayoutParams (0, RelativeLayout.LayoutParams.MATCH_PARENT, 1);
        icon.setLayoutParams (iconLayoutParam);
        icon.setText (itemContent[0]);
        icon.setGravity (Gravity.CENTER);
        icon.setTextColor (mContext.getColor (R.color.black));
        item.addView (icon);

        TextView text = new TextView (mContext);
        LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams (0, RelativeLayout.LayoutParams.MATCH_PARENT, 4);
        text.setText (itemContent[1]);
        text.setGravity (Gravity.CENTER_VERTICAL);
        text.setLayoutParams (textLayoutParam);
        text.setTextColor (mContext.getColor (R.color.black));
        item.addView (text);
        mLeftNavArea.addView (item);
        item.setOnClickListener (new LeftNavItemClickListener ( ));
    }

    class LeftNavItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            selectPage (mQuickPageModelRoot.getIndex ((String) v.getTag ( )));
        }
    }

    protected abstract void initFirstPageMap(Map <String, String[]> quickLayoutMap);

    private void loadMainWindow() {
        mMainLayout = this.findViewById (R.id.root_layout);
        mToolbar = this.findViewById (R.id.root_toolbar);
        setSupportActionBar (mToolbar);
        mToolbar.setNavigationIcon (R.drawable.ic_launcher_foreground);   //setNavigationIcon 需要放在 setSupportActionBar 之后才会生效。
        mToolbar.setNavigationOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                mMainLayout.openDrawer (Gravity.START);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mLeftWindow.getVisibility ( ) == View.VISIBLE) {
                //当左边的菜单栏是可见的，则关闭
                mMainLayout.closeDrawer (mLeftWindow);
            } else {
                if (!mBackKeyPressed) {
                    Toast.makeText (this, "再按一次退出", Toast.LENGTH_SHORT).show ( );
                    mBackKeyPressed = true;
                    new Timer ( ).schedule (new TimerTask ( ) {
                        @Override
                        public void run() {
                            mBackKeyPressed = false;
                        }
                    }, 2000);
                    return true;
                } else {
                    finish ( );
                }
            }
            return true;
        }
        return super.onKeyDown (keyCode, event);
    }

}
