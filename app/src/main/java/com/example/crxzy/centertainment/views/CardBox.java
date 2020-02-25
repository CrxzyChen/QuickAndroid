package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Tool;

import java.util.HashSet;
import java.util.Set;

public class CardBox extends ScrollView {
    public float mRefreshIconDragMaxDistance = 40;
    public int mRefreshIconSize = 50;
    public float mRefreshIconTrigger = 0;
    public Context mContext;
    private boolean mBlackSpace = false;
    public LinearLayout mContainer;
    private OnTouchBottomListener mTouchBottomListener;
    private OnActiveAreaChangedListener mActiveAreaChangedListener;
    public float mTouchBottomDistance = 0;
    int mTopActiveElementIndex = 0;
    int mBottomActiveElementIndex = 0;
    Set <View> mActiveAreaElementSet = new HashSet <> ( );
    float mTouchDownY = 0;
    OnTopOverDragListener mTopOverDragListener;
    private ImageView mRefreshIcon;
    RelativeLayout.LayoutParams mRefreshIconParam;
    private boolean isOverDragStart = false;
    private float mLastY;
    boolean mIsOpenTopOverDragListener = false;
    OnOverDragRefreshListener mRefreshListener;

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

        RelativeLayout mMainView = new RelativeLayout (mContext);
        RelativeLayout.LayoutParams mMainViewParam = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        mRefreshIcon = new ImageView (mContext);
        mRefreshIconSize = Tool.dip2px (mContext, mRefreshIconSize);
        mRefreshIconDragMaxDistance = Tool.dip2px (mContext, mRefreshIconDragMaxDistance);
        mRefreshIconTrigger = Tool.dip2px (mContext, mRefreshIconTrigger);

        int x = Tool.getScreenWidth (mContext) / 2 - mRefreshIconSize / 2;
        mRefreshIconParam = new RelativeLayout.LayoutParams (mRefreshIconSize, mRefreshIconSize);
        mRefreshIconParam.leftMargin = x;
        mRefreshIconParam.topMargin = -mRefreshIconSize;
        mRefreshIconParam.addRule (RelativeLayout.ALIGN_PARENT_TOP);
        mRefreshIcon.setImageDrawable (mContext.getDrawable (R.drawable.ic_launcher_foreground));
        mRefreshIcon.setLayoutParams (mRefreshIconParam);

        mContainer = new LinearLayout (mContext);
        LinearLayout.LayoutParams mContainerParam = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.setMinimumHeight (mRefreshIconSize+(int)mRefreshIconDragMaxDistance);
        mContainer.setLayoutParams (mContainerParam);
        mContainer.setOrientation (LinearLayout.VERTICAL);
        mMainView.setLayoutParams (mMainViewParam);
        mMainView.addView (mContainer);
        mMainView.addView (mRefreshIcon);

        addView (mMainView);
        setOnScrollChangeListener (new MyScrollChangeListener ( ));
        setOnTopOverDragListener (new MyTopOverDragListener ( ));
        setOnOverDragRefreshListener (new MyRefreshListener ( ));
    }

    public void clearAll() {
        mContainer.removeAllViews ( );
    }

    public void setIsOpenTopOverDragListener(boolean status) {
        mIsOpenTopOverDragListener = status;
        if (mIsOpenTopOverDragListener) {
            setOverScrollMode (OVER_SCROLL_NEVER);
        } else {
            setOverScrollMode (OVER_SCROLL_ALWAYS);
        }
    }

    public enum LayoutStyle {
        block, linearBlock
    }

    @Override
    public boolean performClick() {
        return super.performClick ( );
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction ( );
        float currentScrollY = getScrollY ( );
        float currentY = ev.getRawY ( );
        long intervalTime = ev.getEventTime ( );
        float deltaY = currentY - mLastY;
        float totalY = currentY - mTouchDownY;
        if (mIsOpenTopOverDragListener && currentScrollY == 0 && totalY >= 0) {
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    if (!isOverDragStart) {
                        mLastY = mTouchDownY = ev.getRawY ( );
                        mTopOverDragListener.OnOverDragStart ( );
                        isOverDragStart = true;
                    } else {
                        mTopOverDragListener.OnOverDragDuration (deltaY, totalY, intervalTime);
                        mLastY = currentY;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isOverDragStart = false;
                    mTopOverDragListener.OnOverDragEnd (ev.getRawY ( ) - mTouchDownY);
                    mLastY = 0;
                    mTouchDownY = 0;
                    performClick ( );
                    break;
            }
            return true;
        }
        return super.onTouchEvent (ev);
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

    public void setOnActiveAreaChangedListener(OnActiveAreaChangedListener listener) {
        mActiveAreaChangedListener = listener;
    }

    public void setOnTopOverDragListener(OnTopOverDragListener listener) {
        mTopOverDragListener = listener;
    }

    public void setOnTouchBottomListener(OnTouchBottomListener listener) {
        mTouchBottomDistance = listener.setDistance (mTouchBottomDistance);
        mTouchBottomListener = listener;
    }

    public void setOnOverDragRefreshListener(OnOverDragRefreshListener listener) {
        mRefreshListener = listener;
    }

    public interface OnTouchBottomListener {
        float setDistance(float distance);

        void OnTouchBottom();
    }

    public interface OnTopOverDragListener {
        void OnOverDragStart();

        void OnOverDragDuration(float deltaY, float totalY, long intervalTime);

        void OnOverDragEnd(float endY);
    }

    public interface OnOverDragRefreshListener {
        void OnRefresh();
    }

    class MyRefreshListener implements OnOverDragRefreshListener {

        @Override
        public void OnRefresh() {
            playEndRefreshAnimation ( );
        }
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

    class MyTopOverDragListener implements OnTopOverDragListener {
        @Override
        public void OnOverDragStart() {
        }

        @Override
        public void OnOverDragDuration(float deltaY, float totalY, long intervalTime) {
            if (mRefreshIconParam.topMargin < mRefreshIconDragMaxDistance) {
                mRefreshIconParam.topMargin += deltaY * (1 - (mRefreshIconParam.topMargin + mRefreshIconSize) / (mRefreshIconSize + mRefreshIconDragMaxDistance));
            } else {
                return;
            }
            mRefreshIcon.setLayoutParams (mRefreshIconParam);
            mRefreshIcon.invalidate ( );
        }

        @Override
        public void OnOverDragEnd(float endY) {
            if (mRefreshIconParam.topMargin > mRefreshIconTrigger) {
                playRefreshingAnimation ( );
                mRefreshListener.OnRefresh ( );
            } else {
                playEndRefreshAnimation ( );
            }
        }
    }

    public void playEndRefreshAnimation() {
        if(mRefreshIcon.getAnimation ()!=null&&!mRefreshIcon.getAnimation ().hasEnded ()){
            mRefreshIcon.getAnimation ().getStartTime ();
            mRefreshIconParam.topMargin = (int) mRefreshIconTrigger;
            mRefreshIcon.setLayoutParams (mRefreshIconParam);
            mRefreshIcon.invalidate ( );
        }
        TranslateAnimation animation = new TranslateAnimation (0, 0, 0, -mRefreshIconParam.topMargin - mRefreshIconSize);
        animation.setDuration (200);
        animation.setAnimationListener (new Animation.AnimationListener ( ) {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRefreshIconParam.topMargin = -mRefreshIconSize;
                mRefreshIcon.setLayoutParams (mRefreshIconParam);
                mRefreshIcon.invalidate ( );
                mRefreshIcon.clearAnimation ( );
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mRefreshIcon.startAnimation (animation);
    }

    public void playRefreshingAnimation() {
        mRefreshIcon.clearAnimation ( );
        TranslateAnimation animation = new TranslateAnimation (0, 0, 0, -mRefreshIconParam.topMargin + mRefreshIconTrigger);

        animation.setDuration (200);
        animation.setAnimationListener (new Animation.AnimationListener ( ) {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRefreshIconParam.topMargin = (int) mRefreshIconTrigger;
                mRefreshIcon.setLayoutParams (mRefreshIconParam);
                mRefreshIcon.invalidate ( );
                mRefreshIcon.clearAnimation ( );
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mRefreshIcon.startAnimation (animation);
    }
}