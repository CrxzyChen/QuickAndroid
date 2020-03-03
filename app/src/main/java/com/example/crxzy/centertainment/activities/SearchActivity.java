package com.example.crxzy.centertainment.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.models.MangaResource;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.system.QuickActivity;
import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.tools.Tool;
import com.example.crxzy.centertainment.views.CardBox;
import com.example.crxzy.centertainment.views.MangaSelfCard;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchActivity extends QuickActivity {
    Handler mHandler;
    CardBox mContainer;
    private int mLoadedSize;
    private boolean mIsLoading = false;
    private int mSingleLoadSize = 10;
    private String mSearchContent;
    float mActiveAreaMarginSize = 2000;
    private Set <View> mInactivateErrorSet = new HashSet <> ( );

    public void onInitiation() {
        setContentView (R.layout.independ_search);
        mHandler = new MyHandler (this);
        Intent intent = getIntent ( );
        mSearchContent = intent.getStringExtra ("search_content");
        mActiveAreaMarginSize = Tool.dip2px (this, mActiveAreaMarginSize);
        initMainWindow ( );
    }

    private void initMainWindow() {
        mContainer = findViewById (R.id.sub_search_container);
        mContainer.setOnTouchBottomListener (new CardBox.OnTouchBottomListener ( ) {
            @Override
            public float setDistance(float distance) {
                return Tool.dip2px (SearchActivity.this, 400);
            }

            @Override
            public void onTouchBottom() {
                if (!mIsLoading) {
                    mContainer.setLoadingCard(View.VISIBLE);
                    NetApi.search (mSearchContent, SearchActivity.this, "searchSuccess", mSingleLoadSize, mLoadedSize);
                    mIsLoading = true;
                }
            }
        });
        NetApi.search (mSearchContent, this, "searchSuccess", mSingleLoadSize, mLoadedSize);
        mContainer.setOnActiveAreaChangedListener (new CardBoxOnActiveAreaChangedListener ( ));
    }

    class CardBoxOnActiveAreaChangedListener implements CardBox.OnActiveAreaChangedListener {

        @Override
        public int setActiveAreaMarginSize(int activeAreaMarginSize) {
            return Tool.dip2px (SearchActivity.this, mActiveAreaMarginSize);
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

    public void searchSuccess(Network.Response response) {
        Message message = mHandler.obtainMessage ( );
        message.obj = response.content;
        message.what = MyHandler.SHOW_SEARCH_RESULT;
        mHandler.sendMessage (message);
    }

    static class MyHandler extends ActivityHandler {

        public static final int SHOW_SEARCH_RESULT = 100;

        public MyHandler(QuickActivity activity) {
            super (activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SearchActivity activity = (SearchActivity) mOuterClass.get ( );

            switch (msg.what) {
                case SHOW_SEARCH_RESULT:
                    JSONArray json = (JSONArray) msg.obj;
                    List <MangaResource> opus = new ArrayList <> ( );
                    for (int index = 0; index < json.length ( ); index++) {
                        try {
                            opus.add (new MangaResource (json.getJSONObject (index)));
                        } catch (JSONException e) {
                            e.printStackTrace ( );
                        }
                    }
                    activity.addItem (opus);
                    break;
            }
        }
    }

    private void addItem(List <MangaResource> opus) {
        for (MangaResource opu : opus) {
            mContainer.setLoadingCard (View.VISIBLE);
            mContainer.addCard (new MangaSelfCard (this, opu));
        }
        mLoadedSize += opus.size ( );
        mIsLoading = false;
    }
}
