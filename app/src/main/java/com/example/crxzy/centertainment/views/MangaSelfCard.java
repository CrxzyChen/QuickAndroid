package com.example.crxzy.centertainment.views;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.activities.PictureActivity;
import com.example.crxzy.centertainment.controllers.Subscribe;
import com.example.crxzy.centertainment.models.MangaResource;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.system.MainApplication;
import com.example.crxzy.centertainment.tools.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MangaSelfCard extends CardBox.BlockItem {
    public RoundedImageView image;
    public TextView title;
    public TextView clickTime;
    public TextView pageCount;
    public TextView sourceTag;
    public TextView statusTag;
    public ImageView langFlag;

    public MangaSelfCard(Context context) {
        super (context);
        drawView (context);
    }

    public MangaSelfCard(Context context, AttributeSet attributeSet) {
        super (context);
        drawView (context);
    }

    public MangaSelfCard(Context context, MangaResource resource) {
        super (context);
        drawView (context);

        title.setText (resource.Title);

        if (resource.Language.equals ("english")) {
            langFlag.setImageDrawable (getContext ( ).getDrawable (R.drawable.flag_en));
        } else if (resource.Language.equals ("chinese")) {
            langFlag.setImageDrawable (getContext ( ).getDrawable (R.drawable.flag_cn));
        } else {
            langFlag.setImageDrawable (getContext ( ).getDrawable (R.drawable.flag_jp));
        }
        switch (resource.ThumbStatus) {
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
        clickTime.setText ((String) Integer.toString (resource.clickedTimes));
        final String tagsString = resource.Source + "." + resource.Language;
        sourceTag.setText (tagsString);
        pageCount.setText ((String) Integer.toString (resource.PageCount));
        image.setImageURL ("http://10.0.0.2:4396/gallery/" + resource.ThumbId + "/" + resource.ImageNames.get (0) + "?height=480&width=360");
        CardOnClickListener cardOnClickListener = new CardOnClickListener (resource);
        setOnClickListener (cardOnClickListener);
    }

    class CardOnClickListener implements View.OnClickListener {
        MangaResource mResource;

        CardOnClickListener(MangaResource resource) {
            mResource = resource;
        }

        @Override
        public void onClick(View v) {
            int uid = ((MainApplication) (((AppCompatActivity) MangaSelfCard.this.getContext ( )).getApplication ( ))).mUser.mUid;
            String resource_id = mResource.ResourceId;
            NetApi.addHistory (uid, resource_id);
            NetApi.upClickedCount (resource_id);
            int clickedTimes = Integer.parseInt ((String) MangaSelfCard.this.clickTime.getText ( )) + 1;
            MangaSelfCard.this.clickTime.setText ((String) Integer.toString (clickedTimes));
            Intent intent = new Intent ( );
            intent.setClass (MangaSelfCard.this.getContext ( ), PictureActivity.class);
            intent.putExtra ("info", mResource.Resource.toString ());
            getContext ().startActivity (intent);
        }
    }

    private void drawView(Context context) {
        LinearLayout mainLinerLayout = new LinearLayout (context);
        LinearLayout.LayoutParams mainLinerLayoutParams = new LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mainLinerLayout.setLayoutParams (mainLinerLayoutParams);
        mainLinerLayout.setBackground (context.getDrawable (R.drawable.items_box));
        mainLinerLayout.setOrientation (LinearLayout.VERTICAL);
        //cover
        FrameLayout coverLayout = new FrameLayout (context);
        LayoutParams coverLayoutParams = new LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        coverLayout.setLayoutParams (coverLayoutParams);
        coverLayout.setBackground (context.getDrawable (R.drawable.items_box));

        RelativeLayout imageInfoBox = new RelativeLayout (context);
        RelativeLayout.LayoutParams imageInfoBoxLayoutParams = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, Tool.dip2px (context, 225));
        imageInfoBox.setLayoutParams (imageInfoBoxLayoutParams);
        //Image
        image = new RoundedImageView (context);
        image.setCornerSize (Tool.dip2px (context, 5));
        image.setImageResource (R.drawable.ic_launcher_foreground);
        image.setScaleType (ImageView.ScaleType.FIT_XY);
        image.setBackgroundColor (context.getColor (R.color.class_one_title));
        RelativeLayout.LayoutParams imageAreaLayoutParams = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, Tool.dip2px (context, 225));
        image.setLayoutParams (imageAreaLayoutParams);

        //ImageInfo
        LinearLayout imageInfo = new LinearLayout (context);
        imageInfo.setOrientation (LinearLayout.HORIZONTAL);

        LinearLayout leftImageInfo = new LinearLayout (context);
        LinearLayout.LayoutParams leftImageInfoParams = new LinearLayout.LayoutParams (0, LayoutParams.MATCH_PARENT);
        leftImageInfoParams.weight = 1;
        LinearLayout rightImageInfo = new LinearLayout (context);
        LinearLayout.LayoutParams rightImageInfoParams = new LinearLayout.LayoutParams (0, LayoutParams.MATCH_PARENT);
        rightImageInfoParams.weight = 2;

        //LeftImageInfo
        //language_flag
        langFlag = new ImageView (mContext);
        LinearLayout.LayoutParams langFlagParam = new LinearLayout.LayoutParams (Tool.dip2px (mContext, 20), Tool.dip2px (mContext, 15));
        langFlagParam.topMargin = Tool.dip2px (mContext, 2);
        langFlagParam.leftMargin = Tool.dip2px (mContext, 10);
        langFlag.setLayoutParams (langFlagParam);
        leftImageInfo.addView (langFlag);
        leftImageInfo.setGravity (Gravity.START);
        leftImageInfo.setLayoutParams (leftImageInfoParams);

        //RightImageInfo
        TextView clicked = new TextView (context);
        clicked.setText (context.getString (R.string.hot));
        clicked.setPadding (Tool.dip2px (mContext, 10), 0, 0, 0);
        clicked.setTextColor (context.getColor (R.color.white));
        clickTime = new TextView (context);
        clickTime.setTextColor (context.getColor (R.color.white));

        TextView counter = new TextView (context);
        counter.setText (context.getText (R.string.counter));
        counter.setTextColor (context.getColor (R.color.white));
        counter.setPadding (0, 0, Tool.dip2px (mContext, 10), 0);
        pageCount = new TextView (context);
        pageCount.setTextColor (context.getColor (R.color.white));
        pageCount.setPadding (Tool.dip2px (mContext, 10), 0, 0, 0);
        rightImageInfo.setGravity (Gravity.END);
        rightImageInfo.addView (clicked);
        rightImageInfo.addView (clickTime);
        rightImageInfo.addView (pageCount);
        rightImageInfo.addView (counter);
        rightImageInfo.setLayoutParams (rightImageInfoParams);

        //Merge left and right to ImageInfo
        RelativeLayout.LayoutParams imageInfoLayoutParams = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageInfoLayoutParams.addRule (RelativeLayout.ALIGN_PARENT_BOTTOM);
        imageInfo.addView (leftImageInfo);
        imageInfo.addView (rightImageInfo);
        imageInfo.setBackgroundColor (context.getColor (R.color.translucent_gray));
        imageInfo.setLayoutParams (imageInfoLayoutParams);
        imageInfoBox.addView (image);
        imageInfoBox.addView (imageInfo);

        //Title
        LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, Tool.dip2px (context, 45));
        title = new TextView (context);
        title.setTextColor (context.getColor (R.color.black));
        title.setBackgroundColor (context.getColor (R.color.white));
        title.setPadding (Tool.dip2px (context, 10), Tool.dip2px (context, 10), Tool.dip2px (context, 10), Tool.dip2px (context, 0));
        title.setTextSize (TypedValue.COMPLEX_UNIT_SP, 12);
        title.setEllipsize (TextUtils.TruncateAt.END);
        title.setMaxLines (2);
        title.setLayoutParams (textViewLayoutParams);

        LinearLayout tagsArea = new LinearLayout (context);
        LinearLayout.LayoutParams tagsAreaParams = new LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, Tool.dip2px (context, 30));
        tagsArea.setBackgroundColor (context.getColor (R.color.white));
        tagsArea.setBackground (mContext.getDrawable (R.drawable.items_box_tags_area));
        tagsArea.setLayoutParams (tagsAreaParams);

        sourceTag = new TextView (context);
        LinearLayout.LayoutParams sourceTagParam = new LinearLayout.LayoutParams (0, LinearLayout.LayoutParams.MATCH_PARENT, 3);
        sourceTag.setTextColor (context.getColor (R.color.colorText));
        sourceTag.setTextSize (TypedValue.COMPLEX_UNIT_SP, 12);
        sourceTag.setPadding (Tool.dip2px (context, 10), Tool.dip2px (context, 6), Tool.dip2px (context, 10), Tool.dip2px (context, 6));
        sourceTag.setLayoutParams (sourceTagParam);
        tagsArea.addView (sourceTag);

        statusTag = new TextView (context);
        LinearLayout.LayoutParams statusTagParam = new LinearLayout.LayoutParams (0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        statusTag.setTextAlignment (TEXT_ALIGNMENT_VIEW_END);
        statusTag.setTextColor (context.getColor (R.color.colorText));
        statusTag.setTextSize (TypedValue.COMPLEX_UNIT_SP, 12);
        statusTag.setPadding (Tool.dip2px (context, 10), Tool.dip2px (context, 6), Tool.dip2px (context, 10), Tool.dip2px (context, 6));
        statusTag.setLayoutParams (statusTagParam);
        tagsArea.addView (statusTag);

        mainLinerLayout.addView (imageInfoBox);
        mainLinerLayout.addView (title);
        mainLinerLayout.addView (tagsArea);
        addView (mainLinerLayout);
        addView (coverLayout);
    }


    @Override
    public CardBox.LayoutStyle setLayoutStyle() {
        return CardBox.LayoutStyle.block;
    }
}
