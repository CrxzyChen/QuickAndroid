package com.example.crxzy.centertainment.controllers.picture;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.crxzy.centertainment.views.*;
import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.system.ClassSecondPageBase;
import com.example.crxzy.centertainment.tools.Network;

import java.lang.reflect.InvocationTargetException;

public class Recommend extends ClassSecondPageBase {
    public Recommend(AppCompatActivity context, View view, String currentPageName) {
        super (context, view, currentPageName);
    }

    @Override
    public void onShow() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Network network = new Network ( );
        Network.Request request = network.InstanceRequest ("http://10.0.0.2/CEntertainment/Manga/Latest.json?limit=10");
        request.setSuccess (this, "success");
        network.send (request);

        ItemsBoxView itemBox = (ItemsBoxView) mContext.findViewById (R.id.picture_recommend_itemsbox);
        ItemsBoxView.NormalItem normalItem = new ItemsBoxView.NormalItem (mContext);
        itemBox.addItem (normalItem);
        ItemsBoxView.NormalItem normalItem2 = new ItemsBoxView.NormalItem (mContext);
        itemBox.addItem (normalItem2);
    }

    public void success(Network.Response response) {
    }

    public void error(Network.Response response) {
    }
}
