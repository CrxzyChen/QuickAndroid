package com.example.crxzy.centertainment.controllers.picture;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.crxzy.centertainment.system.ItemsBox;
import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.system.ClassSecondPageBase;

import java.lang.reflect.InvocationTargetException;

public class Recommend extends ClassSecondPageBase {
    public Recommend(AppCompatActivity context, View view, String currentPageName) {
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
