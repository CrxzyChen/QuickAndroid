package com.example.crxzy.centertainment.controllers;

import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.models.PictureResource;
import com.example.crxzy.centertainment.system.FirstPageBase;
import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.views.Card;
import com.example.crxzy.centertainment.views.CardBox;

import org.json.JSONObject;

import java.util.Map;

public class Subscribe extends FirstPageBase {
    private CardBox mViewContainer;

    public Subscribe(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void onInitiation() {
        mViewContainer = (CardBox) mView;
        mViewContainer.setCardBoxAdapt (new CardBox.CardBoxAdapt (mViewContainer) {
            @Override
            protected void initViewTypeToView(Map <Integer, Class <?>> mViewTypeToView) {
                mViewTypeToView.put (0, Card.class);
            }

            @Override
            protected void requestData(int loadedSize, int singleLoadSize, LoadDataCallback callback) {
                NetApi.getSubscribe (mApp.mUser.uid, singleLoadSize, loadedSize, callback);
            }

            @Override
            protected void loadItem(CardBox cardBox, JSONObject obj) {
                cardBox.addResource (new PictureResource (obj));
            }
        });
    }

}
