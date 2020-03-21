package com.example.crxzy.centertainment.controllers.main.picture;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.models.PictureResource;
import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.system.ThirdPageBase;
import com.example.crxzy.centertainment.views.Card;
import com.example.crxzy.centertainment.views.CardBox;

import org.json.JSONObject;

import java.util.Map;

public class Latest extends ThirdPageBase {
    private CardBox mCardBox;

    public Latest(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void onInitiation() {
        mCardBox = mView.findViewById (R.id.picture_last_card_box);
        mCardBox.setCardBoxAdapt (new CardBox.CardBoxAdapt (mCardBox) {
            @Override
            protected void initViewTypeToView(Map <Integer, Class <?>> mViewTypeToView) {
                mViewTypeToView.put (0, Card.class);
            }

            @Override
            protected void requestData(int loadedSize, int singleLoadSize, LoadDataCallback callback) {
                NetApi.getLatest (mApp.mUser.uid, singleLoadSize, loadedSize, callback);
            }

            @Override
            protected void loadItem(CardBox cardBox, JSONObject obj) {
                cardBox.addResource (new PictureResource (obj));
            }
        });
    }
}
