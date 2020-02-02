package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

    private abstract static class ItemBase extends LinearLayout {
        public LinearLayout.LayoutParams mItemLayoutParams = new LinearLayout.LayoutParams (0, 0);
        public Context mContext;

        ItemBase(Context context) {
            super (context);
            this.mContext = context;
            initLayoutParams ( );
        }

        public void initLayoutParams() {
            this.setBackgroundColor (this.mContext.getColor (R.color.colorPrimary));
            this.setBackground (this.mContext.getDrawable (R.drawable.roundrect_image));//设置圆角
            this.setElevation (Tool.dip2px (mContext, 2));//设置阴影
            this.mItemLayoutParams.height = Tool.dip2px (mContext, 300);
            this.mItemLayoutParams.setMargins (Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5));
            this.setOrientation (VERTICAL);
            this.setLayoutParams (this.mItemLayoutParams);
        }

        abstract public LayoutStyle getLayoutStyle();

        public void setWidth(int width) {
            this.mItemLayoutParams.width = width;
            this.setLayoutParams (this.mItemLayoutParams);
        }

        public void setHeight(int height) {
            this.mItemLayoutParams.height = height;
            this.setLayoutParams (this.mItemLayoutParams);
        }
    }

    public static class BlockItem extends ItemBase {
        public RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams (0, 0);

        public BlockItem(Context context) {
            super (context);
            this.mContext = context;
            initLayoutParams ( );
        }

        @Override
        public LayoutStyle getLayoutStyle() {
            return LayoutStyle.block;
        }
    }

    public static class LinearBlockItem extends ItemBase {
        public Context mContext;
        public RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams (0, 0);

        public LinearBlockItem(Context context) {
            super (context);
            this.mContext = context;
            initLayoutParams ( );
        }

        public void initLayoutParams() {
            this.setBackgroundColor (this.mContext.getColor (R.color.colorPrimary));
            this.layoutParams.height = 400;
            this.layoutParams.setMargins (Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5));
            this.setLayoutParams (this.layoutParams);
        }

        @Override
        public LayoutStyle getLayoutStyle() {
            return LayoutStyle.linearBlock;
        }
    }

    public static class NormalItem extends ItemBase {
        public RoundedImageView image;
        public LinearLayout imageInfo;
        public TextView title;
        public TextView clickTime;
        public TextView pageCount;

        public NormalItem(Context context) {
            super (context);

            RelativeLayout imageInfoBox = new RelativeLayout (context);
            RelativeLayout.LayoutParams imageInfoBoxLayoutParams = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, Tool.dip2px (context, 225));
            imageInfoBox.setLayoutParams (imageInfoBoxLayoutParams);
            //Image
            this.image = new RoundedImageView (context);
            this.image.setCornerSize (Tool.dip2px (context, 5));
            this.image.setImageResource (R.drawable.test);
            this.image.setScaleType (ImageView.ScaleType.FIT_XY);
            this.image.setBackgroundColor (context.getColor (R.color.class_one_title));
            RelativeLayout.LayoutParams imageAreaLayoutParams = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, Tool.dip2px (context, 225));
            this.image.setLayoutParams (imageAreaLayoutParams);

            //ImageInfo
            this.imageInfo = new LinearLayout (context);
            this.imageInfo.setOrientation (LinearLayout.HORIZONTAL);

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
            this.imageInfo.addView (leftImageInfo);
            this.imageInfo.addView (rightImageInfo);
            this.imageInfo.setBackgroundColor (context.getColor (R.color.colorText));
            this.imageInfo.setLayoutParams (imageInfoLayoutParams);
            imageInfoBox.addView (image);
            imageInfoBox.addView (imageInfo);

            //Title
            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, Tool.dip2px (context, 40));
            this.title = new TextView (context);
            this.title.setTextColor (R.color.black);
            this.title.setBackgroundColor (context.getColor (R.color.white));
            this.title.setPadding (Tool.dip2px (context, 3), Tool.dip2px (context, 3), Tool.dip2px (context, 3), Tool.dip2px (context, 3));
            this.title.setTextSize (TypedValue.COMPLEX_UNIT_SP, 12);
            this.title.setEllipsize (TextUtils.TruncateAt.END);
            this.title.setMaxLines (2);
            this.title.setLayoutParams (textViewLayoutParams);

            this.addView (imageInfoBox);
            this.addView (this.title);
        }

        @Override
        public LayoutStyle getLayoutStyle() {
            return LayoutStyle.block;
        }
    }

    public ItemsBoxView(Context context) {
        super (context);
        this.mContext = context;
        this.setBackgroundColor (context.getColor (R.color.colorBackground));
    }

    public ItemsBoxView(Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
        this.mContext = context;
        this.setBackgroundColor (context.getColor (R.color.colorBackground));
    }

    public ItemsBoxView(Context context, AttributeSet attrs) {
        super (context, attrs);
        this.mContext = context;
        this.setBackgroundColor (context.getColor (R.color.colorBackground));
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
                    int childCount = this.getChildCount ( );
                    ((ViewGroup) this.getChildAt (childCount - 1)).addView (item);
                    mBlackSpace = false;
                } else {
                    LinearLayout linearLayout = new LinearLayout (mContext);
                    linearLayout.addView (item);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    this.addView (linearLayout, layoutParams);
                    mBlackSpace = true;
                }
                break;
            case linearBlock:
                width = Tool.getScreenWidth (mContext) - Tool.dip2px (mContext, 20);
                setWidth.invoke (item, width);
                this.addView (item);
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