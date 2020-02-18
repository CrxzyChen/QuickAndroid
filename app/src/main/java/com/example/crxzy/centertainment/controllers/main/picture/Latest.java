package com.example.crxzy.centertainment.controllers.main.picture;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.example.crxzy.centertainment.PictureActivity;
import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.system.PageBase;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.system.ThirdPageBase;
import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.views.CardBox;
import com.example.crxzy.centertainment.views.ImageView;
import com.example.crxzy.centertainment.views.MangaSelfCard;
import com.example.crxzy.centertainment.views.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Latest extends ThirdPageBase {
    private CardBox mCardBox;
    private PictureLatestHandler mHandler;
    private CardBox.LinearBlockItem mLoadingItem;
    private int mSkip;
    private int mLimit = 10;
    private boolean mIsLoading = false;
    private Set <View> mInactivateErrorSet;

    public Latest(AppCompatActivity context, View view, QuickPageModel.Page pageModel) {
        super (context, view, pageModel);
    }


    @Override
    public void onShow() {
        mCardBox = mContext.findViewById (R.id.picture_last_card_box);
        mCardBox.setOnTouchBottomListener (new CardBoxOnTouchBottomListener ( ));
        mCardBox.setOnActiveAreaChangedListener (new CardBoxOnActiveAreaChangedListener ( ));
        mLoadingItem = new CardBox.LinearBlockItem (mContext);
        NetApi.getLatest (mLimit, 0, this, "addRequestSuccess");
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
                NetApi.getLatest (mLimit, mSkip, Latest.this, "addRequestSuccess");
            }
        }
    }

    class CardBoxOnActiveAreaChangedListener implements CardBox.OnActiveAreaChangedListener {

        @Override
        public void setActiveAreaOperation(Set <View> viewSet) {
            for (View view : viewSet) {
                ((MangaSelfCard) view).image.load ( );
            }
        }

        @Override
        public void setInActiveAreaOperation(Set <View> viewSet) {
            if(mInactivateErrorSet!=null){
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

    public void addRequestSuccess(Network.Response response) {
        Message message = Message.obtain ( );
        message.obj = response;
        message.what = PictureLatestHandler.ADD_ITEMS;
        this.mHandler.sendMessage (message);
    }

    private static class PictureLatestHandler extends Handler {

        static final int ADD_ITEMS = 100;

        WeakReference <PageBase> outerClass;

        PictureLatestHandler(PageBase activity) {
            outerClass = new WeakReference <> (activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Latest latest = (Latest) outerClass.get ( );
            if (msg.what == ADD_ITEMS) {
                addItems (msg, latest);
                latest.mIsLoading = false;
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
                    final MangaSelfCard normalItem = new MangaSelfCard (latest.mContext);
                    normalItem.title.setText (name);
                    JSONArray languages = info.getJSONArray ("languages");
                    String language = getLanguage (languages);
                    normalItem.clickTime.setText (Objects.requireNonNull (Integer.toString (clickedTimes)));
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
                    latest.mCardBox.addItem (normalItem);
                    ItemOnClickListener itemOnClickListener = new ItemOnClickListener (latest, item, normalItem);
                    normalItem.setOnClickListener (itemOnClickListener);
                }
                latest.mCardBox.deleteItem (latest.mLoadingItem);
                latest.mCardBox.addItem (latest.mLoadingItem);
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

        class ItemOnClickListener implements View.OnClickListener {
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
                    int uid = mLatest.mApp.mUser.mUid;
                    String resource_id = mItem.getJSONObject ("_id").getString ("$oid");
                    NetApi.addHistory (uid, resource_id);
                    NetApi.upClickedCount (resource_id);
                    int clickedTimes = Integer.parseInt ((String) mNormalItem.clickTime.getText ( )) + 1;
                    mNormalItem.clickTime.setText ((String) Integer.toString (clickedTimes));

                    Intent intent = new Intent ( );
                    intent.setClass (mLatest.mContext, PictureActivity.class);
                    intent.putExtra ("info", mItem.toString ( ));
                    mLatest.mContext.startActivity (intent);
                } catch (JSONException e) {
                    e.printStackTrace ( );
                }
            }
        }
    }
}
