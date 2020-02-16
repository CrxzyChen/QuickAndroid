package com.example.crxzy.centertainment.system;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Tool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

abstract public class ActivityBase extends AppCompatActivity {
    public Context mContext;
    private DrawerLayout mMainLayout;
    private Toolbar mToolbar;
    public QuickPageModel mQuickPageModel;
    public QuickPageModel.Page mQuickPageModelRoot;
    public Map <String, String[]> mFirstPageMap = new LinkedHashMap <> ( );
    LinearLayout mLeftNavArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.root);
        onInitiation ( );//调用子类初始化方法
    }

    public void onInitiation() {
        mContext = this;
        loadMainWindow ( );

        initFirstPageMap (mFirstPageMap);
        mQuickPageModel = new QuickPageModel (this, mFirstPageMap);
        mQuickPageModelRoot = mQuickPageModel.getRoot ( );

        loadLeftNavItem ( );

        String firstPageName = mFirstPageMap.keySet ( ).iterator ( ).next ( );
        selectPage (firstPageName);
    }

    private void selectPage(String pageName) {
        try {
            Object controller = Objects.requireNonNull (mQuickPageModelRoot.mChildPages.get (pageName)).mController;
            Method method = controller.getClass ( ).getMethod ("onInitiation");
            method.invoke (controller);
            RelativeLayout relativeLayout = findViewById (R.id.root_container);
            relativeLayout.addView (Objects.requireNonNull (mQuickPageModelRoot.mChildPages.get (pageName)).mView);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace ( );
        }
    }

    private void loadLeftNavItem() {
        mLeftNavArea = findViewById (R.id.root_layout_left_nav_area);

        for (String key : mFirstPageMap.keySet ( )) {
            addSubPageNav (key, Objects.requireNonNull (mFirstPageMap.get (key)));
        }
    }

    private void addSubPageNav(String key, String[] itemContent) {
        LinearLayout item = new LinearLayout (mContext);
        item.setOrientation (LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams itemLayoutParam = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, Tool.dip2px (mContext, 50));
        item.setBackground (mContext.getDrawable (R.drawable.drawer_item));
        item.setClickable (true);
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
            Toast.makeText (mContext, (String) v.getTag ( ), Toast.LENGTH_SHORT).show ( );
        }
    }

    protected abstract void initFirstPageMap(Map <String, String[]> quickLayoutMap);


    private void loadMainWindow() {
        mMainLayout = this.findViewById (R.id.root_layout);
        mToolbar = this.findViewById (R.id.root_toolbar);
        mToolbar.setTitle ("Test Title");
        mToolbar.setSubtitle ("This is substitle");
        //toolbar.setLogo(R.drawable.ic_launcher); 可以在 Navigation后 设置一个 logo
        mToolbar.setSubtitleTextColor (this.getColor (R.color.white)); //设置二级标题的颜色
        mToolbar.setTitleTextColor (this.getColor (R.color.white)); //设置标题的颜色
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
            RelativeLayout relativeLayout = findViewById (R.id.main_layout_left);
            if (relativeLayout.getVisibility ( ) == View.VISIBLE) {
                //当左边的菜单栏是可见的，则关闭
                mMainLayout.closeDrawer (relativeLayout);
            } else {
                finish ( );
            }
            return true;
        }
        return super.onKeyDown (keyCode, event);
    }

}