package com.example.crxzy.centertainment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Picture extends ClassOnePageBase {
    public Picture(AppCompatActivity context, View view,String currentPageName) {
        super (context, view,currentPageName);
    }

    private Bundle mPagenameToText;

    @Override
    protected void onInitiation() {
        super.onInitiation ( );
        mPagenameToText = new Bundle ( );
        mPagenameToText.putString ("recommend", "推荐");
        mPagenameToText.putString ("lasted", "最新");
    }

    @Override
    public String[] getPageOrder() {
        return new String[]{
                "recommend",
                "lasted"
        };
    }

    @Override
    protected String getNavText(String s) {
        return (String) mPagenameToText.get (s);
    }

}
