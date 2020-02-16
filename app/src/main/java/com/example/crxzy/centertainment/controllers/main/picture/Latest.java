package com.example.crxzy.centertainment.controllers.main.picture;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ScrollView;

import com.example.crxzy.centertainment.MainApplication;
import com.example.crxzy.centertainment.PictureActivity;
import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.system.ThirdPageBase;
import com.example.crxzy.centertainment.views.*;
import com.example.crxzy.centertainment.tools.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;

public class Latest extends ThirdPageBase {
    private ItemsBoxView mItemBox;
    private PictureLatestHandler mHandler;
    private ScrollView mScrollView;
    private Boolean mTouchBottomLock = false;
    private ItemsBoxView.LinearBlockItem mLoadingItem;
    private int mSkip;
    private int mLimit = 10;
    private MainApplication mApp;

    public Latest(AppCompatActivity context, View view, QuickPageModel.Page pageModel) {
        super (context, view, pageModel);
    }


    @Override
    public void onShow() {
        mItemBox = mContext.findViewById (R.id.picture_latest_items_box);
        mScrollView = mContext.findViewById (R.id.picture_latest_scroll_view);
        mApp = (MainApplication) mContext.getApplication ( );

        mLoadingItem = new ItemsBoxView.LinearBlockItem (mContext);
        NetApi.getLatest (mLimit, 0, this, "initRequestSuccess");
        this.mHandler = new PictureLatestHandler (this);
    }

    public void initRequestSuccess(Network.Response response) {
        Message message = Message.obtain ( );
        message.obj = response;
        message.what = PictureLatestHandler.INIT_ITEMS_BOX;
        this.mHandler.sendMessage (message);
    }

    public void addRequestSuccess(Network.Response response) {
        Message message = Message.obtain ( );
        message.obj = response;
        message.what = PictureLatestHandler.ADD_ITEMS;
        this.mHandler.sendMessage (message);
    }

    private static class PictureLatestHandler extends Handler {

        static final int INIT_ITEMS_BOX = 100;
        static final int ADD_ITEMS = 101;

        WeakReference <Latest> outerClass;

        PictureLatestHandler(Latest activity) {
            outerClass = new WeakReference <> (activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Latest latest = outerClass.get ( );
            switch (msg.what) {
                case INIT_ITEMS_BOX: {
                    addItems (msg, latest);
                    ItemsBoxOnScrollChangeListener itemsBoxOnScrollChangeListener = new ItemsBoxOnScrollChangeListener (latest);
                    latest.mScrollView.setOnScrollChangeListener (itemsBoxOnScrollChangeListener);
                }
                break;
                case ADD_ITEMS: {
                    addItems (msg, latest);
                    latest.mTouchBottomLock = false;
                }
                break;
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
                    final ItemsBoxView.NormalItem normalItem = new ItemsBoxView.NormalItem (latest.mContext);
                    normalItem.title.setText (name);
                    JSONArray languages = info.getJSONArray ("languages");
                    String language = getLanguage (languages);
                    normalItem.clickTime.setText ((String) Integer.toString (clickedTimes));
                    final String tagsString = item.getString ("source") + "." + language;
                    normalItem.sourceTag.setText (tagsString);
                    normalItem.pageCount.setText (info.getString ("page"));
                    normalItem.image.setImageURL ("http://10.0.0.2:4396/gallery/" + thumb.getString ("thumb_id") + "/" + image_names.get (0));
                    latest.mItemBox.addItem (normalItem);
                    ItemOnClickListener itemOnClickListener = new ItemOnClickListener (latest, item, normalItem);
                    normalItem.setOnClickListener (itemOnClickListener);
                }
                latest.mItemBox.deleteItem (latest.mLoadingItem);
                latest.mItemBox.addItem (latest.mLoadingItem);
                latest.mSkip += latest.mLimit;
            } catch (JSONException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
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
            ItemsBoxView.NormalItem mNormalItem;

            ItemOnClickListener(Latest context, JSONObject item, ItemsBoxView.NormalItem normalItem) {
                mLatest = context;
                mItem = item;
                mNormalItem = normalItem;
            }

            @Override
            public void onClick(View v) {
                try {
                    int uid = mLatest.mApp.mUser.mUid;
                    String resource_id = mItem.getJSONObject ("_id").getString ("$oid");
                    NetApi.addHistory(uid,resource_id);
                    NetApi.upClickedCount(resource_id);
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

        class ItemsBoxOnScrollChangeListener implements View.OnScrollChangeListener {
            Latest mContext;

            ItemsBoxOnScrollChangeListener(Latest latest) {
                mContext = latest;
            }

            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int itemBoxHeight = mContext.mItemBox.getHeight ( );
                int scrollViewHeight = mContext.mScrollView.getHeight ( );
                int threshold = 400;
                if ((itemBoxHeight - scrollViewHeight - scrollY <= threshold) && !mContext.mTouchBottomLock) {
                    mContext.mTouchBottomLock = true;
                    NetApi.getLatest (mContext.mLimit, mContext.mSkip, mContext, "addRequestSuccess");
                }
            }
        }
    }
}
