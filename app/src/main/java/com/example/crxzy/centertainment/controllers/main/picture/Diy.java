package com.example.crxzy.centertainment.controllers.main.picture;

import android.os.Message;
import android.view.View;

import com.example.crxzy.centertainment.models.MangaResource;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.system.PageBase;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.system.ThirdPageBase;
import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.tools.Tool;
import com.example.crxzy.centertainment.views.CardBox;
import com.example.crxzy.centertainment.views.MangaSelfCard;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

public class Diy extends ThirdPageBase {
    private final int mSingleLoadSize = 10;
    private MyHandler mHandler;
    private boolean mIsLoading = false;
    private float mActiveAreaMarginSize = 2000;
    private Set <View> mInactivateErrorSet = new HashSet <> ( );

    public Diy(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void onInitiation() {
        mHandler = new MyHandler (this);
        mActiveAreaMarginSize = Tool.dip2px (mActivity, mActiveAreaMarginSize);
        NetApi.getUserDefine (mApp.mUser.uid, mSingleLoadSize, this, "getUserDefineSuccess");
        ((CardBox) mView).setOnTouchBottomListener (new CardBox.OnTouchBottomListener ( ) {
            @Override
            public float setDistance(float distance) {
                return Tool.dip2px (mActivity, 400);
            }

            @Override
            public void onTouchBottom() {
                if (!mIsLoading) {
                    ((CardBox) mView).setLoadingCard (View.VISIBLE);
                    NetApi.getUserDefine (mApp.mUser.uid, mSingleLoadSize, Diy.this, "getUserDefineSuccess");
                    mIsLoading = true;
                }
            }
        });
        ((CardBox) mView).setOnActiveAreaChangedListener (new CardBoxOnActiveAreaChangedListener ( ));
        ((CardBox) mView).setOnOverDragRefreshListener (new CardBoxOnOverDragRefresh ( ));
        ((CardBox) mView).playRefreshingAnimation ( );
    }

    public void getUserDefineSuccess(Network.Response response) {
        Message message = mHandler.obtainMessage ( );
        message.what = MyHandler.LOAD_ITEM;
        message.obj = response.content;
        mHandler.sendMessage (message);
    }

    static class MyHandler extends PageHandler {

        static final int LOAD_ITEM = 100;

        MyHandler(PageBase page) {
            super (page);
        }

        @Override
        public void handleMessage(Message msg) {
            Diy diy = (Diy) mOuterClass.get ( );
            if (msg.what == LOAD_ITEM) {
                diy.addItem ((JSONArray) msg.obj);
            }
        }
    }

    private void addItem(JSONArray obj) {
        for (int index = 0; index < obj.length ( ); index++) {
            try {
                ((CardBox) mView).addCard (new MangaSelfCard (mActivity, new MangaResource (obj.getJSONObject (index))));
                mIsLoading = false;
                ((CardBox) mView).playEndRefreshAnimation ( );
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }
    }

    class CardBoxOnActiveAreaChangedListener implements CardBox.OnActiveAreaChangedListener {

        @Override
        public int setActiveAreaMarginSize(int activeAreaMarginSize) {
            return Tool.dip2px (mActivity, mActiveAreaMarginSize);
        }

        @Override
        public void setActiveAreaOperation(Set <View> viewSet) {
            for (View view : viewSet) {
                ((MangaSelfCard) view).image.load ( );
            }
        }

        @Override
        public void setInActiveAreaOperation(Set <View> viewSet) {
            if (mInactivateErrorSet != null) {
                viewSet.addAll (mInactivateErrorSet);
            }
            Set <View> newInactivateErrorSet = new HashSet <> ( );
            for (View view : viewSet) {
                if (!((MangaSelfCard) view).image.release ( )) {
                    newInactivateErrorSet.add (view);
                }
            }
            mInactivateErrorSet = newInactivateErrorSet;
        }
    }

    class CardBoxOnOverDragRefresh implements CardBox.OnOverDragRefreshListener {

        @Override
        public void OnRefresh() {
            ((CardBox) mView).clearAll ( );
            NetApi.getUserDefine (mApp.mUser.uid, mSingleLoadSize, Diy.this, "getUserDefineSuccess");
        }
    }
}
