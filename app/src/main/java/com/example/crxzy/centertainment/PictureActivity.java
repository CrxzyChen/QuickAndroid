package com.example.crxzy.centertainment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crxzy.centertainment.models.MangaResource;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.tools.Tool;
import com.example.crxzy.centertainment.views.ImageView;
import com.example.crxzy.centertainment.views.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PictureActivity extends AppCompatActivity {
    public static final int IMAGE_ALL_DOWNLOADED = 4;
    PictureActivity mContext = this;
    MangaResource mResource;
    ViewPager mImageBrowser;
    boolean mIsLoading = false;
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
    LinearLayout mRecommendArea;
    LinearLayout mRecommendContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.independ_picture);
        onInitiation ( );
    }

    private void loadsInfo() {
        Intent intent = getIntent ( );
        String infoString = intent.getStringExtra ("info");
        mResource = new MangaResource (infoString);
    }

    private void onInitiation() {
        mApp = (MainApplication) mContext.getApplication ( );
        mNetwork = new Network ( );
        mHandler = new SubPictureHandler (this);
        loadsInfo ( );
        initImageBrowser ( );
        initAuthorInfo ( );
        initTagsArea ( );
        initRecommendArea ( );
    }

    private void initRecommendArea() {
        mRecommendArea = findViewById (R.id.independ_picture_recommend);
        mRecommendContainer = findViewById (R.id.independ_picture_recommend_container);
        mRecommendContainer.setBackgroundColor (getColor (R.color.colorBackground));

        if (mResource.Recommend.length ( ) == 0) {
            mRecommendArea.setVisibility (View.GONE);
        } else {
            NetApi.getResourceByIds (mResource.Recommend.toString ( ), this, "successGetRecommend");
        }
    }

    private void initTagsArea() {
        mTagsArea = findViewById (R.id.sub_picture_tags);
        mTagsArea.setText (String.join (", ", mResource.Tags));
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

    public void successGetRecommend(Network.Response response) {
        Message message = mHandler.obtainMessage ( );
        message.what = SubPictureHandler.LOAD_RECOMMEND;
        message.obj = response.content;
        mHandler.sendMessage (message);
    }

    private void initImageBrowser() {
        mImageBrowser = findViewById (R.id.sub_picture_browser);
        if (mResource.ThumbStatus == IMAGE_ALL_DOWNLOADED) {
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
                    NetApi.addLike (mApp.mUser.mUid, mResource.ResourceId, mContext, "successLike");
                    Toast.makeText (mContext, "已标记为喜欢", Toast.LENGTH_SHORT).show ( );

                } else {
                    NetApi.removeLike (mApp.mUser.mUid, mResource.ResourceId, mContext, "successRemoveLike");
                    Toast.makeText (mContext, "已标记为不喜欢", Toast.LENGTH_SHORT).show ( );
                }
            }
        });
        mBrowserButtonSubScribe.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                if (!isSubscribe) {
                    NetApi.addSubscribe (mApp.mUser.mUid, mResource.ResourceId, mContext, "successSubscribe");
                    Toast.makeText (mContext, "已发送订阅请求", Toast.LENGTH_SHORT).show ( );
                } else {
                    NetApi.removeSubscribe (mApp.mUser.mUid, mResource.ResourceId, mContext, "successRemoveSubscribe");
                    Toast.makeText (mContext, "已取消订阅", Toast.LENGTH_SHORT).show ( );
                }
            }
        });

        NetApi.isLike (mApp.mUser.mUid, mResource.ResourceId, mContext, "successIsLike");
        NetApi.isSubscribe (mApp.mUser.mUid, mResource.ResourceId, mContext, "successIsSubscribe");
    }

    private List <View> getViews() {
        List <View> viewLists = new ArrayList <> ( );
        int endIndex = (mResource.ImageNames.size ( ) < mLoadedViewIndex + mLoadViewBatchSize) ? mResource.ImageNames.size ( ) : mLoadedViewIndex + mLoadViewBatchSize;
        for (int index = mLoadedViewIndex; index < endIndex; index++) {
            ImageView imageView = new ImageView (this);
            imageView.setTag (index);
            SubPictureClickListener clickListener = new SubPictureClickListener ( );
            imageView.setOnClickListener (clickListener);
            imageView.setScaleType (android.widget.ImageView.ScaleType.FIT_CENTER);
            imageView.setImageURL ("http://10.0.0.2:4396/gallery/" + mResource.ThumbId + "/" + mResource.ImageNames.get (index));
            viewLists.add (imageView);
        }
        mIsLoading = false;
        mLoadedViewIndex = endIndex;
        return viewLists;
    }

    private void initAuthorInfo() {
        ImageView cover = findViewById (R.id.sub_picture_cover);
        TextView title = findViewById (R.id.sub_picture_title);
        title.setText (mResource.Title);
        TextView artists = findViewById (R.id.sub_picture_artists);
        artists.setText (String.join (", ", mResource.Artists));
        cover.setImageURL ("http://10.0.0.2:4396/gallery/" + mResource.ThumbId + "/" + mResource.ImageNames.get (0));
        cover.setScaleType (android.widget.ImageView.ScaleType.FIT_XY);
    }

    public class SubPictureClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent ( );
            intent.setClass (mContext, PicturePlayerActivity.class);
            intent.putExtra ("imageNames", String.join (",", mResource.ImageNames));
            intent.putExtra ("thumbId", mResource.ThumbId);
            intent.putExtra ("thumbStatus", mResource.ThumbStatus);
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
        static final int LOAD_RECOMMEND = 102;

        WeakReference <PictureActivity> mOuterClass;

        SubPictureHandler(PictureActivity pictureActivity) {
            mOuterClass = new WeakReference <> (pictureActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            final PictureActivity pictureActivity = mOuterClass.get ( );

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
            } else if (msg.what == LOAD_RECOMMEND) {
                try {
                    final JSONArray recommends = (JSONArray) msg.obj;
                    for (int index = 0; index < recommends.length ( ); index++) {
                        final JSONObject recommend = recommends.getJSONObject (index);
                        MangaResource resource = new MangaResource (recommend);
                        RoundedImageView imageView = new RoundedImageView (pictureActivity);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams (Tool.dip2px (pictureActivity, 100), LinearLayout.LayoutParams.MATCH_PARENT);
                        imageView.setCornerSize (Tool.dip2px (pictureActivity, 10));
                        layoutParams.setMargins (10, 10, 10, 10);
                        imageView.setImageURL ("http://10.0.0.2:4396/gallery/" + resource.ThumbId + "/" + resource.ImageNames.get (0) + "?height=480&width=360");
                        imageView.setLayoutParams (layoutParams);
                        imageView.setTag (index);
                        imageView.setOnClickListener (new View.OnClickListener ( ) {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent ( );
                                intent.setClass (pictureActivity, PictureActivity.class);
                                intent.putExtra ("info", recommend.toString ( ));
                                pictureActivity.startActivity (intent);
                            }
                        });
                        pictureActivity.mRecommendContainer.addView (imageView);
                    }
                } catch (JSONException e) {
                    e.printStackTrace ( );
                }
            }
        }
    }
}
