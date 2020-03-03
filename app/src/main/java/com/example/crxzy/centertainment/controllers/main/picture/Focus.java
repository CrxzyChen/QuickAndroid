package com.example.crxzy.centertainment.controllers.main.picture;

import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.example.crxzy.centertainment.R;
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

public class Focus extends ThirdPageBase {
    public static final int mTouchBottomDistance = 400;
    private final int mSingleLoadSize = 10;
    private int mLoadedSize;
    private MyHandler mHandler;
    private boolean mIsLoading = false;
    private static float mActiveAreaMarginSize = 2000;
    private Set <View> mInactivateErrorSet = new HashSet <> ( );

    public Focus(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void onInitiation() {
        mHandler = new MyHandler (this);
        NetApi.getUserFocus (mApp.mUser.uid, mSingleLoadSize, mLoadedSize, new loadItemCallback ( ));
        ((CardBox) mView).setOnTouchBottomListener (new CardBoxOnTouchBottomListener ( ));
        ((CardBox) mView).setOnActiveAreaChangedListener (new CardBoxOnActiveAreaChangedListener ( ));
        ((CardBox) mView).setOnOverDragRefreshListener (new CardBoxOnOverDragRefresh ( ));
        ((CardBox) mView).playRefreshingAnimation ( );
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
            mLoadedSize = 0;
            NetApi.getUserFocus (mApp.mUser.uid, mSingleLoadSize, mLoadedSize, new loadItemCallback ( ));
        }
    }

    class CardBoxOnTouchBottomListener implements CardBox.OnTouchBottomListener {
        @Override
        public float setDistance(float distance) {
            return Tool.dip2px (mActivity, mTouchBottomDistance);
        }

        @Override
        public void onTouchBottom() {
            if (!mIsLoading) {
                NetApi.getUserFocus (mApp.mUser.uid, mSingleLoadSize, mLoadedSize, new loadItemCallback ( ));
                ((CardBox) mView).setLoadingCard (View.VISIBLE);
                mIsLoading = true;
            }
        }
    }

    class loadItemCallback extends Network.Callback {

        @Override
        public void success(Network.Response response) {
            Message message = mHandler.obtainMessage ( );
            message.what = MyHandler.LOAD_ITEM;
            message.obj = response.content;
            mHandler.sendMessage (message);
        }

        @Override
        public void error(Network.Response response) {

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
        mLoadedSize += obj.length ( );
    }

    static class MyHandler extends PageHandler {
        static final int LOAD_ITEM = 100;

        MyHandler(PageBase page) {
            super (page);
        }

        @Override
        public void handleMessage(Message msg) {
            Focus focus = (Focus) mOuterClass.get ( );
            switch (msg.what) {
                case LOAD_ITEM:
                    focus.addItem ((JSONArray) msg.obj);
                    break;
            }
        }
    }
}
