package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.crxzy.centertainment.PictureActivity;
import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Tool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ItemsBoxView extends LinearLayout {
    public Context mContext;
    private boolean mBlackSpace = false;

    public enum LayoutStyle {
        block, linearBlock;
    }

    private abstract static class ItemBase extends FrameLayout {
        public FrameLayout.LayoutParams mItemLayoutParams = new FrameLayout.LayoutParams (0, 0);
        public Context mContext;

        ItemBase(Context context) {
            super (context);
            mContext = context;
            initLayoutParams ( );
        }

        public void initLayoutParams() {
            setBackgroundColor (mContext.getColor (R.color.colorPrimary));
            setBackground (mContext.getDrawable (R.drawable.roundrect_image));//设置圆角
            setElevation (Tool.dip2px (mContext, 2));//设置阴影
            mItemLayoutParams.height = Tool.dip2px (mContext, 300);
            mItemLayoutParams.setMargins (Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5));
//            setOrientation (VERTICAL);
            setLayoutParams (mItemLayoutParams);
        }

        abstract public LayoutStyle getLayoutStyle();

        public void setWidth(int width) {
            mItemLayoutParams.width = width;
            setLayoutParams (mItemLayoutParams);
        }

        public void setHeight(int height) {
            mItemLayoutParams.height = height;
            setLayoutParams (mItemLayoutParams);
        }
    }

    public static class BlockItem extends ItemBase {
        public RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams (0, 0);

        public BlockItem(Context context) {
            super (context);
            mContext = context;
            initLayoutParams ( );
        }

        @Override
        public LayoutStyle getLayoutStyle() {
            return LayoutStyle.block;
        }
    }

    public static class LinearBlockItem extends ItemBase {
        public LinearBlockItem(Context context) {
            super (context);
        }

        public void initLayoutParams() {
            setBackgroundColor (mContext.getColor (R.color.colorPrimary));
            mItemLayoutParams.height = 400;
            mItemLayoutParams.setMargins (Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5));
            setLayoutParams (mItemLayoutParams);
        }

        @Override
        public LayoutStyle getLayoutStyle() {
            return LayoutStyle.linearBlock;
        }
    }

    public static class NormalItem extends ItemBase {
        public RoundedImageView image;
        public TextView title;
        public TextView clickTime;
        public TextView pageCount;
        public TextView sourceTag;

        public NormalItem(Context context) {
            super (context);
            LinearLayout mainLinerLayout = new LinearLayout (context);
            LinearLayout.LayoutParams mainLinerLayoutParams = new LinearLayout.LayoutParams (FrameLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            mainLinerLayout.setLayoutParams (mainLinerLayoutParams);
            mainLinerLayout.setBackground (context.getDrawable (R.drawable.items_box));
            mainLinerLayout.setOrientation (LinearLayout.VERTICAL);

            FrameLayout coverLayout = new FrameLayout (context);
            FrameLayout.LayoutParams coverLayoutParams = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
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
            leftImageInfoParams.weight = 2;
            LinearLayout rightImageInfo = new LinearLayout (context);
            LinearLayout.LayoutParams rightImageInfoParams = new LinearLayout.LayoutParams (0, LayoutParams.MATCH_PARENT);
            rightImageInfoParams.weight = 1;

            //LeftImageInfo
            TextView clicked = new TextView (context);
            clicked.setText (context.getString (R.string.hot));
            clicked.setPadding (10, 0, 10, 0);
            clicked.setTextColor (context.getColor (R.color.white));
            clickTime = new TextView (context);
            clickTime.setTextColor (context.getColor (R.color.white));
            leftImageInfo.setGravity (Gravity.START);
            leftImageInfo.addView (clicked);
            leftImageInfo.addView (clickTime);
            leftImageInfo.setLayoutParams (leftImageInfoParams);

            //RightImageInfo
            TextView counter = new TextView (context);
            counter.setText (context.getText (R.string.counter));
            counter.setTextColor (context.getColor (R.color.white));
            pageCount = new TextView (context);
            pageCount.setTextColor (context.getColor (R.color.white));
            pageCount.setPadding (10, 0, 10, 0);
            rightImageInfo.setGravity (Gravity.END);
            rightImageInfo.addView (counter);
            rightImageInfo.addView (pageCount);
            rightImageInfo.setLayoutParams (rightImageInfoParams);

            //Merge left and right to ImageInfo
            RelativeLayout.LayoutParams imageInfoLayoutParams = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            imageInfoLayoutParams.addRule (RelativeLayout.ALIGN_PARENT_BOTTOM);
            imageInfo.addView (leftImageInfo);
            imageInfo.addView (rightImageInfo);
            imageInfo.setBackgroundColor (context.getColor (R.color.colorText));
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
            sourceTag.setTextColor (context.getColor (R.color.colorText));
            sourceTag.setTextSize (TypedValue.COMPLEX_UNIT_SP, 12);
            sourceTag.setPadding (Tool.dip2px (context, 10), Tool.dip2px (context, 6), Tool.dip2px (context, 10), Tool.dip2px (context, 6));
            tagsArea.addView (sourceTag);

            mainLinerLayout.addView (imageInfoBox);
            mainLinerLayout.addView (title);
            mainLinerLayout.addView (tagsArea);
            addView (mainLinerLayout);
            addView (coverLayout);
        }

        @Override
        public LayoutStyle getLayoutStyle() {
            return LayoutStyle.block;
        }
    }

    public ItemsBoxView(Context context) {
        super (context);
        mContext = context;
        setBackgroundColor (context.getColor (R.color.colorBackground));
    }

    public ItemsBoxView(Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
        mContext = context;
        setBackgroundColor (context.getColor (R.color.colorBackground));
    }

    public ItemsBoxView(Context context, AttributeSet attrs) {
        super (context, attrs);
        mContext = context;
        setBackgroundColor (context.getColor (R.color.colorBackground));
    }

    public void addItem(View item) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getLayoutStyle = item.getClass ( ).getDeclaredMethod ("getLayoutStyle");
        Method setWidth = item.getClass ( ).getDeclaredMethod ("setWidth", int.class);

        Object layoutStyle = getLayoutStyle.invoke (item);

        int width = 0;

        switch ((LayoutStyle) layoutStyle) {
            case block:
                width = Tool.getScreenWidth (mContext) / 2 - Tool.dip2px (mContext, 15);
                setWidth.invoke (item, width);
                if (mBlackSpace) {
                    int childCount = getChildCount ( );
                    ((ViewGroup) getChildAt (childCount - 1)).addView (item);
                    mBlackSpace = false;
                } else {
                    LinearLayout linearLayout = new LinearLayout (mContext);
                    linearLayout.addView (item);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    addView (linearLayout, layoutParams);
                    mBlackSpace = true;
                }
                break;
            case linearBlock:
                width = Tool.getScreenWidth (mContext) - Tool.dip2px (mContext, 20);
                setWidth.invoke (item, width);
                addView (item);
                mBlackSpace = false;
                break;
        }
        item.setOnClickListener (new OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( );
                intent.setClass (mContext, PictureActivity.class);
                mContext.startActivity (intent);
            }
        });
    }
}