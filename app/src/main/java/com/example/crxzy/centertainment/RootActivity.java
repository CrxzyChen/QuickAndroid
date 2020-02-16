package com.example.crxzy.centertainment;

import android.os.Bundle;

import com.example.crxzy.centertainment.system.ActivityBase;

import java.util.LinkedHashMap;
import java.util.Map;

public class RootActivity extends ActivityBase {
    public Bundle mPageNameToText = new Bundle ( );

    @Override
    public void onInitiation() {
//        mPageNameToText = new Bundle ( );
//        mPageNameToText.putString ("video", "视频");
//        mPageNameToText.putString ("picture", "图片");
//        mPageNameToText.putString ("article", "文章");
//        mPageNameToText.putString ("personal", "个人");


        super.onInitiation ( );
    }

    @Override
    protected void initFirstPageMap(Map <String, String[]> firstPageMap) {
        firstPageMap.put ("main", new String[]{"⌂", "首页"});
        firstPageMap.put ("history", new String[]{"↺", "历史记录"});
    }

//    @Override
//    public String[] getMainPageOrder() {
//        return new String[]{
//                "picture",
//                "video",
//                "article",
//                "personal"
//        };
//    }
//
//    @Override
//    public String getTabButtonText(String pageName) {
//        return (String) mPageNameToText.get (pageName);
//    }
}
