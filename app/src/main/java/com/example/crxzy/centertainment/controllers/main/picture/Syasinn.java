package com.example.crxzy.centertainment.controllers.main.picture;

import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.models.PictureResource;
import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.system.ThirdPageBase;
import com.example.crxzy.centertainment.views.CardBox;
import com.example.crxzy.centertainment.views.SyasinnSelfCard;

import org.json.JSONObject;

import java.util.Map;

public class Syasinn extends ThirdPageBase {

    public Syasinn(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void onInitiation() {
        super.onInitiation ( );
        CardBox mContainer = (CardBox) mView;
        mContainer.setCardBoxAdapt (new CardBox.CardBoxAdapt (mContainer) {
            @Override
            protected void initViewTypeToView(Map <Integer, Class <?>> mViewTypeToView) {
                mViewTypeToView.put (0, SyasinnSelfCard.class);
            }

            @Override
            protected void requestData(int loadedSize, int singleLoadSize, LoadDataCallback callback) {
                NetApi.getLatest (mApp.mUser.uid, singleLoadSize, loadedSize, callback, "syasinn");
            }

            @Override
            protected void loadItem(CardBox cardBox, JSONObject obj) {
                cardBox.addResource (new PictureResource (obj));
            }
        });
    }


}
