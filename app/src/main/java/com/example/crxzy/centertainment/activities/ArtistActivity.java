package com.example.crxzy.centertainment.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.models.MangaResource;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.system.MainApplication;
import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.tools.Tool;
import com.example.crxzy.centertainment.views.CardBox;
import com.example.crxzy.centertainment.views.MangaSelfCard;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ArtistActivity extends AppCompatActivity {

    public static final int ARTIST_INFO_FOCUS_STATUS_FALSE = 0;
    public static final int ARTIST_INFO_FOCUS_STATUS_TRUE = 1;
    MyHandler mHandler;
    private CardBox mContainer;
    private int mLoadedSize;
    private int mSingleLoadSize = 10;
    private boolean mIsLoading = false;
    private LinearLayout mArtistInfo;
    private String mArtistName;
    private MainApplication mApp;
    private boolean mIsFocused = false;
    private Button mArtistInfoFocusButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.independ_artist);
        onInitiation ( );
    }

    private void onInitiation() {
        mHandler = new MyHandler (this);
        mArtistName = getIntent ( ).getStringExtra ("artist");
        mApp = (MainApplication) getApplication ( );
        mIsLoading = true;
        initArtistInfo ( );
        initArtistOpusArea ( );
    }

    private void initArtistOpusArea() {
        mContainer = findViewById (R.id.sub_artist_container);
        mContainer.setOnTouchBottomListener (new CardBox.OnTouchBottomListener ( ) {
            @Override
            public float setDistance(float distance) {
                return Tool.dip2px (ArtistActivity.this, 200);
            }

            @Override
            public void onTouchBottom() {
                if (!mIsLoading) {
                    mContainer.setLoadingCard (View.VISIBLE);
                    NetApi.getArtistOpus (mArtistName, ArtistActivity.this, "getArtistOpusSuccess", mSingleLoadSize, mLoadedSize);
                    mIsLoading = true;
                }
            }
        });
        NetApi.getArtistOpus (mArtistName, this, "getArtistOpusSuccess", mSingleLoadSize, mLoadedSize);
    }

    private void initArtistInfo() {
        NetApi.isFocusArtist (mApp.mUser.uid, mArtistName, new Network.Callback ( ) {
            @Override
            public void success(Network.Response response) {
                if (response.content.equals ("true")) {
                    mIsFocused = true;
                    Message message = mHandler.obtainMessage ( );
                    message.what = MyHandler.CHANGE_ARTIST_INFO_FOCUS_STATUS;
                    message.arg1 = ARTIST_INFO_FOCUS_STATUS_TRUE;
                    mHandler.sendMessage (message);
                }
            }

            @Override
            public void error(Network.Response response) {

            }
        });
        mArtistInfo = findViewById (R.id.sub_artist_info);
        TextView artistNameTextView = findViewById (R.id.sub_artist_info_name);
        mArtistInfoFocusButton = findViewById (R.id.sub_artist_info_focus_button);
        mArtistInfoFocusButton.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                if (!mIsFocused) {
                    NetApi.addFocusArtist (mApp.mUser.uid, mArtistName, new Network.Callback ( ) {
                        @Override
                        public void success(Network.Response response) {
                            if (response.content.equals ("true")) {
                                Message message = mHandler.obtainMessage ( );
                                message.what = MyHandler.CHANGE_ARTIST_INFO_FOCUS_STATUS;
                                message.arg1 = ARTIST_INFO_FOCUS_STATUS_TRUE;
                                mHandler.sendMessage (message);
                                mIsFocused = true;
                            }

                        }

                        @Override
                        public void error(Network.Response response) {

                        }
                    });
                } else {
                    NetApi.removeFocusArtist (mApp.mUser.uid, mArtistName, new Network.Callback ( ) {
                        @Override
                        public void success(Network.Response response) {
                            if (response.content.equals ("true")) {
                                Message message = mHandler.obtainMessage ( );
                                message.what = MyHandler.CHANGE_ARTIST_INFO_FOCUS_STATUS;
                                message.arg1 = ARTIST_INFO_FOCUS_STATUS_FALSE;
                                mHandler.sendMessage (message);
                                mIsFocused = false;
                            }
                        }

                        @Override
                        public void error(Network.Response response) {

                        }
                    });
                }
            }
        });
        artistNameTextView.setText (mArtistName);
    }

    public void getArtistOpusSuccess(Network.Response response) {
        Message message = mHandler.obtainMessage ( );
        message.what = MyHandler.LOAD_ARTIST_INFO;
        message.obj = response.content;
        mHandler.sendMessage (message);
    }

    static class MyHandler extends Handler {
        static final int LOAD_ARTIST_INFO = 100;
        static final int CHANGE_ARTIST_INFO_FOCUS_STATUS = 101;
        private WeakReference <ArtistActivity> outClass;

        MyHandler(ArtistActivity activity) {
            outClass = new WeakReference <> (activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ArtistActivity activity = outClass.get ( );
            switch (msg.what) {
                case LOAD_ARTIST_INFO:
                    JSONArray json = (JSONArray) msg.obj;
                    List <MangaResource> opus = new ArrayList <> ( );
                    for (int index = 0; index < json.length ( ); index++) {
                        try {
                            opus.add (new MangaResource (json.getJSONObject (index)));
                        } catch (JSONException e) {
                            e.printStackTrace ( );
                        }
                    }
                    activity.addItem (opus);
                    break;
                case CHANGE_ARTIST_INFO_FOCUS_STATUS:
                    if (msg.arg1 == ARTIST_INFO_FOCUS_STATUS_FALSE) {
                        activity.mArtistInfoFocusButton.setText (activity.getText (R.string.string_cn_focus));
                    } else if (msg.arg1 == ARTIST_INFO_FOCUS_STATUS_TRUE) {
                        activity.mArtistInfoFocusButton.setText (activity.getText (R.string.string_cn_unfocus));
                    }
                    break;
            }
        }
    }

    private void addItem(List <MangaResource> opus) {
        for (MangaResource opu : opus) {
            mContainer.addCard (new MangaSelfCard (this, opu));
        }
        mLoadedSize += opus.size ( );
        mIsLoading = false;
    }
}
