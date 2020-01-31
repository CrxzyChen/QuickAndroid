package com.example.crxzy.centertainment.system;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.crxzy.centertainment.R;

import java.lang.reflect.InvocationTargetException;

public class ClassSecondPageBase {
    protected final AppCompatActivity mContext; //主Activity实列
    protected final View mView;//当前控制器对应一级页面视图
    private final String mCurrentPageName;

    public ClassSecondPageBase(AppCompatActivity context, View view, String currentPageName) {
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

    protected void onShow() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        mView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Toast.makeText (mContext,"It's OK",Toast.LENGTH_SHORT);
            }
        });
        Button btn = (Button) mContext.findViewById (R.id.button);
        btn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Toast.makeText (mContext, "You clicked Add", Toast.LENGTH_SHORT).show ( );
            }
        });
    }
}
