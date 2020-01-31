package com.example.crxzy.centertainment;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.reflect.InvocationTargetException;

public class PictureRecommend extends ClassSecondPageBase {
    PictureRecommend(AppCompatActivity context, View view, String currentPageName) {
        super (context, view, currentPageName);
    }

    @Override
    public void onShow() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ItemsBox itemBox = (ItemsBox)mContext.findViewById (R.id.picture_recommend_itemsbox);
        ItemsBox.NormalItem normalItem = new ItemsBox.NormalItem (mContext);
        itemBox.addItem (normalItem);
        ItemsBox.NormalItem normalItem2 = new ItemsBox.NormalItem (mContext);
        itemBox.addItem (normalItem2);
    }
}
