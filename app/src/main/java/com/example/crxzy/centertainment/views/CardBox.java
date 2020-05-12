package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.tools.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CardBox extends RecyclerView {
    public static final int SCROLL_SPEED_LIMIT = 80;

    private boolean isOverDragStart = false;
    private float mLastY;
    private static boolean mIsOverSpeed = false;
    private int mScrollSpeed;
    private boolean mIsSpeedJump = false;
    private CardBoxAdapt mAdapt;

    float mTouchDownY = 0;

    public void addResource(ResourceManager.ResourceBase resource) {
        MyAdapter adapter = (MyAdapter) getAdapter ( );
        assert adapter != null;
        adapter.addItem (adapter.getItemCount ( ) - 1, resource);
    }

    @Override
    public boolean performClick() {
        return super.performClick ( );
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mAdapt.isListenerOverDragRefresh) {
            switch (ev.getAction ( )) {
                case MotionEvent.ACTION_MOVE:
                    if (mLastY == 0) {
                        mLastY = ev.getRawY ( );
                    }
                    float deltaY = mLastY - ev.getRawY ( );
                    mLastY = ev.getRawY ( );
                    if (!canScrollVertically (-1) && deltaY < 0) {
                        if (!isOverDragStart) {
                            mTouchDownY = ev.getRawY ( );
                            isOverDragStart = true;
                        }
                    }
                    if (isOverDragStart) {
                        float currentY = ev.getRawY ( );
                        float totalY = currentY - mTouchDownY;

                        CardBase card = ((MyAdapter.ViewHolder) Objects.requireNonNull (findViewHolderForAdapterPosition (0))).card;
                        MarginLayoutParams cardParams = (MarginLayoutParams) card.getLayoutParams ( );

                        if (Tool.dip2px (getContext ( ), 80) < totalY) {
                            totalY = Tool.dip2px (getContext ( ), 80);
                            mTouchDownY = ev.getRawY ( ) - totalY;
                        }
                        totalY = Math.max (totalY, 1);

                        cardParams.topMargin = (int) (-Tool.dip2px (getContext ( ), 80) + totalY);
                        card.setLayoutParams (cardParams);
                        smoothScrollToPosition (0);
                        if (deltaY > 0 && cardParams.topMargin == -Tool.dip2px (getContext ( ), 80) + 1) {
                            isOverDragStart = false;
                        }
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isOverDragStart) {
                        CardBase card = ((MyAdapter.ViewHolder) Objects.requireNonNull (findViewHolderForAdapterPosition (0))).card;
                        MarginLayoutParams cardParams = (MarginLayoutParams) card.getLayoutParams ( );
                        if (cardParams.topMargin > -Tool.dip2px (getContext ( ), 40)) {
                            openRefreshNotify ( );
                            mAdapt.onRefresh ( );
                        } else {
                            closeRefreshNotify ( );
                        }
                        isOverDragStart = false;
                    }
                    mLastY = 0;
                    mTouchDownY = 0;
                    performClick ( );
                    break;
            }
        }
        return super.onTouchEvent (ev);
    }

    public void openRefreshNotify() {
        MyAdapter.ViewHolder viewHolder = (MyAdapter.ViewHolder) findViewHolderForAdapterPosition (0);
        if (null != viewHolder) {
            CardBase card = viewHolder.card;
            MarginLayoutParams cardParams = (MarginLayoutParams) card.getLayoutParams ( );
            cardParams.topMargin = -Tool.dip2px (getContext ( ), 40);
            card.setLayoutParams (cardParams);
        }
    }

    public void closeRefreshNotify() {
        MyAdapter.ViewHolder viewHolder = (MyAdapter.ViewHolder) findViewHolderForAdapterPosition (0);
        if (null != viewHolder) {
            CardBase card = viewHolder.card;
            MarginLayoutParams cardParams = (MarginLayoutParams) card.getLayoutParams ( );
            cardParams.topMargin = -Tool.dip2px (getContext ( ), 80) + 1;
            card.setLayoutParams (cardParams);
        }
    }

    public void clearAll() {
        ResourceManager resourceManager = ((MyAdapter) Objects.requireNonNull (getAdapter ( ))).mResourceManager;
        resourceManager.clearAll ( );
        resourceManager.add (0, new HeaderResource ( ));
        resourceManager.add (1, new FooterResource ( ));
    }

    public void refresh() {
        openRefreshNotify ( );
        mAdapt.onRefresh ( );
    }

    public abstract static class ResourceManager {
        Map <Integer, Class <?>> mViewTypeToTemplete = new HashMap <> ( );
        List <ResourceBase> mResourceList = new ArrayList <> ( );

        ResourceManager() {
            mViewTypeToTemplete.put (MyAdapter.TYPE_HEADER, RefreshNotifyCard.class);
            mViewTypeToTemplete.put (MyAdapter.TYPE_FOOTER, LoadNotifyCard.class);
            initViewTypeToView (mViewTypeToTemplete);
        }

        abstract protected void initViewTypeToView(Map <Integer, Class <?>> mViewTypeToView);

        public void add(int position, ResourceBase resource) {
            mResourceList.add (position, resource);
        }

        Class <?> getTemplete(int viewType) {
            return mViewTypeToTemplete.get (viewType);
        }

        int getViewType(int position) {
            return mResourceList.get (position).getViewType ( );
        }

        public ResourceBase get(int i) {
            return mResourceList.get (i);
        }

        int size() {
            return mResourceList.size ( );
        }

        void clearAll() {
            mResourceList.clear ( );
        }

        public abstract static class ResourceBase {
            public boolean isEmpty = true;

            abstract public int getViewType();

            abstract public int getSpanCount();
        }
    }

    public abstract static class CardBase extends FrameLayout {
        public FrameLayout.LayoutParams mItemLayoutParams = new FrameLayout.LayoutParams (0, 0);
        public Context mContext;
        public boolean isEmpty = true;
        public int resourceId = -1;

        public CardBase(Context context) {
            super (context);
            mContext = context;
            setCommonStyle ( );
        }

        public void setCommonStyle() {
            setBackgroundColor (mContext.getColor (R.color.colorPrimary));
            setBackground (mContext.getDrawable (R.drawable.roundrect_image));//设置圆角
            setElevation (Tool.dip2px (mContext, 5));//设置阴影
        }

        abstract public void clear();

        abstract public void loadResource(ResourceManager.ResourceBase resource);
    }

    static class HeaderResource extends ResourceManager.ResourceBase {

        @Override
        public int getViewType() {
            return MyAdapter.TYPE_HEADER;
        }

        @Override
        public int getSpanCount() {
            return 2;
        }
    }

    static class RefreshNotifyCard extends CardBase {

        public RefreshNotifyCard(Context context) {
            super (context);
            setBackgroundColor (getContext ( ).getColor (R.color.colorBackground));
            ViewGroup.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.height = Tool.dip2px (getContext ( ), 80);
            layoutParams.topMargin = -Tool.dip2px (getContext ( ), 80) + 1;
            setLayoutParams (layoutParams);
            TextView info = new TextView (getContext ( ));
            info.setHeight (Tool.dip2px (getContext ( ), 40));
            info.setText ("下拉刷新");
            info.setTextColor (getContext ( ).getColor (R.color.gray));
            info.setGravity (Gravity.CENTER);
            LayoutParams infoParams = new LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            infoParams.gravity = Gravity.BOTTOM;
            info.setLayoutParams (infoParams);
            addView (info);
        }

        @Override
        public void clear() {

        }

        @Override
        public void loadResource(ResourceManager.ResourceBase resource) {

        }
    }

    static class FooterResource extends ResourceManager.ResourceBase {
        boolean isVisible = false;

        @Override
        public int getViewType() {
            return MyAdapter.TYPE_FOOTER;
        }

        @Override
        public int getSpanCount() {
            return 2;
        }
    }

    static class LoadNotifyCard extends CardBase {
        public LoadNotifyCard(Context context) {
            super (context);
            RelativeLayout mLoadingBlank = new RelativeLayout (mContext);
            mLoadingBlank.setBackground (mContext.getDrawable (R.drawable.cardbox_card));
            mLoadingBlank.setGravity (Gravity.CENTER_HORIZONTAL);
            RelativeLayout.LayoutParams mLoadingBlankParam = new RelativeLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ImageView loadingImage = new ImageView (mContext);
            RelativeLayout.LayoutParams loadingImageParam = new RelativeLayout.LayoutParams (Tool.dip2px (mContext, 150), Tool.dip2px (mContext, 150));
            loadingImage.setScaleType (android.widget.ImageView.ScaleType.CENTER_CROP);
            loadingImage.setLayoutParams (loadingImageParam);
            try {
                Drawable decodedAnimation = ImageDecoder.decodeDrawable (ImageDecoder.createSource (getResources ( ), R.drawable.loading));
                if (decodedAnimation instanceof AnimatedImageDrawable) {
                    // Prior to start(), the first frame is displayed.
                    ((AnimatedImageDrawable) decodedAnimation).start ( );
                }
                loadingImage.setImageDrawable (decodedAnimation);
            } catch (IOException e) {
                e.printStackTrace ( );
            }
            mLoadingBlank.addView (loadingImage);
            mLoadingBlank.setMinimumHeight (Tool.dip2px (mContext, 150));
            mLoadingBlank.setLayoutParams (mLoadingBlankParam);
            addView (mLoadingBlank);
        }

        @Override
        public void clear() {

        }

        @Override
        public void loadResource(ResourceManager.ResourceBase resource) {
            if (!((FooterResource) resource).isVisible) {
                setVisibility (INVISIBLE);
            }
        }
    }


    public CardBox(@NonNull Context context) {
        super (context);
        onInitiation ( );
    }

    public CardBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);
        onInitiation ( );
    }

    public CardBox(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
        onInitiation ( );
    }

    private void onInitiation() {
        setPadding (Tool.dip2px (getContext ( ), 5), Tool.dip2px (getContext ( ), 5), Tool.dip2px (getContext ( ), 5), Tool.dip2px (getContext ( ), 5));
        int mSpanCount = 2;
        setLayoutManager (new GridLayoutManager (getContext ( ), mSpanCount, GridLayoutManager.VERTICAL, false));
        setItemViewCacheSize (0);
        addOnScrollListener (new MyOnScrollListener ( ));
    }

    class MyOnScrollListener extends OnScrollListener {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled (recyclerView, dx, dy);
            mScrollSpeed = Math.abs (dy);
            if (mScrollSpeed > Tool.dip2px (getContext ( ), SCROLL_SPEED_LIMIT)) {
                mIsOverSpeed = true;
                mIsSpeedJump = true;
            } else {
                mIsOverSpeed = false;
            }
            checkIsTouchBottom ( );
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            // SCROLL_STATE_IDLE:停止滚动；
            // SCROLL_STATE_DRAGGING: 用户慢慢拖动
            // SCROLL_STATE_SETTLING：惯性滚动
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (mIsSpeedJump || mScrollSpeed > 5) {//judge whether scroll arrested
                    int firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull (getLayoutManager ( ))).findFirstVisibleItemPosition ( );
                    int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull (getLayoutManager ( ))).findLastVisibleItemPosition ( );
                    int firstPosition = firstVisibleItemPosition;
                    while (findViewHolderForAdapterPosition (--firstPosition) != null) ;
                    for (int index = firstPosition + 1; index < lastVisibleItemPosition + 1; index++) {
                        if (((MyAdapter.ViewHolder) Objects.requireNonNull (findViewHolderForAdapterPosition (index))).card.resourceId != index) {
                            Objects.requireNonNull (getAdapter ( )).notifyItemChanged (index);
                        }
                    }
                }
                mIsOverSpeed = false;
                mIsSpeedJump = false;
            }
        }

        private void checkIsTouchBottom() {
            MyAdapter adapter = (MyAdapter) Objects.requireNonNull (CardBox.this.getAdapter ( ));

            if (adapter.getItemCount ( ) > 2) {
                int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull (getLayoutManager ( ))).findLastVisibleItemPosition ( );

                if (lastVisibleItemPosition == Math.max (adapter.getItemCount ( ) - 1, 0)) {
                    if (mAdapt.isListenerTouchBottom) {
                        mAdapt.onTouchBottom ( );
                    }
                }
            }
        }
    }

    public void openLoadNotify() {
        MyAdapter adapter = (MyAdapter) Objects.requireNonNull (CardBox.this.getAdapter ( ));
        MyAdapter.ViewHolder holder = (MyAdapter.ViewHolder) findViewHolderForAdapterPosition (adapter.getItemCount ( ) - 1);
        if (holder != null) {
            ((FooterResource) adapter.mResourceManager.get (adapter.getItemCount ( ) - 1)).isVisible = true;
            LoadNotifyCard card = (LoadNotifyCard) holder.card;
            card.setVisibility (VISIBLE);
        }
    }

    public void setResourceManager(ResourceManager manager) {
        setAdapter (new MyAdapter (manager));
    }

    public void setCardBoxAdapt(final CardBoxAdapt adapt) {
        mAdapt = adapt;
        setResourceManager (new CardBox.ResourceManager ( ) {
            @Override
            protected void initViewTypeToView(Map <Integer, Class <?>> mViewTypeToView) {
                adapt.initViewTypeToView (mViewTypeToView);
            }
        });
    }

    static class MyAdapter extends RecyclerView.Adapter <MyAdapter.ViewHolder> {
        static final int TYPE_HEADER = -1;
        static final int TYPE_FOOTER = -2;
        ResourceManager mResourceManager;

        MyAdapter(ResourceManager resourceManager) {
            mResourceManager = resourceManager;
            mResourceManager.add (0, new HeaderResource ( ));
            mResourceManager.add (1, new FooterResource ( ));
        }

        void addItem(int position, ResourceManager.ResourceBase resource) {
            mResourceManager.add (position, resource);
            notifyItemInserted (position);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEADER;
            } else if (position == getItemCount ( ) - 1) {
                return TYPE_FOOTER;
            } else {
                return mResourceManager.getViewType (position);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            ViewHolder viewHolder = null;
            try {
                Constructor constructor = mResourceManager.getTemplete (i).getDeclaredConstructor (Context.class);
                viewHolder = new ViewHolder ((View) constructor.newInstance (viewGroup.getContext ( )));
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace ( );
            }
            assert viewHolder != null;
            return viewHolder;
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView (recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager ( );
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager g = (GridLayoutManager) manager;
                g.setSpanSizeLookup (new GridLayoutManager.SpanSizeLookup ( ) {
                    @Override
                    public int getSpanSize(int position) {
                        return mResourceManager.get (position).getSpanCount ( );
                    }
                });
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            if (viewHolder.card.resourceId != i) {
                if (!viewHolder.card.isEmpty) {
                    viewHolder.card.clear ( );
                    viewHolder.card.isEmpty = true;
                }
                if (!mIsOverSpeed) {
                    viewHolder.card.resourceId = i;
                    viewHolder.card.isEmpty = false;
                    viewHolder.card.loadResource (mResourceManager.get (i));
                }
            }
        }

        @Override
        public int getItemCount() {
            return mResourceManager.size ( );
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            CardBase card;

            ViewHolder(@NonNull View itemView) {
                super (itemView);
                card = (CardBase) itemView;
            }
        }

    }

    static public abstract class CardBoxAdapt {
        boolean isListenerTouchBottom = true;
        boolean isListenerOverDragRefresh = true;
        final int mSingleLoadSize = 10;
        int mLoadedSize;
        MyHandler mHandler;
        boolean mIsLoading = false;
        private LoadDataCallback mCallback;
        CardBox mCardBox;

        protected CardBoxAdapt(CardBox cardBox) {
            mCardBox = cardBox;
            mHandler = new MyHandler (cardBox, this);
            mCallback = new LoadDataCallback ( );
            requestData (mLoadedSize, mSingleLoadSize, mCallback);
        }

        void onRefresh() {
            mCardBox.clearAll ( );
            mLoadedSize = 0;
            requestData (mLoadedSize, mSingleLoadSize, mCallback);
        }

        public class LoadDataCallback extends Network.Callback {
            @Override
            public void success(Network.Response response) {
                Message message = mHandler.obtainMessage ( );
                message.obj = response.content;
                message.what = MyHandler.LOAD_ITEM;
                mHandler.sendMessage (message);
            }

            @Override
            public void error(Network.Response response) {

            }
        }

        static class MyHandler extends Handler {
            WeakReference <CardBox> mCardBoxReference;
            WeakReference <CardBoxAdapt> mCardBoxAdaptReference;
            static final int LOAD_ITEM = 100;

            MyHandler(CardBox cardBox, CardBoxAdapt adapt) {
                mCardBoxReference = new WeakReference <> (cardBox);
                mCardBoxAdaptReference = new WeakReference <> (adapt);
            }

            @Override
            public void handleMessage(Message msg) {
                CardBox cardBox = mCardBoxReference.get ( );
                CardBoxAdapt adapt = mCardBoxAdaptReference.get ( );
                switch (msg.what) {
                    case LOAD_ITEM:
                        JSONArray jsonArray = (JSONArray) msg.obj;
                        for (int index = 0; index < jsonArray.length ( ); index++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject (index);
                                adapt.loadItem (cardBox, object);
                                cardBox.closeRefreshNotify ( );
                            } catch (JSONException e) {
                                e.printStackTrace ( );
                            }
                        }
                        adapt.mLoadedSize += jsonArray.length ( );
                        adapt.mIsLoading = false;
                        break;
                }
            }
        }

        void onTouchBottom() {
            if (!mIsLoading) {
                requestData (mLoadedSize, mSingleLoadSize, mCallback);
                mCardBox.openLoadNotify ( );
                mIsLoading = true;
            }
        }

        protected abstract void initViewTypeToView(Map <Integer, Class <?>> mViewTypeToView);

        protected abstract void requestData(int loadedSize, int singleLoadSize, LoadDataCallback callback);

        protected abstract void loadItem(CardBox cardBox, JSONObject obj);
    }
}
