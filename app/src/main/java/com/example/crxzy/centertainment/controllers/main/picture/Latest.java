package com.example.crxzy.centertainment.controllers.main.picture;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Message;
import android.view.View;

import com.example.crxzy.centertainment.activities.PictureActivity;
import com.example.crxzy.centertainment.R;
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
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Latest extends ThirdPageBase {
    private CardBox mCardBox;
    private PictureLatestHandler mHandler;
    private int mSkip;
    private int mLimit = 10;
    private boolean mIsLoading = false;
    private Set <View> mInactivateErrorSet;
    private float mActiveAreaMarginSize = 1000;

    public Latest(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void onInitiation() {
        mCardBox = mView.findViewById (R.id.picture_last_card_box);
        mCardBox.setOnTouchBottomListener (new CardBoxOnTouchBottomListener ( ));
        mCardBox.setOnActiveAreaChangedListener (new CardBoxOnActiveAreaChangedListener ( ));
        mCardBox.setIsOpenTopOverDragListener (true);
        mCardBox.setOnOverDragRefreshListener (new CardBoxOnOverDragRefresh ( ));
        NetApi.getLatest (mApp.mUser.uid, mLimit, 0, Latest.this, "addRequestSuccess");
        mCardBox.playRefreshingAnimation ( );
        this.mHandler = new PictureLatestHandler (this);
    }

    class CardBoxOnTouchBottomListener implements CardBox.OnTouchBottomListener {

        @Override
        public float setDistance(float distance) {
            return 400;
        }

        @Override
        public void OnTouchBottom() {
            if (!mIsLoading) {
                mIsLoading = true;
                mCardBox.setLoadingCard(View.VISIBLE);
                NetApi.getLatest (mApp.mUser.uid, mLimit, mSkip, Latest.this, "addRequestSuccess");
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
            mCardBox.clearAll ( );
            NetApi.getLatest (mApp.mUser.uid, mLimit, 0, Latest.this, "addRequestSuccess");
        }
    }

    public void addRequestSuccess(Network.Response response) {
        Message message = Message.obtain ( );
        message.obj = response;
        message.what = PictureLatestHandler.ADD_ITEMS;
        this.mHandler.sendMessage (message);
    }

    private static class PictureLatestHandler extends PageBase.PageHandler {

        static final int ADD_ITEMS = 100;

        PictureLatestHandler(PageBase activity) {
            super (activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Latest latest = (Latest) mOuterClass.get ( );
            if (msg.what == ADD_ITEMS) {
                addItems (msg, latest);
                latest.mIsLoading = false;
                latest.mCardBox.playEndRefreshAnimation ( );
            }
        }

        private void addItems(Message msg, final Latest latest) {
            try {
                final JSONArray json = ((JSONArray) ((Network.Response) msg.obj).content);
                for (int index = 0; index < json.length ( ); index++) {
                    final JSONObject item = json.getJSONObject (index);
                    int clickedTimes = 0;
                    if (item.has ("clicked")) {
                        clickedTimes = item.getInt ("clicked");
                    }
                    JSONObject info = item.getJSONObject ("info");
                    JSONObject thumb = item.getJSONObject ("thumb");
                    JSONArray image_names = thumb.getJSONArray ("image_names");
                    String name = (!"null".equals (info.getString ("original_name"))) ? info.getString ("original_name") : info.getString ("name");
                    final MangaSelfCard normalItem = new MangaSelfCard (latest.mActivity);
                    normalItem.title.setText (name);
                    JSONArray languages = info.getJSONArray ("languages");
                    String language = getLanguage (languages);
                    normalItem.clickTime.setText (Objects.requireNonNull (Integer.toString (clickedTimes)));
                    if (language.equals ("english")) {
                        normalItem.langFlag.setImageDrawable (latest.mActivity.getDrawable (R.drawable.flag_en));
                    } else if (language.equals ("chinese")) {
                        normalItem.langFlag.setImageDrawable (latest.mActivity.getDrawable (R.drawable.flag_cn));
                    } else {
                        normalItem.langFlag.setImageDrawable (latest.mActivity.getDrawable (R.drawable.flag_jp));
                    }
                    final String tagsString = item.getString ("source") + "." + language;
                    normalItem.sourceTag.setText (tagsString);
                    int status = thumb.getInt ("status");
                    if (status == 0) {
                        normalItem.statusTag.setText ("○");
                    } else if (status == 1) {
                        normalItem.statusTag.setText ("◔");
                    } else if (status == 2) {
                        normalItem.statusTag.setText ("◑");
                    } else if (status == 3) {
                        normalItem.statusTag.setText ("◕");
                    } else if (status == 4) {
                        normalItem.statusTag.setText ("●");
                    }
                    normalItem.pageCount.setText (info.getString ("page"));
                    normalItem.image.setImageURL ("http://10.0.0.2:4396/gallery/" + thumb.getString ("thumb_id") + "/" + image_names.get (0) + "?height=480&width=360");
                    latest.mCardBox.addCard (normalItem);
                    ItemOnClickListener itemOnClickListener = new ItemOnClickListener (latest, item, normalItem);
                    normalItem.setOnClickListener (itemOnClickListener);
                }
                latest.mSkip += latest.mLimit;
            } catch (JSONException e) {
                e.printStackTrace ( );
            }

        }

        private String getLanguage(JSONArray languages) throws JSONException {
            String language = null;
            for (int index_2 = 0; index_2 < languages.length ( ); index_2++) {
                language = (String) languages.get (index_2);
                if (!language.equals ("translated")) {
                    break;
                }
            }
            return language;
        }

        static class ItemOnClickListener implements View.OnClickListener {
            Latest mLatest;
            JSONObject mItem;
            MangaSelfCard mNormalItem;

            ItemOnClickListener(Latest context, JSONObject item, MangaSelfCard normalItem) {
                mLatest = context;
                mItem = item;
                mNormalItem = normalItem;
            }

            @Override
            public void onClick(View v) {
                try {
                    int uid = mLatest.mApp.mUser.uid;
                    String resource_id = mItem.getJSONObject ("_id").getString ("$oid");
                    NetApi.addHistory (uid, resource_id);
                    NetApi.upClickedCount (resource_id);
                    int clickedTimes = Integer.parseInt ((String) mNormalItem.clickTime.getText ( )) + 1;
                    mNormalItem.clickTime.setText ((String) Integer.toString (clickedTimes));

                    Intent intent = new Intent ( );
                    intent.setClass (mLatest.mActivity, PictureActivity.class);
                    intent.putExtra ("info", mItem.toString ( ));
                    mLatest.mActivity.startActivity (intent);
                } catch (JSONException e) {
                    e.printStackTrace ( );
                }
            }
        }
    }
}
