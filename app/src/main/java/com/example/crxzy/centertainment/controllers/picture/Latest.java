package com.example.crxzy.centertainment.controllers.picture;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

    public Latest(AppCompatActivity context, View view, String currentPageName) {
        super (context, view, currentPageName);
    }

    @Override
    public void onShow() {
        Network network = new Network ( );
        Network.Request request = network.InstanceRequest ("http://10.0.0.2/CEntertainment/Manga/Latest.json?limit=100");
        request.setSuccess (this, "success");
        network.send (request);
        this.mHandler = new PictureLatestHandler (this);
    }

    public void success(Network.Response response) {
        Message message = Message.obtain ( );
        message.obj = response;
        message.what = PictureLatestHandler.ADD_ITEM;
        this.mHandler.sendMessage (message);
    }

    private static class PictureLatestHandler extends Handler {

        static final int ADD_ITEM = 100;
        WeakReference <Latest> outerClass;

        PictureLatestHandler(Latest activity) {
            outerClass = new WeakReference <Latest> (activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Latest latest = outerClass.get ( );
            if (msg.what == ADD_ITEM) {
                JSONArray json = ((JSONArray) ((Network.Response) msg.obj).content);
                latest.mItemBox = latest.mContext.findViewById (R.id.picture_latest_itemsbox);
                for (int index = 0; index < json.length ( ); index++) {
                    JSONObject item = null;
                    try {
                        item = json.getJSONObject (index);
                        JSONObject info = item.getJSONObject ("info");
                        JSONObject thumb = item.getJSONObject ("thumb");
                        JSONArray image_names = thumb.getJSONArray ("image_names");
                        String name = (!"null".equals (info.getString ("original_name"))) ? info.getString ("original_name") : info.getString ("name");
                        ItemsBoxView.NormalItem normalItem = new ItemsBoxView.NormalItem (latest.mContext);
                        normalItem.title.setText (name);
                        normalItem.image.setImageURL ("http://10.0.0.2:4396/gallery/" + thumb.getString ("thumb_id") + "/" + image_names.get (0));
                        latest.mItemBox.addItem (normalItem);
                    } catch (JSONException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace ( );
                    }
                }
            }
        }
    }
}
