package com.example.crxzy.centertainment.controllers;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.crxzy.centertainment.PictureActivity;
import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.system.FirstPageBase;
import com.example.crxzy.centertainment.system.PageBase;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.tools.Tool;
import com.example.crxzy.centertainment.views.ItemsBoxView;
import com.example.crxzy.centertainment.views.MangaSelfCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Subscribe extends FirstPageBase {
    private MyHandler mHandler = new MyHandler (this);
    private ItemsBoxView mViewContainer;

    public Subscribe(AppCompatActivity context, View view, QuickPageModel.Page pageModel) {
        super (context, view, pageModel);
    }

    @Override
    public void onInitiation() {
        super.onInitiation ( );
        mViewContainer = mView.findViewById (R.id.subscribe_container);
        NetApi.getSubscribe (mApp.mUser.mUid, this, "getSubscribeSuccess");
    }

    public void getSubscribeSuccess(Network.Response response) {
        Message message = Message.obtain ( );
        message.what = MyHandler.LOAD_SUBSCRIBE;
        message.obj = response;
        mHandler.sendMessage (message);
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
                        final MangaSelfCard mangaSelfCard = new MangaSelfCard (subscribe.mContext);
                        mangaSelfCard.title.setText (name);
                        JSONArray languages = info.getJSONArray ("languages");
                        String language = null;
                        for (int index_2 = 0; index_2 < languages.length ( ); index_2++) {
                            language = (String) languages.get (index_2);
                            if (!language.equals ("translated")) {
                                break;
                            }
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
                        mangaSelfCard.image.setImageURL ("http://10.0.0.2:4396/gallery/" + thumb.getString ("thumb_id") + "/" + image_names.get (0));
                        subscribe.mViewContainer.addItem (mangaSelfCard);
                        ItemOnClickListener itemOnClickListener = new ItemOnClickListener (subscribe, item, mangaSelfCard);
                        mangaSelfCard.setOnClickListener (itemOnClickListener);
                    }
                } catch (JSONException e) {
                    e.printStackTrace ( );
                }
            }
        }

        class ItemOnClickListener implements View.OnClickListener {
            PageBase mPage;
            JSONObject mItem;
            MangaSelfCard mNormalItem;

            ItemOnClickListener(PageBase page, JSONObject item, MangaSelfCard normalItem) {
                mPage = page;
                mItem = item;
                mNormalItem = normalItem;
            }

            @Override
            public void onClick(View v) {
                try {
                    int uid = mPage.mApp.mUser.mUid;
                    String resource_id = mItem.getJSONObject ("_id").getString ("$oid");
                    NetApi.addHistory (uid, resource_id);
                    NetApi.upClickedCount (resource_id);
                    int clickedTimes = Integer.parseInt ((String) mNormalItem.clickTime.getText ( )) + 1;
                    mNormalItem.clickTime.setText ((String) Integer.toString (clickedTimes));
                    Intent intent = new Intent ( );
                    intent.setClass (mPage.mContext, PictureActivity.class);
                    intent.putExtra ("info", mItem.toString ( ));
                    mPage.mContext.startActivity (intent);
                } catch (JSONException e) {
                    e.printStackTrace ( );
                }
            }
        }
    }
}
