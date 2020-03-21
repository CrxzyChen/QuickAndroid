package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.crxzy.centertainment.activities.PictureActivity;
import com.example.crxzy.centertainment.controllers.main.Picture;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.models.PictureResource;
import com.example.crxzy.centertainment.system.MainApplication;

public class SyasinnSelfCard extends Card {
    public SyasinnSelfCard(Context context) {
        super (context);
    }

    @Override
    public void loadResource(CardBox.ResourceManager.ResourceBase resource) {
        PictureResource mResource = (PictureResource) resource;
        this.pageCount.setText ((CharSequence) Integer.toString (mResource.PageCount));
        this.clickTime.setText ((CharSequence) Integer.toString (mResource.clickedTimes));
        this.title.setText (mResource.Title);
        switch (mResource.ThumbStatus) {
            case 0:
                statusTag.setText ("○");
                break;
            case 1:
                statusTag.setText ("◔");
                break;
            case 2:
                statusTag.setText ("◑");
                break;
            case 3:
                statusTag.setText ("◕");
                break;
            case 4:
                statusTag.setText ("●");
                break;
        }
        this.image.setImageURL ("http://10.0.0.2:4396/gallery/" + mResource.ThumbId + "/" + mResource.ImageNames.get (0) + "?height=480&width=360");
        CardOnClickListener cardOnClickListener = new CardOnClickListener (mResource);
        setOnClickListener (cardOnClickListener);
    }


    class CardOnClickListener implements View.OnClickListener {
        PictureResource mResource;

        CardOnClickListener(PictureResource resource) {
            mResource = resource;
        }

        @Override
        public void onClick(View v) {
            int uid = ((MainApplication) (((AppCompatActivity) getContext ( )).getApplication ( ))).mUser.uid;
            String resource_id = mResource.ResourceId;
            NetApi.addHistory (uid, resource_id);
            NetApi.upClickedCount (resource_id, "syasinn");
            int clickedTimes = Integer.parseInt ((String) clickTime.getText ( )) + 1;
            clickTime.setText ((String) Integer.toString (clickedTimes));
            Intent intent = new Intent ( );
            intent.setClass (getContext ( ), PictureActivity.class);
            intent.putExtra ("info", mResource.Resource.toString ( ));
            getContext ( ).startActivity (intent);
        }
    }
}
