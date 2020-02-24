package com.example.crxzy.centertainment.controllers;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.system.FirstPageBase;
import com.example.crxzy.centertainment.system.QuickPageModel;

import java.util.Map;

public class Main extends FirstPageBase {

    public Main(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void onInitiation() {
        super.onInitiation ( );
    }

    @Override
    public void setHeader(View header) {
        ((LinearLayout) header).getChildAt (0).setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                mActivity.mRootPage.mMainLayout.openDrawer (Gravity.START);
            }
        });
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