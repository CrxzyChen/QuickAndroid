package com.example.crxzy.centertainment.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.models.MangaResource;
import com.example.crxzy.centertainment.models.NetApi;
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

    MyHandler mHandler;
    private CardBox mContainer;
    private int mLoadedSize;
    private int mSingleLoadSize = 10;
    private boolean mIsLoading = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.independ_artist);
        mHandler = new MyHandler (this);

        onInitiation ( );
    }

    private void onInitiation() {
        final String mArtist = getIntent ( ).getStringExtra ("artist");
        mContainer = findViewById (R.id.sub_artist_container);
        mContainer.setOnTouchBottomListener (new CardBox.OnTouchBottomListener ( ) {
            @Override
            public float setDistance(float distance) {
                return Tool.dip2px (ArtistActivity.this, 200);
            }

            @Override
            public void OnTouchBottom() {
                if (!mIsLoading) {
                    NetApi.getArtistOpus (mArtist, ArtistActivity.this, "getArtistOpusSuccess", mSingleLoadSize, mLoadedSize);
                    mIsLoading = true;
                }
            }
        });
        NetApi.getArtistOpus (mArtist, this, "getArtistOpusSuccess", mSingleLoadSize, mLoadedSize);
        mIsLoading = true;
    }

    public void getArtistOpusSuccess(Network.Response response) {
        Message message = mHandler.obtainMessage ( );
        message.what = MyHandler.LOAD_ARTIST_INFO;
        message.obj = response.content;
        mHandler.sendMessage (message);
    }

    static class MyHandler extends Handler {
        static final int LOAD_ARTIST_INFO = 100;
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
