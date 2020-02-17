package com.example.crxzy.centertainment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.views.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PictureActivity extends AppCompatActivity {
    public static final int IMAGE_ALL_DOWNLOADED = 4;
    PictureActivity mContext = this;
    JSONObject mResource;
    JSONObject mThumb;
    JSONObject mInfo;
    ViewPager mImageBrowser;
    List <String> mImageNames;
    List <String> mArtists;
    List <String> mTags;
    String mThumbId;
    String mTitle;
    String mResourceId;
    int mLoadedViewIndex = 0;
    int mLoadViewBatchSize = 3;
    static final int LOAD_DISTANCE = 5;
    SubPicturePagerAdapter mAdapter;
    TextView mTagsArea;
    Button mBrowserButtonLike;
    Button mBrowserButtonSubScribe;
    boolean isLike = false;
    Network mNetwork;
    MainApplication mApp;
    SubPictureHandler mHandler;
    private boolean isSubscribe = false;
    private int mThumbStatus;
    private boolean mIsLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.independ_picture);
        onInitiation ( );
    }

    private void loadsInfo() {
        Intent intent = getIntent ( );
        String infoString = intent.getStringExtra ("info");
        try {
            mImageNames = new ArrayList <> ( );
            mArtists = new ArrayList <> ( );
            mTags = new ArrayList <> ( );
            mResource = new JSONObject (infoString);
            mResourceId = mResource.getJSONObject ("_id").getString ("$oid");
            mThumb = mResource.getJSONObject ("thumb");
            mInfo = mResource.getJSONObject ("info");
            mTitle = !mInfo.getString ("original_name").equals ("null") ? mInfo.getString ("original_name") : mInfo.getString ("name");
            mThumbId = mThumb.getString ("thumb_id");
            mThumbStatus = mThumb.getInt ("status");
            JSONArray imageNames = mThumb.getJSONArray ("image_names");
            for (int index = 0; index < imageNames.length ( ); index++) {
                mImageNames.add ((String) imageNames.get (index));
            }
            JSONArray artists = mInfo.getJSONArray ("artists");
            for (int index = 0; index < artists.length ( ); index++) {
                mArtists.add ((String) artists.get (index));
            }
            JSONArray tags = mInfo.getJSONArray ("tags");
            for (int index = 0; index < tags.length ( ); index++) {
                mTags.add ((String) tags.get (index));
            }
        } catch (JSONException e) {
            e.printStackTrace ( );
        }
    }

    private void onInitiation() {
        mApp = (MainApplication) mContext.getApplication ( );
        mNetwork = new Network ( );
        mHandler = new SubPictureHandler (this);
        loadsInfo ( );
        initImageBrowser ( );
        initAuthorInfo ( );
        initTagsArea ( );
    }

    private void initTagsArea() {
        mTagsArea = findViewById (R.id.sub_picture_tags);
        mTagsArea.setText (String.join (", ", mTags));
    }

    public void successLike(Network.Response response) {
        if ("true".equals (response.content)) {
            Message message = mHandler.obtainMessage ( );
            message.what = SubPictureHandler.CHANGE_LIKE_BUTTON_STATUS;
            message.obj = true;
            mHandler.sendMessage (message);
        }
    }

    public void successRemoveLike(Network.Response response) {
        if ("true".equals (response.content)) {
            Message message = mHandler.obtainMessage ( );
            message.what = SubPictureHandler.CHANGE_LIKE_BUTTON_STATUS;
            message.obj = false;
            mHandler.sendMessage (message);
        }
    }

    public void successIsLike(Network.Response response) {
        Message message = mHandler.obtainMessage ( );
        message.what = SubPictureHandler.CHANGE_LIKE_BUTTON_STATUS;
        if ("true".equals (response.content)) {
            message.obj = true;
            mHandler.sendMessage (message);
        } else if ("false".equals (response.content)) {
            message.obj = false;
            mHandler.sendMessage (message);
        }
    }

    public void successSubscribe(Network.Response response) {
        if ("true".equals (response.content)) {
            Message message = mHandler.obtainMessage ( );
            message.what = SubPictureHandler.CHANGE_SUBSCRIBE_BUTTON_STATUS;
            message.obj = true;
            mHandler.sendMessage (message);
        }
    }

    public void successRemoveSubscribe(Network.Response response) {
        if ("true".equals (response.content)) {
            Message message = mHandler.obtainMessage ( );
            message.what = SubPictureHandler.CHANGE_SUBSCRIBE_BUTTON_STATUS;
            message.obj = false;
            mHandler.sendMessage (message);
        }
    }

    public void successIsSubscribe(Network.Response response) {
        Message message = mHandler.obtainMessage ( );
        message.what = SubPictureHandler.CHANGE_SUBSCRIBE_BUTTON_STATUS;
        if ("true".equals (response.content)) {
            message.obj = true;
            mHandler.sendMessage (message);
        } else if ("false".equals (response.content)) {
            message.obj = false;
            mHandler.sendMessage (message);
        }
    }

    private void initImageBrowser() {
        mImageBrowser = findViewById (R.id.sub_picture_browser);
        if (mThumbStatus == IMAGE_ALL_DOWNLOADED) {
            mLoadViewBatchSize = 10;
            mImageBrowser.addOnPageChangeListener (new SubPageChangeListener ( ));
        }
        List <View> viewLists = getViews ( );
        mAdapter = new SubPicturePagerAdapter (viewLists);
        mImageBrowser.setAdapter (mAdapter);
        mBrowserButtonLike = findViewById (R.id.sub_picture_button_like);
        mBrowserButtonSubScribe = findViewById (R.id.sub_picture_button_subscribe);
        mBrowserButtonLike.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                if (!isLike) {
                    NetApi.addLike (mApp.mUser.mUid, mResourceId, mContext, "successLike");
                    Toast.makeText (mContext, "已标记为喜欢", Toast.LENGTH_SHORT).show ( );

                } else {
                    NetApi.removeLike (mApp.mUser.mUid, mResourceId, mContext, "successRemoveLike");
                    Toast.makeText (mContext, "已标记为不喜欢", Toast.LENGTH_SHORT).show ( );
                }
            }
        });
        mBrowserButtonSubScribe.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                if (!isSubscribe) {
                    NetApi.addSubscribe (mApp.mUser.mUid, mResourceId, mContext, "successSubscribe");
                    Toast.makeText (mContext, "已发送订阅请求", Toast.LENGTH_SHORT).show ( );
                } else {
                    NetApi.removeSubscribe (mApp.mUser.mUid, mResourceId, mContext, "successRemoveSubscribe");
                    Toast.makeText (mContext, "已取消订阅", Toast.LENGTH_SHORT).show ( );
                }
            }
        });

        NetApi.isLike (mApp.mUser.mUid, mResourceId, mContext, "successIsLike");
        NetApi.isSubscribe (mApp.mUser.mUid, mResourceId, mContext, "successIsSubscribe");
    }

    private List <View> getViews() {
        List <View> viewLists = new ArrayList <> ( );
        int endIndex = (mImageNames.size ( ) < mLoadedViewIndex + mLoadViewBatchSize) ? mImageNames.size ( ) : mLoadedViewIndex + mLoadViewBatchSize;
        for (int index = mLoadedViewIndex; index < endIndex; index++) {
            ImageView imageView = new ImageView (this);
            imageView.setTag (index);
            SubPictureClickListener clickListener = new SubPictureClickListener ( );
            imageView.setOnClickListener (clickListener);
            imageView.setScaleType (android.widget.ImageView.ScaleType.FIT_CENTER);
            imageView.setImageURL ("http://10.0.0.2:4396/gallery/" + mThumbId + "/" + mImageNames.get (index));
            viewLists.add (imageView);
        }
        mIsLoading = false;
        mLoadedViewIndex = endIndex;
        return viewLists;
    }

    private void initAuthorInfo() {
        ImageView cover = findViewById (R.id.sub_picture_cover);
        TextView title = findViewById (R.id.sub_picture_title);
        title.setText (mTitle);
        TextView artists = findViewById (R.id.sub_picture_artists);
        artists.setText (String.join (", ", mArtists));
        cover.setImageURL ("http://10.0.0.2:4396/gallery/" + mThumbId + "/" + mImageNames.get (0));
        cover.setScaleType (android.widget.ImageView.ScaleType.FIT_XY);
    }

    public class SubPictureClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent ( );
            intent.setClass (mContext, PicturePlayerActivity.class);
            intent.putExtra ("imageNames", String.join (",", mImageNames));
            intent.putExtra ("thumbId", mThumbId);
            intent.putExtra ("thumbStatus", mThumbStatus);
            mContext.startActivity (intent);
        }
    }

    public class SubPicturePagerAdapter extends PagerAdapter {
        List <View> viewLists;


        SubPicturePagerAdapter(List <View> viewLists) {
            super ( );
            this.viewLists = viewLists;
        }

        void addView(List <View> viewLists) {
            this.viewLists.addAll (viewLists);
            this.notifyDataSetChanged ( );
        }

        @Override
        public int getCount() {
            return viewLists.size ( );
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView (viewLists.get (position));
            return viewLists.get (position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView (viewLists.get (position));
        }
    }

    public class SubPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
            if (i + LOAD_DISTANCE == mAdapter.viewLists.size ( )) {
                mAdapter.addView (mContext.getViews ( ));
                mIsLoading = true;
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

    static class SubPictureHandler extends Handler {
        static final int CHANGE_LIKE_BUTTON_STATUS = 100;
        static final int CHANGE_SUBSCRIBE_BUTTON_STATUS = 101;
        WeakReference <PictureActivity> mOuterClass;

        SubPictureHandler(PictureActivity pictureActivity) {
            mOuterClass = new WeakReference <> (pictureActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            PictureActivity pictureActivity = mOuterClass.get ( );

            if (msg.what == CHANGE_LIKE_BUTTON_STATUS) {
                if ((boolean) msg.obj) {
                    pictureActivity.isLike = true;
                    pictureActivity.mBrowserButtonLike.setText (R.string.like);
                } else {
                    pictureActivity.isLike = false;
                    pictureActivity.mBrowserButtonLike.setText (R.string.unlike);
                }
            } else if (msg.what == CHANGE_SUBSCRIBE_BUTTON_STATUS) {
                if ((boolean) msg.obj) {
                    pictureActivity.isSubscribe = true;
                    pictureActivity.mBrowserButtonSubScribe.setText (R.string.subscribe);
                } else {
                    pictureActivity.isSubscribe = false;
                    pictureActivity.mBrowserButtonSubScribe.setText (R.string.unsubscribe);
                }
            }
        }
    }
}
