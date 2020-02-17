package com.example.crxzy.centertainment.views;

import android.content.Context;
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

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Tool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ItemsBoxView extends LinearLayout {
    public Context mContext;
    private boolean mBlackSpace = false;

    public void deleteItem(View view) {
        removeView (view);
    }

    public enum LayoutStyle {
        block, linearBlock
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

        abstract public LayoutStyle setLayoutStyle();

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
        public BlockItem(Context context) {
            super (context);
        }

        @Override
        public LayoutStyle setLayoutStyle() {
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
        public LayoutStyle setLayoutStyle() {
            return LayoutStyle.linearBlock;
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

    public void addItem(ItemBase item) {
        LayoutStyle layoutStyle = item.setLayoutStyle ( );
        switch (layoutStyle) {
            case block:
                item.setWidth (Tool.getScreenWidth (mContext) / 2 - Tool.dip2px (mContext, 15));
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
                item.setWidth (Tool.getScreenWidth (mContext) - Tool.dip2px (mContext, 20));
                addView (item);
                mBlackSpace = false;
                break;
        }
    }
}