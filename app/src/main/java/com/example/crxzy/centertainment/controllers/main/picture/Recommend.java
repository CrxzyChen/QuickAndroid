package com.example.crxzy.centertainment.controllers.main.picture;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.system.ThirdPageBase;
import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.views.CardBox;
import com.example.crxzy.centertainment.views.MangaSelfCard;

public class Recommend extends ThirdPageBase {

    public Recommend(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity,  pageModel);
    }

    @Override
    public void onInitiation() {
        Network network = new Network ( );
        Network.Request request = network.InstanceRequest ("http://10.0.0.2/CEntertainment/Manga/Latest.json?limit=10");
        request.setSuccess (this, "success");
        network.send (request);
        CardBox itemBox = (CardBox) mActivity.findViewById (R.id.picture_recommend_card_box);
        MangaSelfCard normalItem = new MangaSelfCard (mActivity);
        itemBox.addItem (normalItem);
        MangaSelfCard normalItem2 = new MangaSelfCard (mActivity);
        itemBox.addItem (normalItem2);
    }

    public void success(Network.Response response) {
    }

    public void error(Network.Response response) {
    }
}
