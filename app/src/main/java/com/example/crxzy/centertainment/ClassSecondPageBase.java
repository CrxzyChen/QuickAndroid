package com.example.crxzy.centertainment;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ClassSecondPageBase {
    private final AppCompatActivity mContext; //主Activity实列
    public final View mView;//当前控制器对应一级页面视图
    private final String mCurrentPageName;

    ClassSecondPageBase(AppCompatActivity context, View view, String currentPageName) {
        mContext = context;
        mView = view;
        mCurrentPageName = currentPageName;
        onInitiation();
    }

    /***
     * 初始化方法，可有子类重构用于初始化
     */
    protected void onInitiation() { }
    /**
     * 二级页面添加到Activity后执行该函数
     */

    protected void onShow(){
        Button btn = (Button) mContext.findViewById (R.id.button);
        btn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Toast.makeText (mContext, "You clicked Add", Toast.LENGTH_SHORT).show ( );
            }
        });
    }
}
