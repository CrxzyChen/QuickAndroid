package com.example.crxzy.centertainment.controllers;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.system.FirstPageBase;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.tools.Tool;
import com.example.crxzy.centertainment.views.LabelBox;

import java.util.Map;
import java.util.zip.Inflater;

public class Main extends FirstPageBase {

    public Main(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void onInitiation() {
        super.onInitiation ( );
    }

    @Override
    public void onHeaderInitialize(View header) {
        ((LinearLayout) header).getChildAt (0).setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                mActivity.mRootPage.mMainLayout.openDrawer (Gravity.START);
            }
        });
        header.findViewById (R.id.header_main_def).setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Tool.setWindowAlpha (mActivity, 0.6f);
                PopupWindow popupWindow = new PopupWindow (View.inflate (mActivity, R.layout.popup_picture_setting, null), (int)(Tool.getScreenWidth (mActivity)*0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setTouchable (true);
                popupWindow.setOutsideTouchable (true);
                popupWindow.showAtLocation (mView, Gravity.CENTER, 0, 0);
                popupWindow.setOnDismissListener (new PopupWindow.OnDismissListener ( ) {
                    @Override
                    public void onDismiss() {
                        Tool.setWindowAlpha (mActivity, 1f);
                    }
                });
            }
        });
    }

    @Override
    public void onHeaderShow(View header) {

    }

    @Override
    public void onShow() {
    }

    @Override
    public void setPageMap(Map <String, String[]> secondPageMap) {
        secondPageMap.put ("picture", new String[]{"图片", "❀"});
        secondPageMap.put ("video", new String[]{"视频", "▷"});
        secondPageMap.put ("article", new String[]{"文章", "✎"});
        secondPageMap.put ("personal", new String[]{"个人", "☻"});
    }
}