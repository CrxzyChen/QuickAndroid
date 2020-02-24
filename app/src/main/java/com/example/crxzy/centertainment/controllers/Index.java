package com.example.crxzy.centertainment.controllers;

import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;

import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.system.ZeroPageBase;

import java.util.Map;

public class Index extends ZeroPageBase {
    public Index(MainActivity context, QuickPageModel.Page pageModel) {
        super (context, pageModel);
    }

    @Override
    public void setPageMap(Map <String, String[]> pageMap) {
        pageMap.put ("main", new String[]{"首页", "⌂"});
        pageMap.put ("history", new String[]{"历史记录", "↺"});
        pageMap.put ("subscribe", new String[]{"订阅", "✉"});
    }

    @Override
    public void setHeader(View header) {
        mActivity.setSupportActionBar ((android.support.v7.widget.Toolbar) header);
        ((Toolbar) header).setNavigationOnClickListener (new Toolbar.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                mMainLayout.openDrawer (Gravity.START);
            }
        });
    }
}