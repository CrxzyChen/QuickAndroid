package com.example.crxzy.centertainment.activities;

import android.content.Intent;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.models.PictureResource;
import com.example.crxzy.centertainment.system.QuickActivity;
import com.example.crxzy.centertainment.views.Card;
import com.example.crxzy.centertainment.views.CardBox;

import org.json.JSONObject;

import java.util.Map;

public class SearchActivity extends QuickActivity {
    CardBox mContainer;
    private String mSearchContent;

    public void onInitiation() {
        setContentView (R.layout.independ_search);
        Intent intent = getIntent ( );
        mSearchContent = intent.getStringExtra ("search_content");
        initMainWindow ( );
    }

    private void initMainWindow() {
        mContainer = findViewById (R.id.sub_search_container);
        mContainer.setCardBoxAdapt (new CardBox.CardBoxAdapt (mContainer) {
            @Override
            protected void initViewTypeToView(Map <Integer, Class <?>> mViewTypeToView) {
                mViewTypeToView.put (0, Card.class);
            }

            @Override
            protected void requestData(int loadedSize, int singleLoadSize, LoadDataCallback callback) {
                NetApi.search (mSearchContent, singleLoadSize, loadedSize, callback);
            }

            @Override
            protected void loadItem(CardBox cardBox, JSONObject obj) {
                cardBox.addResource (new PictureResource (obj));
            }
        });
    }

}
