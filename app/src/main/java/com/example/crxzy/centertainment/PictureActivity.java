package com.example.crxzy.centertainment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crxzy.centertainment.views.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PictureActivity extends AppCompatActivity {
    PictureActivity mContext;
    JSONObject mResource;
    JSONObject mThumb;
    JSONObject mInfo;
    ViewPager mImageBrowser;
    List <String> mImageNames;
    List <String> mArtists;
    List <String> mTags;
    String mThumbId;
    String mTitle;
    int mLoadedViewIndex = 0;
    final int mLoadViewBatchSize = 3;
    SubPicturePagerAdapter mAdapter;
    TextView mTagsArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate (savedInstanceState);
        setContentView (R.layout.sub_picture);

        Intent intent = getIntent ( );
        String infoString = intent.getStringExtra ("info");
        onInitiation (infoString);
    }

    private void loadsInfo(String infoString) {
        try {
            mImageNames = new ArrayList <> ( );
            mArtists = new ArrayList <> ( );
            mTags = new ArrayList <> ( );
            mResource = new JSONObject (infoString);
            mThumb = mResource.getJSONObject ("thumb");
            mInfo = mResource.getJSONObject ("info");
            mTitle = !mInfo.getString ("original_name").equals ("null") ? mInfo.getString ("original_name") : mInfo.getString ("name");
            mThumbId = mThumb.getString ("thumb_id");
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

    private void onInitiation(String infoString) {
        loadsInfo (infoString);
        initImageBrowser ( );
        initAuthorInfo ( );
        initTagsArea ( );
    }

    private void initTagsArea() {
        mTagsArea = findViewById (R.id.sub_picture_tags);
        mTagsArea.setText (String.join (", ", mTags));
    }

    private void initImageBrowser() {
        mImageBrowser = findViewById (R.id.sub_picture_browser);
        List <View> viewLists = getViews ( );
        mAdapter = new SubPicturePagerAdapter (viewLists);
        mImageBrowser.setAdapter (mAdapter);
        //Auto load other images
//        SubPageChangeListener mPageListener = new SubPageChangeListener ( );
//        mImageBrowser.addOnPageChangeListener (mPageListener);
    }

    @NonNull
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
        static final int LOAD_DISTANCE = 2;

        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
            if (i + LOAD_DISTANCE == mAdapter.viewLists.size ( )) {
                mAdapter.addView (mContext.getViews ( ));
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }
}
