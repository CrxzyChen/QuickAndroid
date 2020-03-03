package com.example.crxzy.centertainment.controllers;

import android.content.Intent;
import android.os.Message;
import android.view.View;

import com.example.crxzy.centertainment.activities.PictureActivity;
import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.system.FirstPageBase;
import com.example.crxzy.centertainment.system.PageBase;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.views.CardBox;
import com.example.crxzy.centertainment.views.MangaSelfCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Subscribe extends FirstPageBase {
    private MyHandler mHandler = new MyHandler (this);
    private CardBox mViewContainer;
    private int mSingleLoadSize = 10;
    private int mLoadedSize = 0;
    private boolean mIsLoading = false;

    public Subscribe(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void onInitiation() {
        super.onInitiation ( );
        mViewContainer = mView.findViewById (R.id.subscribe_container);
        NetApi.getSubscribe (mApp.mUser.uid, this, "getSubscribeSuccess", mSingleLoadSize, mLoadedSize);
        mViewContainer.setIsOpenTopOverDragListener (true);
        mViewContainer.setOnOverDragRefreshListener (new CardBoxOnOverDragRefresh ( ));
        mViewContainer.setOnTouchBottomListener (new CardBox.OnTouchBottomListener ( ) {
            @Override
            public float setDistance(float distance) {
                return 200;
            }

            @Override
            public void onTouchBottom() {
                if (!mIsLoading) {
                    mIsLoading = true;
                    mViewContainer.setLoadingCard(View.VISIBLE);
                    NetApi.getSubscribe (mApp.mUser.uid, Subscribe.this, "getSubscribeSuccess", mSingleLoadSize, mLoadedSize);
                }
            }
        });
        mViewContainer.playRefreshingAnimation ( );
    }

    class CardBoxOnOverDragRefresh implements CardBox.OnOverDragRefreshListener {
        @Override
        public void OnRefresh() {
            mViewContainer.clearAll ( );
            mLoadedSize = 0;
            NetApi.getSubscribe (mApp.mUser.uid, Subscribe.this, "getSubscribeSuccess", mSingleLoadSize, mLoadedSize);
        }
    }

    public void getSubscribeSuccess(Network.Response response) {
        Message message = Message.obtain ( );
        message.what = MyHandler.LOAD_SUBSCRIBE;
        message.obj = response;
        mHandler.sendMessage (message);
    }

    private void addItem(JSONArray subscribeItems) {
        try {
            for (int index = 0; index < subscribeItems.length ( ); index++) {
                final JSONObject item = subscribeItems.getJSONObject (index);
                int clickedTimes = 0;
                if (item.has ("clicked")) {
                    clickedTimes = item.getInt ("clicked");
                }
                JSONObject info = item.getJSONObject ("info");
                JSONObject thumb = item.getJSONObject ("thumb");
                JSONArray image_names = thumb.getJSONArray ("image_names");
                String name = (!"null".equals (info.getString ("original_name"))) ? info.getString ("original_name") : info.getString ("name");
                final MangaSelfCard mangaSelfCard = new MangaSelfCard (mActivity);
                mangaSelfCard.title.setText (name);
                JSONArray languages = info.getJSONArray ("languages");
                String language = null;
                for (int index_2 = 0; index_2 < languages.length ( ); index_2++) {
                    language = (String) languages.get (index_2);
                    if (!language.equals ("translated")) {
                        break;
                    }
                }
                if (Objects.equals (language, "english")) {
                    mangaSelfCard.langFlag.setImageDrawable (mActivity.getDrawable (R.drawable.flag_en));
                } else if (Objects.equals (language, "chinese")) {
                    mangaSelfCard.langFlag.setImageDrawable (mActivity.getDrawable (R.drawable.flag_cn));
                } else {
                    mangaSelfCard.langFlag.setImageDrawable (mActivity.getDrawable (R.drawable.flag_jp));
                }
                int status = thumb.getInt ("status");
                if (status == 0) {
                    mangaSelfCard.statusTag.setText ("○");
                } else if (status == 1) {
                    mangaSelfCard.statusTag.setText ("◔");
                } else if (status == 2) {
                    mangaSelfCard.statusTag.setText ("◑");
                } else if (status == 3) {
                    mangaSelfCard.statusTag.setText ("◕");
                } else if (status == 4) {
                    mangaSelfCard.statusTag.setText ("●");
                }
                mangaSelfCard.clickTime.setText ((String) Integer.toString (clickedTimes));
                final String tagsString = item.getString ("source") + "." + language;
                mangaSelfCard.sourceTag.setText (tagsString);
                mangaSelfCard.pageCount.setText (info.getString ("page"));
                mangaSelfCard.image.setImageURL ("http://10.0.0.2:4396/gallery/" + thumb.getString ("thumb_id") + "/" + image_names.get (0) + "?height=480&width=360");
                mViewContainer.addCard (mangaSelfCard);
                ItemOnClickListener itemOnClickListener = new ItemOnClickListener (item, mangaSelfCard);
                mangaSelfCard.setOnClickListener (itemOnClickListener);
            }
            mLoadedSize += subscribeItems.length ( );
            mIsLoading = false;
        } catch (JSONException e) {
            e.printStackTrace ( );
        }
    }

    class ItemOnClickListener implements View.OnClickListener {
        JSONObject mItem;
        MangaSelfCard mNormalItem;

        ItemOnClickListener(JSONObject item, MangaSelfCard normalItem) {

            mItem = item;
            mNormalItem = normalItem;
        }

        @Override
        public void onClick(View v) {
            try {
                int uid = mApp.mUser.uid;
                String resource_id = mItem.getJSONObject ("_id").getString ("$oid");
                NetApi.addHistory (uid, resource_id);
                NetApi.upClickedCount (resource_id);
                int clickedTimes = Integer.parseInt ((String) mNormalItem.clickTime.getText ( )) + 1;
                mNormalItem.clickTime.setText ((String) Integer.toString (clickedTimes));
                Intent intent = new Intent ( );
                intent.setClass (mActivity, PictureActivity.class);
                intent.putExtra ("info", mItem.toString ( ));
                mActivity.startActivity (intent);
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }
    }

    static class MyHandler extends PageHandler {

        static final int LOAD_SUBSCRIBE = 100;

        MyHandler(PageBase activity) {
            super (activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Subscribe subscribe = (Subscribe) mOuterClass.get ( );
            if (msg.what == LOAD_SUBSCRIBE) {
                JSONArray subscribeItems = ((JSONArray) ((Network.Response) msg.obj).content);
                subscribe.addItem (subscribeItems);
                subscribe.mViewContainer.playEndRefreshAnimation ( );
            }
        }
    }
}
