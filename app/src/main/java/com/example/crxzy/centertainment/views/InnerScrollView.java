package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.example.crxzy.centertainment.R;

import java.util.Objects;

public class InnerScrollView extends ScrollView {
    private int mOuterScrollViewId;
    private ScrollView mOuterScrollView;
    public AppCompatActivity mContext;

    public InnerScrollView(Context context) {
        super (context);
        mContext = (AppCompatActivity) context;
    }

    public InnerScrollView(Context context, AttributeSet attrs) {
        super (context, attrs);
        mContext = (AppCompatActivity) context;
        TypedArray typedArray = context.obtainStyledAttributes (attrs, R.styleable.InnerScrollView);
        if (typedArray != null) {
            mOuterScrollViewId = typedArray.getResourceId (R.styleable.InnerScrollView_outerScrollViewId, 0);
        }
        Objects.requireNonNull (typedArray).recycle ( );
    }

    public InnerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        mContext = (AppCompatActivity) context;
        TypedArray typedArray = context.obtainStyledAttributes (attrs, R.styleable.InnerScrollView);
        if (typedArray != null) {
            mOuterScrollViewId = typedArray.getResourceId (R.styleable.InnerScrollView_outerScrollViewId, 0);
        }
        Objects.requireNonNull (typedArray).recycle ( );
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent ( ).requestDisallowInterceptTouchEvent (true);

        return super.onInterceptTouchEvent (ev);
    }
}
