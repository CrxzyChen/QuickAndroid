package com.example.crxzy.centertainment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.crxzy.centertainment.views.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PictureActivity extends AppCompatActivity {
    JSONObject mResource;
    JSONObject mThumb;
    JSONObject mInfo;
    ViewPager mImageBrowser;
    List <String> mImageNames;
    List <String> mArtists;
    List <String> mTags;
    String mThumbId;
    String mTitle;
    TextView mTagsArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        List <View> viewLists = new ArrayList <> ( );
        for (int index = 0; index < (mImageNames.size ( ) < 3 ? mImageNames.size ( ) : 3); index++) {
            ImageView imageView = new ImageView (this);
            imageView.setScaleType (android.widget.ImageView.ScaleType.FIT_CENTER);
            imageView.setImageURL ("http://10.0.0.2:4396/gallery/" + mThumbId + "/" + mImageNames.get (index));
            viewLists.add (imageView);
        }
        SubPicturePagerAdapter mAdapter = new SubPicturePagerAdapter (viewLists);
        mImageBrowser.setAdapter (mAdapter);
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

    public class SubPicturePagerAdapter extends PagerAdapter {
        List <View> viewLists;


        public SubPicturePagerAdapter(List <View> viewLists) {
            super ( );
            this.viewLists = viewLists;
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
}
