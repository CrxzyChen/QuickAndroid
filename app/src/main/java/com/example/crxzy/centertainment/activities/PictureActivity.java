package com.example.crxzy.centertainment.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.models.PictureResource;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.system.MainApplication;
import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.tools.Tool;
import com.example.crxzy.centertainment.views.ImageView;
import com.example.crxzy.centertainment.views.LabelBox;
import com.example.crxzy.centertainment.views.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PictureActivity extends AppCompatActivity {
    public final int IMAGE_ALL_DOWNLOADED = 4;
    public int SEARCH_CLOSE_DISTANCE = 100;
    PictureActivity mContext = this;
    PictureResource mResource;
    ViewPager mImageBrowser;
    boolean mIsLoading = false;
    int mLoadedViewIndex = 0;
    int mLoadViewBatchSize = 3;
    static final int LOAD_DISTANCE = 5;
    SubPicturePagerAdapter mAdapter;
    LabelBox mTagsArea;
    Button mBrowserButtonLike;
    Button mBrowserButtonSubScribe;
    boolean mIsLike = false;
    Network mNetwork;
    MainApplication mApp;
    SubPictureHandler mHandler;
    private boolean mIsSubscribe = false;
    LinearLayout mRecommendArea;
    LinearLayout mRecommendContainer;
    private RelativeLayout mSearchArea;
    static private ViewGroup.MarginLayoutParams mSearchAreaParam;
    private ScrollView mContainer;
    private float mContainerTouchX;
    private float mContainerLastX;
    private float mContainerTouchY;
    private float mContainerLastY;
    static private boolean mIsSearchAreaOperated = false;
    private boolean mIsContainerOperated = false;
    private boolean mIsPlayAnimation = false;
    static private boolean mSearchAreaStatus = false;
    LinearLayout mLabelPopupWindowContainer;
    PopupWindow mLabelPopupWindow;
    static LabelBox mSearchAreaMark;
    static LabelBox mSearchAreaFilter;
    private LabelBox.Label mSelectedLabel;
    private float mSearchAreaLastX;
    private float mSearchAreaTouchX;
    private ScrollView mSearchAreaContainer;
    private Button mSearchAreaSearch;
    private Button mSearchAreaSave;
    private ViewGroup mSearchAreaLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.independ_picture);
        onInitiation ( );
    }

    private void loadsInfo() {
        Intent intent = getIntent ( );
        String infoString = intent.getStringExtra ("info");
        mResource = new PictureResource (infoString);
    }

    private void onInitiation() {
        mApp = (MainApplication) mContext.getApplication ( );
        mNetwork = new Network ( );
        mHandler = new SubPictureHandler (this);
        mContainer = findViewById (R.id.sub_picture_container);

        SEARCH_CLOSE_DISTANCE = Tool.dip2px (this, SEARCH_CLOSE_DISTANCE);

        loadsInfo ( );
        initImageBrowser ( );
        initAuthorInfo ( );
        initTagsArea ( );
        initRecommendArea ( );
        initSearchArea ( );
        onInitLabelPopupWindow ( );
    }

    private void onInitLabelPopupWindow() {
        mLabelPopupWindowContainer = new LinearLayout (mContext);
        mLabelPopupWindowContainer.setOrientation (LinearLayout.VERTICAL);
        LinearLayout.LayoutParams labelPopupWindowContainerParam = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mLabelPopupWindowContainer.setLayoutParams (labelPopupWindowContainerParam);


        TextView globeMarked = getLabelPopupWindowItem (getText (R.string.import_as_globe_mark));
        TextView globeFilter = getLabelPopupWindowItem (getText (R.string.import_as_globe_filter));
        TextView searchMarked = getLabelPopupWindowItem (getText (R.string.import_as_search_mark));
        TextView searchFilter = getLabelPopupWindowItem (getText (R.string.import_as_search_filter));
        globeMarked.setOnClickListener (new OnAddGlobeLabelListener ("mark"));
        globeFilter.setOnClickListener (new OnAddGlobeLabelListener ("filter"));
        searchMarked.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                PictureActivity.this.setSearchAreaStatus (true);
                mSearchAreaMark.addLabel (new LabelBox.CancelAbleLabel (mContext, ((String) mSelectedLabel.mTextView.getText ( ))));
                mLabelPopupWindow.dismiss ( );
            }
        });

        searchFilter.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                PictureActivity.this.setSearchAreaStatus (true);
                mSearchAreaFilter.addLabel (new LabelBox.CancelAbleLabel (mContext, ((String) mSelectedLabel.mTextView.getText ( ))));
                mLabelPopupWindow.dismiss ( );
            }
        });
        mLabelPopupWindowContainer.addView (globeMarked);
        mLabelPopupWindowContainer.addView (globeFilter);
        mLabelPopupWindowContainer.addView (searchMarked);
        mLabelPopupWindowContainer.addView (searchFilter);

        mLabelPopupWindow = new PopupWindow (mLabelPopupWindowContainer, ViewGroup.LayoutParams.WRAP_CONTENT, Tool.dip2px (this, 150));//参数为1.View 2.宽度 3.高度
        mLabelPopupWindow.setBackgroundDrawable (getDrawable (R.drawable.label_popup_window));
        mLabelPopupWindow.setTouchable (true);
        mLabelPopupWindow.setOutsideTouchable (true);
        mLabelPopupWindow.setElevation (Tool.dip2px (mContext, 10));
    }

    class OnAddGlobeLabelListener implements View.OnClickListener {
        private final String mLabelKind;

        OnAddGlobeLabelListener(String labelKind) {
            mLabelKind = labelKind;
        }

        @Override
        public void onClick(View v) {
            JSONObject config = (JSONObject) mApp.mUser.getConfig ("picture_common");
            if (config != null && config.has (mLabelKind)) {
                try {
                    JSONArray labels = config.getJSONArray (mLabelKind);
                    boolean isContainLabel = false;
                    for (int index = 0; index < labels.length ( ); index++) {
                        if (labels.getString (index).contentEquals (mSelectedLabel.mTextView.getText ( ))) {
                            isContainLabel = true;
                            break;
                        }
                    }
                    if (!isContainLabel) {
                        labels.put (mSelectedLabel.mTextView.getText ( ));
                        mApp.mUser.putConfig ("picture_common", config);
                        Toast.makeText (mContext, "Add label to globe " + mLabelKind + " success!", Toast.LENGTH_SHORT).show ( );
                    } else {
                        Toast.makeText (mContext, "label is existed in globe " + mLabelKind + "!", Toast.LENGTH_SHORT).show ( );
                    }
                    mLabelPopupWindow.dismiss ( );
                } catch (JSONException e) {
                    e.printStackTrace ( );
                }
            }
        }
    }

    @NonNull
    private TextView getLabelPopupWindowItem(CharSequence content) {
        LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemParam.setMargins (Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5));
        TextView textView = new TextView (mContext);
        textView.setPadding (5, 5, 5, 5);
        textView.setLayoutParams (itemParam);
        textView.setClickable (true);
        textView.setBackground (getDrawable (R.drawable.label_popup_item));
        textView.setText (content);
        textView.setTextColor (getColor (R.color.colorText));
        return textView;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initSearchArea() {
        mSearchArea = findViewById (R.id.sub_picture_search);
        mSearchAreaContainer = findViewById (R.id.sub_picture_search_container);
        mSearchAreaParam = (ViewGroup.MarginLayoutParams) mSearchArea.getLayoutParams ( );
        mSearchAreaLanguage = mSearchArea.findViewById (R.id.sub_picture_search_language);
        if (mSearchAreaMark != null) {
            LabelBox currentView = mSearchArea.findViewById (R.id.sub_picture_search_mark);
            List <String> labels = mSearchAreaMark.getAllLabels ( );
            for (String label : labels) {
                currentView.addLabel (new LabelBox.CancelAbleLabel (mContext, label));
            }
            mSearchAreaMark = currentView;
        } else {
            mSearchAreaMark = mSearchArea.findViewById (R.id.sub_picture_search_mark);
        }
        if (mSearchAreaFilter != null) {
            LabelBox currentView = mSearchArea.findViewById (R.id.sub_picture_search_filter);
            List <String> labels = mSearchAreaFilter.getAllLabels ( );
            for (String label : labels) {
                currentView.addLabel (new LabelBox.CancelAbleLabel (mContext, label));
            }
            mSearchAreaFilter = currentView;
        } else {
            mSearchAreaFilter = mSearchArea.findViewById (R.id.sub_picture_search_filter);
        }
        mSearchAreaSearch = mSearchArea.findViewById (R.id.sub_picture_search_search);
        mSearchAreaSearch.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                turnToSearchPage ( );
            }
        });
        mSearchAreaSave = mSearchArea.findViewById (R.id.sub_picture_search_save);
        mSearchAreaSave.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

            }
        });
        mSearchAreaContainer.setOnTouchListener (new searchAreaTouchEventListener ( ));
        mContainer.setOnTouchListener (new containerTouchEventListener ( ));
        mContainer.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                if (mSearchAreaStatus) {
                    setSearchAreaStatus (false);
                }
            }
        });
    }

    private void turnToSearchPage() {
        List <String> marked_labels = mSearchAreaMark.getAllLabels ( );
        List <String> filter_labels = mSearchAreaFilter.getAllLabels ( );
        List <String> language_labels = new ArrayList <> ( );
        for (int index = 0; index < mSearchAreaLanguage.getChildCount ( ); index++) {
            CheckBox checkBox = (CheckBox) mSearchAreaLanguage.getChildAt (index);
            if (checkBox.isChecked ( )) {
                language_labels.add (((String) checkBox.getText ( )).toLowerCase ( ));
            }
        }
        JSONObject json = new JSONObject ( );
        try {
            json.put ("language", new JSONArray (language_labels));
            json.put ("mark", new JSONArray (marked_labels));
            json.put ("filter", new JSONArray (filter_labels));
            json.put ("resource_kind", mResource.resourceKind);
            String jsonString = json.toString ( );
            Intent intent = new Intent ( );
            intent.setClass (mContext, SearchActivity.class);
            intent.putExtra ("search_content", jsonString);
            mContext.startActivity (intent);
        } catch (JSONException e) {
            e.printStackTrace ( );
        }
    }

    class containerTouchEventListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int endMargin = mSearchAreaParam.getMarginEnd ( );
            switch (event.getAction ( )) {
                case MotionEvent.ACTION_DOWN:
                    mContainerLastY = mContainerTouchY = event.getRawY ( );
                    mContainerLastX = mContainerTouchX = event.getRawX ( );
                    break;
                case MotionEvent.ACTION_MOVE:
                    float currentX = event.getRawX ( );
                    float currentY = event.getRawY ( );

                    float deltaX = mContainerLastX - currentX;
                    float offsetX = mContainerTouchX - currentX;
                    float deltaY = mContainerLastY - currentY;
                    float offsetY = mContainerTouchY - currentY;

                    mContainerLastX = currentX;

                    if (!mIsContainerOperated && !mIsSearchAreaOperated) {
                        if ((offsetX > 50 && !mSearchAreaStatus) || (offsetX < -50 && mSearchAreaStatus)) {
                            mIsSearchAreaOperated = true;
                        } else if (Math.abs (offsetY) > 200) {
                            mIsContainerOperated = true;
                        }
                    }
                    if (mIsContainerOperated) {
                        break;
                    }
                    if (mIsSearchAreaOperated) {
                        if (!mIsPlayAnimation) {
                            if (deltaX > 50) {
                                setSearchAreaStatus (true);
                            } else if (deltaX < -50) {
                                setSearchAreaStatus (false);
                            } else {
                                endMargin += (int) deltaX;
                                if (endMargin < 0) {
                                    mSearchAreaParam.setMarginEnd (endMargin);
                                    mSearchArea.setLayoutParams (mSearchAreaParam);
                                } else {
                                    mSearchAreaParam.setMarginEnd (0);
                                    mSearchArea.setLayoutParams (mSearchAreaParam);
                                }
                            }
                        }
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs (mContainerTouchX - event.getRawX ( )) < 5) {
                        v.performClick ( );
                    } else {
                        if (!mIsPlayAnimation && endMargin != 0 && endMargin != -mSearchArea.getWidth ( )) {
                            if (endMargin < -SEARCH_CLOSE_DISTANCE) {
                                setSearchAreaStatus (false);
                            } else {
                                setSearchAreaStatus (true);
                            }
                        }
                    }
                    mIsSearchAreaOperated = false;
                    mIsContainerOperated = false;
                    break;
            }
            return false;
        }
    }

    class searchAreaTouchEventListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int endMargin = mSearchAreaParam.getMarginEnd ( );
            switch (event.getAction ( )) {
                case MotionEvent.ACTION_DOWN:
                    mSearchAreaLastX = mSearchAreaTouchX = event.getRawX ( );
                    break;
                case MotionEvent.ACTION_MOVE:
                    float currentX = event.getRawX ( );
                    float deltaX = mSearchAreaLastX - currentX;
                    float offsetX = mSearchAreaTouchX - currentX;

                    mSearchAreaLastX = currentX;


                    if ((offsetX > 50 && !mSearchAreaStatus) || (offsetX < -50 && mSearchAreaStatus)) {
                        mIsSearchAreaOperated = true;
                    }

                    if (mIsSearchAreaOperated) {
                        if (!mIsPlayAnimation) {
                            if (deltaX > 50) {
                                setSearchAreaStatus (true);
                            } else if (deltaX < -50) {
                                setSearchAreaStatus (false);
                            } else {
                                endMargin += (int) deltaX;
                                if (endMargin < 0) {
                                    mSearchAreaParam.setMarginEnd (endMargin);
                                    mSearchArea.setLayoutParams (mSearchAreaParam);
                                } else {
                                    mSearchAreaParam.setMarginEnd (0);
                                    mSearchArea.setLayoutParams (mSearchAreaParam);
                                }
                            }
                        }
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs (mSearchAreaTouchX - event.getRawX ( )) < 5) {
                        v.performClick ( );
                    } else {
                        if (!mIsPlayAnimation && endMargin != 0 && endMargin != -mSearchArea.getWidth ( )) {
                            if (endMargin < -SEARCH_CLOSE_DISTANCE) {
                                setSearchAreaStatus (false);
                            } else {
                                setSearchAreaStatus (true);
                            }
                        }
                    }
                    mIsSearchAreaOperated = false;
                    mIsContainerOperated = false;
                    break;
            }
            return false;
        }
    }

    private void setSearchAreaStatus(boolean operation) {
        int endMargin = mSearchAreaParam.getMarginEnd ( );
        int deltaX = operation ? endMargin : mSearchArea.getWidth ( ) - endMargin;
        final int toX = operation ? 0 : -mSearchArea.getWidth ( );
        TranslateAnimation animation = new TranslateAnimation (0, deltaX, 0, 0);
        animation.setDuration (200);
        animation.setAnimationListener (new Animation.AnimationListener ( ) {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSearchAreaParam.setMarginEnd (toX);
                mSearchArea.setLayoutParams (mSearchAreaParam);
                mSearchArea.clearAnimation ( );
                mIsPlayAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSearchArea.clearAnimation ( );
        mSearchArea.startAnimation (animation);
        mIsPlayAnimation = true;
        mSearchAreaStatus = operation;
    }

    private void initRecommendArea() {
        mRecommendArea = findViewById (R.id.independ_picture_recommend);
        mRecommendContainer = findViewById (R.id.independ_picture_recommend_container);
        mRecommendContainer.setBackgroundColor (getColor (R.color.colorBackground));

        if (mResource.recommend.length ( ) == 0) {
            mRecommendArea.setVisibility (View.GONE);
        } else {
            NetApi.getResourceByIds (mResource.recommend.toString ( ), this, "successGetRecommend");
        }
    }

    private void initTagsArea() {
        mTagsArea = findViewById (R.id.sub_picture_tags);

        for (String tab : mResource.tags) {
            final LabelBox.Label label = new LabelBox.Label (this, tab);
            label.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(final View v) {
                    try {
                        JSONObject json = new JSONObject ( );
                        json.put ("mark", new JSONArray (new ArrayList <String> ( ) {{
                            add ((String) ((LabelBox.Label) v).mTextView.getText ( ));
                        }}));
                        json.put ("resource_kind", mResource.resourceKind);
                        String jsonString = json.toString ( );
                        Intent intent = new Intent ( );
                        intent.setClass (mContext, SearchActivity.class);
                        intent.putExtra ("search_content", jsonString);
                        mContext.startActivity (intent);
                    } catch (JSONException e) {
                        e.printStackTrace ( );
                    }
                }
            });
            label.setOnLongClickListener (new View.OnLongClickListener ( ) {
                @Override
                public boolean onLongClick(View v) {
                    mLabelPopupWindow.showAsDropDown (v);
                    mSelectedLabel = (LabelBox.Label) v;
                    return true;
                }
            });
            mTagsArea.addLabel (label);
        }
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
        if (mResource.thumbStatus == IMAGE_ALL_DOWNLOADED) {
            mLoadViewBatchSize = 10;
            mImageBrowser.addOnPageChangeListener (new SubPageChangeListener ( ));
        }
        List <View> viewLists = getViews ( );
        mAdapter = new SubPicturePagerAdapter (viewLists);
        mImageBrowser.setAdapter (mAdapter);
        mBrowserButtonLike = findViewById (R.id.sub_picture_button_like);
        mBrowserButtonSubScribe = findViewById (R.id.sub_picture_button_subscribe);
        if (mResource.resourceKind.equals ("manga")) {
            mBrowserButtonLike.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    if (!mIsLike) {
                        NetApi.addLike (mApp.mUser.uid, mResource.resourceId, mContext, "successLike");
                        Toast.makeText (mContext, "已标记为喜欢", Toast.LENGTH_SHORT).show ( );

                    } else {
                        NetApi.removeLike (mApp.mUser.uid, mResource.resourceId, mContext, "successRemoveLike");
                        Toast.makeText (mContext, "已标记为不喜欢", Toast.LENGTH_SHORT).show ( );
                    }
                }
            });
            mBrowserButtonSubScribe.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    if (!mIsSubscribe) {
                        NetApi.addSubscribe (mApp.mUser.uid, mResource.resourceId, mContext, "successSubscribe");
                        Toast.makeText (mContext, "已发送订阅请求", Toast.LENGTH_SHORT).show ( );
                    } else {
                        NetApi.removeSubscribe (mApp.mUser.uid, mResource.resourceId, mContext, "successRemoveSubscribe");
                        Toast.makeText (mContext, "已取消订阅", Toast.LENGTH_SHORT).show ( );
                    }
                }
            });
        } else {
            mBrowserButtonLike.setVisibility (View.GONE);
            mBrowserButtonSubScribe.setVisibility (View.GONE);
        }

        NetApi.isLike (mApp.mUser.uid, mResource.resourceId, mContext, "successIsLike");
        NetApi.isSubscribe (mApp.mUser.uid, mResource.resourceId, mContext, "successIsSubscribe");
    }

    private List <View> getViews() {
        List <View> viewLists = new ArrayList <> ( );
        int endIndex = Math.min (mResource.imageNames.size ( ), mLoadedViewIndex + mLoadViewBatchSize);
        for (int index = mLoadedViewIndex; index < endIndex; index++) {
            ImageView imageView = new ImageView (this);
            imageView.setTag (index);
            SubPictureClickListener clickListener = new SubPictureClickListener ( );
            imageView.setOnClickListener (clickListener);
            imageView.setScaleType (android.widget.ImageView.ScaleType.FIT_CENTER);
            imageView.setImageURL ("http://10.0.0.2:4396/gallery/" + mResource.thumbId + "/" + mResource.imageNames.get (index) + "?width=720");
            viewLists.add (imageView);
        }
        mIsLoading = false;
        mLoadedViewIndex = endIndex;
        return viewLists;
    }

    private void initAuthorInfo() {
        ImageView cover = findViewById (R.id.sub_picture_cover);
        TextView title = findViewById (R.id.sub_picture_title);
        android.widget.ImageView langFlag = findViewById (R.id.sub_picture_lang_flag);
        if (mResource.language != null) {
            if (mResource.language.equals ("english")) {
                langFlag.setImageDrawable (mContext.getDrawable (R.drawable.flag_en));
            } else if (mResource.language.equals ("chinese")) {
                langFlag.setImageDrawable (mContext.getDrawable (R.drawable.flag_cn));
            } else {
                langFlag.setImageDrawable (mContext.getDrawable (R.drawable.flag_jp));
            }
        }
        title.setText (mResource.title);
        LabelBox artists = findViewById (R.id.sub_picture_artists);
        for (final String artist : mResource.artists) {
            LabelBox.Label label = new LabelBox.Label (mContext, artist);
            label.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent ( );
                    intent.setClass (mContext, ArtistActivity.class);
                    intent.putExtra ("artist", artist);
                    mContext.startActivity (intent);
                }
            });
            artists.addLabel (label);
        }
        cover.setImageURL ("http://10.0.0.2:4396/gallery/" + mResource.thumbId + "/" + mResource.imageNames.get (0) + "?height=480&width=360");
        cover.setScaleType (android.widget.ImageView.ScaleType.FIT_XY);
    }

    public class SubPictureClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent ( );
            intent.setClass (mContext, PicturePlayerActivity.class);
            intent.putExtra ("imageNames", String.join (",", mResource.imageNames));
            intent.putExtra ("thumbId", mResource.thumbId);
            intent.putExtra ("thumbStatus", mResource.thumbStatus);
            mContext.startActivity (intent);
        }
    }

    public static class SubPicturePagerAdapter extends PagerAdapter {
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
                    pictureActivity.mIsLike = true;
                    pictureActivity.mBrowserButtonLike.setText (R.string.like);
                } else {
                    pictureActivity.mIsLike = false;
                    pictureActivity.mBrowserButtonLike.setText (R.string.unlike);
                }
            } else if (msg.what == CHANGE_SUBSCRIBE_BUTTON_STATUS) {
                if ((boolean) msg.obj) {
                    pictureActivity.mIsSubscribe = true;
                    pictureActivity.mBrowserButtonSubScribe.setText (R.string.subscribe);
                } else {
                    pictureActivity.mIsSubscribe = false;
                    pictureActivity.mBrowserButtonSubScribe.setText (R.string.unsubscribe);
                }
            } else if (msg.what == LOAD_RECOMMEND) {
                try {
                    final JSONArray recommends = (JSONArray) msg.obj;
                    for (int index = 0; index < recommends.length ( ); index++) {
                        final JSONObject recommend = recommends.getJSONObject (index);
                        PictureResource resource = new PictureResource (recommend);

                        RelativeLayout recommendInfo = new RelativeLayout (pictureActivity);
                        RelativeLayout.LayoutParams recommendInfoParam = new RelativeLayout.LayoutParams (Tool.dip2px (pictureActivity, 100), LinearLayout.LayoutParams.MATCH_PARENT);
                        recommendInfoParam.setMargins (10, 10, 10, 10);
                        recommendInfo.setLayoutParams (recommendInfoParam);

                        ImageView langFlag = new ImageView (pictureActivity);
                        RelativeLayout.LayoutParams langFlagParam = new RelativeLayout.LayoutParams (Tool.dip2px (pictureActivity, 20), Tool.dip2px (pictureActivity, 15));
                        langFlagParam.addRule (RelativeLayout.ALIGN_PARENT_BOTTOM);
                        langFlagParam.addRule (RelativeLayout.ALIGN_PARENT_RIGHT);
                        langFlagParam.bottomMargin = Tool.dip2px (pictureActivity, 5);
                        langFlagParam.rightMargin = Tool.dip2px (pictureActivity, 5);

                        langFlag.setImageDrawable (pictureActivity.mContext.getDrawable (R.drawable.flag_cn));
                        langFlag.setLayoutParams (langFlagParam);
                        if (resource.language.equals ("english")) {
                            langFlag.setImageDrawable (pictureActivity.mContext.getDrawable (R.drawable.flag_en));
                        } else if (resource.language.equals ("chinese")) {
                            langFlag.setImageDrawable (pictureActivity.mContext.getDrawable (R.drawable.flag_cn));
                        } else {
                            langFlag.setImageDrawable (pictureActivity.mContext.getDrawable (R.drawable.flag_jp));
                        }
                        RoundedImageView imageView = new RoundedImageView (pictureActivity);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        imageView.setCornerSize (Tool.dip2px (pictureActivity, 10));
                        imageView.setImageURL ("http://10.0.0.2:4396/gallery/" + resource.thumbId + "/" + resource.imageNames.get (0) + "?height=480&width=360");
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

                        recommendInfo.addView (imageView);
                        recommendInfo.addView (langFlag);
                        pictureActivity.mRecommendContainer.addView (recommendInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace ( );
                }
            }
        }
    }
}
