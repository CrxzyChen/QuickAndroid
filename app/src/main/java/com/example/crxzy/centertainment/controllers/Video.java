package com.example.crxzy.centertainment.controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.crxzy.centertainment.system.ClassOnePageBase;

public class Video extends ClassOnePageBase {

    public Video(AppCompatActivity context, View view,String currentPageName) {
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
