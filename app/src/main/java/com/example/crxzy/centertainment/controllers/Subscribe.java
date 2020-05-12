package com.example.crxzy.centertainment.controllers;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.models.PictureResource;
import com.example.crxzy.centertainment.system.FirstPageBase;
import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.views.Card;
import com.example.crxzy.centertainment.views.CardBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Subscribe extends FirstPageBase {
    private CardBox mViewContainer;

    @Override
    public void onHeaderInitialize(View header) {
        ((LinearLayout) header).getChildAt (0).setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                mActivity.mRootPage.mMainLayout.openDrawer (Gravity.START);
            }
        });
        Switch orderSwitch = header.findViewById (R.id.subscribe_order_switch);
        try {
            JSONObject config = (JSONObject) mApp.mUser.getConfig ("subscribe_common");
            orderSwitch.setChecked (config.getBoolean ("is_ordered"));
        } catch (JSONException e) {
            e.printStackTrace ( );
        }
        orderSwitch.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener ( ) {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText (mActivity, "checked", Toast.LENGTH_LONG).show ( );
                    try {
                        JSONObject config = new JSONObject ( );
                        config.put ("is_ordered", true);
                        mApp.mUser.putConfig ("subscribe_common", config);
                    } catch (JSONException e) {
                        e.printStackTrace ( );
                    }
                    mViewContainer.refresh ( );
                } else {
                    Toast.makeText (mActivity, "unchecked", Toast.LENGTH_LONG).show ( );
                    try {
                        JSONObject config = new JSONObject ( );
                        config.put ("is_ordered", false);
                        mApp.mUser.putConfig ("subscribe_common", config);
                    } catch (JSONException e) {
                        e.printStackTrace ( );
                    }
                    mViewContainer.refresh ( );
                }
            }
        });
    }

    public Subscribe(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void onInitiation() {
        mViewContainer = mView.findViewById (R.id.subscribe_container);
        Button mBackButton = mView.findViewById (R.id.subscribe_back);
        mBackButton.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                mViewContainer.smoothScrollToPosition (0);
            }
        });
        mBackButton.setOnLongClickListener (new View.OnLongClickListener ( ) {
            @Override
            public boolean onLongClick(View v) {
                mPageModel.mParent.getController ( ).selectPage (mPageModel.mParent.getIndex ("main"));
                return false;
            }
        });
        mViewContainer.setCardBoxAdapt (new CardBox.CardBoxAdapt (mViewContainer) {
            @Override
            protected void initViewTypeToView(Map <Integer, Class <?>> mViewTypeToView) {
                mViewTypeToView.put (0, Card.class);
            }

            @Override
            protected void requestData(int loadedSize, int singleLoadSize, LoadDataCallback callback) {
                NetApi.getSubscribe (mApp.mUser.uid, singleLoadSize, loadedSize, callback);
            }

            @Override
            protected void loadItem(CardBox cardBox, JSONObject obj) {
                cardBox.addResource (new PictureResource (obj));
            }
        });
    }

}
