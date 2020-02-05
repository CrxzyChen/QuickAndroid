package com.example.crxzy.centertainment.controllers.picture;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import com.example.crxzy.centertainment.PictureActivity;
import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.system.ClassSecondPageBase;
import com.example.crxzy.centertainment.views.*;
import com.example.crxzy.centertainment.tools.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;

public class Latest extends ClassSecondPageBase {
    private ItemsBoxView mItemBox;
    private PictureLatestHandler mHandler;
    private ScrollView mScrollView;
    private Boolean mTouchBottomLock = false;
    private Network mNetwork;
    private ItemsBoxView.LinearBlockItem mLoadingItem;
    private int mSkip;
    private int mLimit = 10;

    public Latest(AppCompatActivity context, View view, String currentPageName) {
        super (context, view, currentPageName);
    }

    @Override
    public void onShow() {
        mItemBox = mContext.findViewById (R.id.picture_latest_items_box);
        mScrollView = mContext.findViewById (R.id.picture_latest_scroll_view);
        mLoadingItem = new ItemsBoxView.LinearBlockItem (mContext);
        mNetwork = new Network ( );
        Network.Request request = mNetwork.InstanceRequest ("http://10.0.0.2/CEntertainment/Manga/Latest.json?limit=" + mLimit);
        request.setSuccess (this, "initRequestSuccess");
        mNetwork.send (request);
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
                    try {
                        addItems (msg, latest);
                        latest.mScrollView.setOnScrollChangeListener (new View.OnScrollChangeListener ( ) {
                            @Override
                            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                                int itemBoxHeight = latest.mItemBox.getHeight ( );
                                int scrollViewHeight = latest.mScrollView.getHeight ( );
                                int threshold = 400;
                                if ((itemBoxHeight - scrollViewHeight - scrollY <= threshold) && !latest.mTouchBottomLock) {
                                    latest.mTouchBottomLock = true;
                                    Network.Request request = new Network.Request ("http://10.0.0.2/CEntertainment/Manga/Latest.json?limit=" + latest.mLimit + "&skip=" + latest.mSkip);
                                    request.setSuccess (latest, "addRequestSuccess");
                                    latest.mNetwork.send (request);
                                }
                            }
                        });
                    } catch (JSONException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace ( );
                    }
                }
                break;
                case ADD_ITEMS: {
                    try {
                        addItems (msg, latest);
                        latest.mTouchBottomLock = false;
                    } catch (JSONException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace ( );
                    }
                }
                break;
            }
        }

        private void addItems(Message msg, final Latest latest) throws JSONException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            JSONArray json = ((JSONArray) ((Network.Response) msg.obj).content);
            for (int index = 0; index < json.length ( ); index++) {
                final JSONObject item = json.getJSONObject (index);
                JSONObject info = item.getJSONObject ("info");
                JSONObject thumb = item.getJSONObject ("thumb");
                JSONArray image_names = thumb.getJSONArray ("image_names");
                String name = (!"null".equals (info.getString ("original_name"))) ? info.getString ("original_name") : info.getString ("name");
                ItemsBoxView.NormalItem normalItem = new ItemsBoxView.NormalItem (latest.mContext);
                normalItem.title.setText (name);
                JSONArray languages = info.getJSONArray ("languages");
                String language = getLanguage (languages);
                normalItem.clickTime.setText ("233");
                final String tagsString = item.getString ("source") + "." + language;
                normalItem.sourceTag.setText (tagsString);
                normalItem.pageCount.setText (info.getString ("page"));
                normalItem.image.setImageURL ("http://10.0.0.2:4396/gallery/" + thumb.getString ("thumb_id") + "/" + image_names.get (0));
                latest.mItemBox.addItem (normalItem);
                normalItem.setOnClickListener (new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent ( );
                        intent.setClass (latest.mContext, PictureActivity.class);
                        intent.putExtra ("info", item.toString ( ));
                        latest.mContext.startActivity (intent);
                    }
                });
            }
            latest.mItemBox.deleteItem (latest.mLoadingItem);
            latest.mItemBox.addItem (latest.mLoadingItem);
            latest.mSkip += latest.mLimit;
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
    }
}
