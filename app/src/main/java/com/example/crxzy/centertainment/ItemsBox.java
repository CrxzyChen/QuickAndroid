package com.example.crxzy.centertainment;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ItemsBox extends LinearLayout {
    public Context mContext;
    private boolean mBlackSpace = false;

    public enum LayoutStyle {
        block, linearblock;
    }

    private abstract static class ItemBase extends LinearLayout{
        public LinearLayout.LayoutParams mItemLayoutParams = new LinearLayout.LayoutParams (0, 0);
        public Context mContext;

        ItemBase(Context context){
            super(context);
            this.mContext = context;
            initLayoutParams ( );
        }

        public void initLayoutParams() {
            this.setBackgroundColor (this.mContext.getColor (R.color.colorPrimary));
            this.setBackground (this.mContext.getDrawable (R.drawable.roundrect_image));//设置圆角
            this.setElevation (Tool.dip2px (mContext,2));//设置阴影
            this.mItemLayoutParams.height = Tool.dip2px (mContext,175);
            this.mItemLayoutParams.setMargins (Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5), Tool.dip2px (mContext, 5));
            this.setOrientation (VERTICAL);
            this.setLayoutParams (this.mItemLayoutParams);
        }

        abstract public LayoutStyle getLayoutStyle() ;

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
            return LayoutStyle.linearblock;
        }
    }

    public static class NormalItem extends ItemBase{
        public NormalItem(Context context) {
            super (context);

            RelativeLayout imageInfoBox = new RelativeLayout (context);
            RelativeLayout.LayoutParams imageInfoBoxLayoutParams = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT,Tool.dip2px (context,100));
            imageInfoBox.setLayoutParams (imageInfoBoxLayoutParams);

            Tool.RoundedImageView image = new Tool.RoundedImageView (context);
            image.setCornerSize (Tool.dip2px (context,5));
            image.setImageResource (R.drawable.test);
            image.setScaleType (ImageView.ScaleType.FIT_XY);
            image.setBackgroundColor (context.getColor (R.color.class_one_title));
            RelativeLayout.LayoutParams imageAreaLayoutParams = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT,Tool.dip2px (context,100));
            image.setLayoutParams (imageAreaLayoutParams);

            TextView imageInfo = new TextView (context);
            imageInfo.setBackgroundColor (context.getColor (R.color.colorText));
            RelativeLayout.LayoutParams imageInfoLayoutParams = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            imageInfoLayoutParams.addRule (RelativeLayout.ALIGN_PARENT_BOTTOM);
            imageInfo.setLayoutParams (imageInfoLayoutParams);

            ((ViewGroup)imageInfoBox).addView (image);
            ((ViewGroup)imageInfoBox).addView (imageInfo);
            ((ViewGroup)this).addView (imageInfoBox);

            TextView textView = new TextView (context);
            textView.setBackgroundColor (context.getColor (R.color.colorBackground));

            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, Tool.dip2px (context,40));

            textView.setLayoutParams (textViewLayoutParams);


            ((ViewGroup)this).addView (textView);
        }
        @Override
        public LayoutStyle getLayoutStyle() {
            return LayoutStyle.block;
        }
    }
    public ItemsBox(Context context) {
        super (context);
        this.mContext = context;
    }

    public ItemsBox(Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
        this.mContext = context;
    }

    public ItemsBox(Context context, AttributeSet attrs) {
        super (context, attrs);
        this.mContext = context;
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
                    int childCount = ((ViewGroup) this).getChildCount ( );
                    ((ViewGroup) ((ViewGroup) this).getChildAt (childCount-1)).addView (item);
                    mBlackSpace = false;
                } else {
                    LinearLayout linearLayout = new LinearLayout (mContext);
                    linearLayout.addView (item);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
                    ((ViewGroup) this).addView (linearLayout, layoutParams);
                    mBlackSpace = true;
                }
                break;
            case linearblock:
                width = Tool.getScreenWidth (mContext) - Tool.dip2px (mContext, 20);
                setWidth.invoke (item, width);
                ((ViewGroup) this).addView (item);
                mBlackSpace = false;
                break;
        }
        item.setOnClickListener (new OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext,PictureActivity.class);
                mContext.startActivity (intent);
            }
        });
    }
}
