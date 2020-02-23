package com.example.crxzy.centertainment.controllers;

import android.view.View;

import com.example.crxzy.centertainment.system.ActivityBase;
import com.example.crxzy.centertainment.system.FirstPageBase;
import com.example.crxzy.centertainment.system.QuickPageModel;

import java.util.Map;

public class Main extends FirstPageBase {

    public Main(ActivityBase context, View view, QuickPageModel.Page pageModel) {
        super (context, view, pageModel);
    }

    @Override
    public void onInitiation() {
        super.onInitiation ();
    }

    @Override
    public void onShow() {
        mContext.setAutoTitle(false);
    }

    @Override
    public void initSecondPageMap(Map <String, String[]> secondPageMap) {
        secondPageMap.put ("picture", new String[]{"❀", "图片"});
        secondPageMap.put ("video", new String[]{"▷", "视频"});
        secondPageMap.put ("article", new String[]{"✎", "文章"});
        secondPageMap.put ("personal", new String[]{"☻", "个人"});
    }
}