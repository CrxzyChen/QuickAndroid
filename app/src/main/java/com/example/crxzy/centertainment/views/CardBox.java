package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CardBox extends ScrollView {
    public Context mContext;
    private boolean mBlackSpace = false;
    public LinearLayout mContainer;
    private OnTouchBottomListener mTouchBottomListener;
    private OnActiveAreaChangedListener mActiveAreaChangedListener;
    public float mTouchBottomDistance = 0;
    int mTopActiveElementIndex = 0;
    int mBottomActiveElementIndex = 0;
    Set <View> mActiveAreaElementSet = new HashSet <> ( );

    public CardBox(Context context) {
        super (context);
        initCardBox (context);
    }

    public CardBox(Context context, AttributeSet attrs) {
        super (context, attrs);
        initCardBox (context);

    }

    public CardBox(Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
        initCardBox (context);
    }

    private void initCardBox(Context context) {
        mContext = context;
        mContainer = new LinearLayout (mContext);
        LinearLayout.LayoutParams mContainerParam = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mContainer.setLayoutParams (mContainerParam);
        mContainer.setOrientation (LinearLayout.VERTICAL);
        mContainer.setBackgroundColor (mContext.getColor (R.color.colorBackground));
        addView (mContainer);
        setOnScrollChangeListener (new MyScrollChangeListener ( ));
    }

    public enum LayoutStyle {
        block, linearBlock
    }

    class MyScrollChangeListener implements View.OnScrollChangeListener {
        @Override
        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            checkIsTouchBottom (scrollY);
            updateTouchActiveArea ( );
        }

        private void checkIsTouchBottom(int scrollY) {
            int itemBoxHeight = mContainer.getHeight ( );
            int scrollViewHeight = getHeight ( );
            if ((itemBoxHeight - scrollViewHeight - scrollY <= mTouchBottomDistance)) {
                if (mTouchBottomListener != null) {
                    mTouchBottomListener.OnTouchBottom ( );
                }
            }
        }
    }

    private void updateTouchActiveArea() {
        int scrollViewHeight = getHeight ( );
        boolean isSetTopActiveElementIndex = false;
        boolean isActiveAreaChanged = false;
        Set <View> newActiveElementSet = new HashSet <> ( );
        for (int index = 0; index < mContainer.getChildCount ( ); index++) {
            ViewGroup viewGroup = (ViewGroup) mContainer.getChildAt (index);
            Rect rect = new Rect ( );
            viewGroup.getLocalVisibleRect (rect);
            if (rect.top >= 0 && rect.top < scrollViewHeight) {
                if (!isSetTopActiveElementIndex) {
                    if (mTopActiveElementIndex != index) {
                        mTopActiveElementIndex = index;
                        isActiveAreaChanged = true;
                    }
                    isSetTopActiveElementIndex = true;
                }
                for (int index_2 = 0; index_2 < viewGroup.getChildCount ( ); index_2++) {
                    newActiveElementSet.add (viewGroup.getChildAt (index_2));
                }
            }
            if (rect.top > scrollViewHeight) {
                if (mTopActiveElementIndex != index) {
                    mBottomActiveElementIndex = index;
                    isActiveAreaChanged = true;
                    break;
                }
            }
        }
        if (mActiveAreaElementSet.isEmpty ( )) {
            mActiveAreaElementSet = newActiveElementSet;
        } else {
            if (isActiveAreaChanged && mActiveAreaChangedListener != null) {
                Set <View> resultSet = new HashSet <> (newActiveElementSet);
                resultSet.removeAll (mActiveAreaElementSet);
                mActiveAreaChangedListener.setActiveAreaOperation (resultSet);
                resultSet.clear ( );
                resultSet.addAll (mActiveAreaElementSet);
                resultSet.removeAll (newActiveElementSet);
                mActiveAreaChangedListener.setInActiveAreaOperation (resultSet);
                mActiveAreaElementSet = newActiveElementSet;
            }
        }

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

    public interface OnActiveAreaChangedListener {
        void setActiveAreaOperation(Set <View> viewSet);

        void setInActiveAreaOperation(Set <View> viewSet);
    }

    public void setOnActiveAreaChangedListener(OnActiveAreaChangedListener listener) {
        mActiveAreaChangedListener = listener;
    }

    public interface OnTouchBottomListener {
        float setDistance(float distance);

        void OnTouchBottom();
    }

    public void setOnTouchBottomListener(OnTouchBottomListener listener) {
        mTouchBottomDistance = listener.setDistance (mTouchBottomDistance);
        mTouchBottomListener = listener;
    }

    public void addItem(ItemBase item) {
        LayoutStyle layoutStyle = item.setLayoutStyle ( );
        switch (layoutStyle) {
            case block:
                item.setWidth (Tool.getScreenWidth (mContext) / 2 - Tool.dip2px (mContext, 15));
                if (mBlackSpace) {
                    int childCount = mContainer.getChildCount ( );
                    ((ViewGroup) mContainer.getChildAt (childCount - 1)).addView (item);
                    mBlackSpace = false;
                } else {
                    LinearLayout linearLayout = new LinearLayout (mContext);
                    linearLayout.addView (item);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    mContainer.addView (linearLayout, layoutParams);
                    mBlackSpace = true;
                }
                break;
            case linearBlock:
                item.setWidth (Tool.getScreenWidth (mContext) - Tool.dip2px (mContext, 20));
                mContainer.addView (item);
                mBlackSpace = false;
                break;
        }
    }

    public void deleteItem(View view) {
        mContainer.removeView (view);
    }

}