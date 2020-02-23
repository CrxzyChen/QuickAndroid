package com.example.crxzy.centertainment.controllers.main.picture;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.system.ActivityBase;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.system.ThirdPageBase;
import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.views.CardBox;
import com.example.crxzy.centertainment.views.MangaSelfCard;

public class Recommend extends ThirdPageBase {

    public Recommend(ActivityBase context, View view, QuickPageModel.Page pageModel) {
        super (context, view, pageModel);
    }

    @Override
    public void onShow() {
        Network network = new Network ( );
        Network.Request request = network.InstanceRequest ("http://10.0.0.2/CEntertainment/Manga/Latest.json?limit=10");
        request.setSuccess (this, "success");
        network.send (request);
        CardBox itemBox = (CardBox) mContext.findViewById (R.id.picture_recommend_card_box);
        MangaSelfCard normalItem = new MangaSelfCard (mContext);
        itemBox.addItem (normalItem);
        MangaSelfCard normalItem2 = new MangaSelfCard (mContext);
        itemBox.addItem (normalItem2);
    }


    public void success(Network.Response response) {
    }

    public void error(Network.Response response) {
    }
}
