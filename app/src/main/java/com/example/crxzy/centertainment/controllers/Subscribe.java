package com.example.crxzy.centertainment.controllers;

import android.view.View;
import android.widget.Button;

import com.example.crxzy.centertainment.R;
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
        mViewContainer = mView.findViewById (R.id.subscribe_container);
        Button mBackButton = mView.findViewById (R.id.subscribe_back);
        mBackButton.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                mViewContainer.smoothScrollToPosition (0);
            }
        });
        mBackButton.setOnLongClickListener (new View.OnLongClickListener ( ) {
            @Override
            public boolean onLongClick(View v) {
                mPageModel.mParent.getController ( ).selectPage (mPageModel.mParent.getIndex ("main"));
                return false;
            }
        });
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
