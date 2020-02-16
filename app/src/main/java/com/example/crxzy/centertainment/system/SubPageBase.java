package com.example.crxzy.centertainment.system;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SubPageBase {
    AppCompatActivity mContext;
    View mView;
    String mCurrentPageName;
    public SubPageBase(AppCompatActivity context, View view, String currentPageName) {
        mContext = context;
        mView = view;
        mCurrentPageName = currentPageName;
    }
}
