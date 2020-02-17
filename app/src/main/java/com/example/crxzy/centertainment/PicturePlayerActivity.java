package com.example.crxzy.centertainment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.tools.Tool;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PicturePlayerActivity extends AppCompatActivity {
    int mStatus;
    int mImagesSize;
    int mLoadedImageSize;
    int mLoadedViewIndex = 0;
    int mLoadViewBatchSize = 3;
    boolean mIsMenuVisible = true;
    boolean mIsMenuRecommendationOpen = false;
    int mLoadDistance = 5;
    boolean isLoading = false;


    String mThumbId;
    Network mNetwork;
    ViewPager mBrowser;
    RelativeLayout mMenu;
    RelativeLayout mCover;
    TextView mCoverContent;
    ProgressBar mCoverProgressBar;
    List <String> mImageNames;
    Animation mCoverOpenAnimation;
    PicturePlayerActivity mContext;
    ImagePlayerHandler mImagePlayerHandler;
    ImagePlayerBrowserAdapter mImagePlayerBrowserAdapter;
    Animation mMenuRecommendationOpenAnimation;
    Animation mMenuRecommendationCloseAnimation;
    LinearLayout mIsMenuRecommendation;
    RelativeLayout mIsMenuRecommendationSwitch;
    ProgressBar mMenuProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate (savedInstanceState);
        setContentView (R.layout.independ_image_player);
        onInitiation ( );
    }

    private void onInitiation() {
        Tool.setFullWindow (mContext);

        loadsInfo ( );
        initImageBrowser ( );
        initCover ( );
        iniMenu ( );
    }

    private void loadsInfo() {
        mNetwork = new Network ( );
        Intent intent = getIntent ( );
        String imageNamesString = intent.getStringExtra ("imageNames");
        mImageNames = Arrays.asList (imageNamesString.split (","));
        mImagesSize = mImageNames.size ( );
        mStatus = intent.getIntExtra ("thumbStatus", 0);
        mThumbId = intent.getStringExtra ("thumbId");
    }

    private void initImageBrowser() {
        mBrowser = findViewById (R.id.image_player_browser);
        List <View> views = new ArrayList <> ( );
        for (int index = 0; index < mImagesSize; index++) {
            ImageView imageView = new ImageView (this);
            views.add (imageView);
            imageView.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    if (mIsMenuVisible) {
                        mMenu.getChildAt (0).setVisibility (View.INVISIBLE);
                        mIsMenuVisible = false;
                    } else {
                        mMenu.getChildAt (0).setVisibility (View.VISIBLE);
                        mIsMenuVisible = true;
                    }
                }
            });
        }
        mImagePlayerBrowserAdapter = new ImagePlayerBrowserAdapter (views);
        mBrowser.setAdapter (mImagePlayerBrowserAdapter);
        mBrowser.addOnPageChangeListener (new MyPageChangeListener ( ));
        if (mStatus == 4) {
            mLoadViewBatchSize = 10;
        }
        loadImages ( );
    }

    class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            if (i + mLoadDistance == mLoadedViewIndex) {
                isLoading = true;
                loadImages ( );
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

    private void initCover() {
        mCover = findViewById (R.id.image_player_cover);
        mCoverContent = findViewById (R.id.image_player_cover_content);
        mCoverProgressBar = findViewById (R.id.image_player_cover_progress_bar);

        mCoverOpenAnimation = AnimationUtils.loadAnimation (this, R.anim.image_player_cover_scale);
        mCoverProgressBar.setMax (mImagesSize);

        refreshCoverProgress ( );
    }

    private void iniMenu() {
        mMenu = findViewById (R.id.image_player_menu);
        mIsMenuRecommendation = findViewById (R.id.image_player_menu_recommendation);
        mIsMenuRecommendationSwitch = findViewById (R.id.image_player_menu_recommendation_switch);
        mMenuRecommendationOpenAnimation = AnimationUtils.loadAnimation (this, R.anim.image_player_cover_menu_recommendation_open);
        mMenuRecommendationOpenAnimation.setAnimationListener (new Animation.AnimationListener ( ) {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.alignWithParent = true;
                layoutParams.addRule (RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.setMargins (0, 0, 0, Tool.dip2px (mContext, -200));
                mIsMenuRecommendation.setLayoutParams (layoutParams);
                mIsMenuRecommendation.clearAnimation ( );
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mMenuRecommendationCloseAnimation = AnimationUtils.loadAnimation (this, R.anim.image_player_cover_menu_recommendation_close);
        mMenuRecommendationCloseAnimation.setAnimationListener (new Animation.AnimationListener ( ) {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.alignWithParent = true;
                layoutParams.addRule (RelativeLayout.ALIGN_PARENT_BOTTOM);
                mIsMenuRecommendation.setLayoutParams (layoutParams);
                mIsMenuRecommendation.clearAnimation ( );
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mIsMenuRecommendationSwitch.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                if (mIsMenuRecommendationOpen) {
                    findViewById (R.id.image_player_menu_recommendation).startAnimation (mMenuRecommendationOpenAnimation);
                    mIsMenuRecommendationOpen = false;
                } else {
                    findViewById (R.id.image_player_menu_recommendation).startAnimation (mMenuRecommendationCloseAnimation);
                    mIsMenuRecommendationOpen = true;
                }
            }
        });
        mMenuProgressBar = findViewById (R.id.image_player_menu_progress_bar);
        mMenuProgressBar.setMax (mImagesSize);
    }

    private void refreshCoverProgress() {
        mCoverProgressBar.setProgress (mLoadedImageSize);
        String text = mLoadedImageSize + "/" + mImagesSize;
        mCoverContent.setText (text);
    }

    private void refreshMenuProgress() {
        mMenuProgressBar.setProgress (mLoadedImageSize);
        if (mLoadedImageSize == mImagesSize) {
            mMenuProgressBar.setVisibility (View.INVISIBLE);
        }
    }

    private void loadImages() {
        mImagePlayerHandler = new ImagePlayerHandler (this);
        mNetwork = new Network ( );
        int endIndex = (mImageNames.size ( ) < mLoadedViewIndex + mLoadViewBatchSize) ? mImageNames.size ( ) : mLoadedViewIndex + mLoadViewBatchSize;
        for (int index = mLoadedViewIndex; index < endIndex; index++) {
            Network.Request request = new Network.Request ("http://10.0.0.2:4396/gallery/" + mThumbId + "/" + mImageNames.get (index));
            request.setSuccess (this, "loadImageSuccess");
            request.setMeta ("index", index);
            mNetwork.send (request);
        }
        mLoadedViewIndex = endIndex;
    }

    public void loadImageSuccess(Network.Response response) {
        Message message = mImagePlayerHandler.obtainMessage ( );
        message.what = ImagePlayerHandler.LOAD_IMAGE_SUCCESS;
        message.obj = response;
        mImagePlayerHandler.sendMessage (message);
    }

    class ImagePlayerBrowserAdapter extends PagerAdapter {
        List <View> mViewLists;

        ImagePlayerBrowserAdapter(List <View> viewLists) {
            super ( );
            this.mViewLists = viewLists;
        }

        void addView(List <View> viewLists) {
            this.mViewLists.addAll (viewLists);
            this.notifyDataSetChanged ( );
        }

        @Override
        public int getCount() {
            return mViewLists.size ( );
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView (mViewLists.get (position));
            return mViewLists.get (position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView (mViewLists.get (position));
        }

        void setImageBitmap(int index, Object content) {
            ((ImageView) mViewLists.get (index)).setImageBitmap ((Bitmap) content);
            this.notifyDataSetChanged ( );
        }
    }

    static class ImagePlayerHandler extends Handler {
        static final int LOAD_IMAGE_SUCCESS = 100;
        WeakReference <PicturePlayerActivity> mOuterClass;

        ImagePlayerHandler(PicturePlayerActivity outerClass) {
            mOuterClass = new WeakReference <> (outerClass);
        }

        @Override
        public void handleMessage(Message msg) {
            PicturePlayerActivity imagePlayer = mOuterClass.get ( );
            if (msg.what == LOAD_IMAGE_SUCCESS) {
                Network.Response response = (Network.Response) msg.obj;
                int index = (int) response.request.getMeta ("index");
                imagePlayer.mImagePlayerBrowserAdapter.setImageBitmap (index, response.content);
                imagePlayer.mLoadedImageSize += 1;
                int endIndex = (imagePlayer.mImageNames.size ( ) < imagePlayer.mLoadedViewIndex + imagePlayer.mLoadViewBatchSize) ? imagePlayer.mImageNames.size ( ) : imagePlayer.mLoadedViewIndex + imagePlayer.mLoadViewBatchSize;
                if (imagePlayer.mLoadedImageSize == imagePlayer.mLoadViewBatchSize) {
                    imagePlayer.mCover.startAnimation (imagePlayer.mCoverOpenAnimation);
                }
                if (imagePlayer.mLoadedImageSize == imagePlayer.mLoadedViewIndex) {
                    imagePlayer.isLoading = false;
                }
                imagePlayer.refreshCoverProgress ( );
                imagePlayer.refreshMenuProgress ( );
            }
        }
    }


}
