package com.example.crxzy.centertainment;

import android.os.Bundle;

public class MainActivity extends MainActivityBase {
    public Bundle mPageNameToText = new Bundle ( );

    @Override
    public void onInitiation() {
        mPageNameToText = new Bundle ( );
        mPageNameToText.putString ("video", "视频");
        mPageNameToText.putString ("picture", "图片");
        mPageNameToText.putString ("article", "文章");
        mPageNameToText.putString ("personal", "个人");
        super.onInitiation ( );
    }

    @Override
    public String[] getPageOrder() {
        return new String[]{
                "picture",
                "video",
                "article",
                "personal"
        };
    }

    @Override
    public String getTabButtonText(String pagename) {
        return (String)mPageNameToText.get (pagename);
    }
}
