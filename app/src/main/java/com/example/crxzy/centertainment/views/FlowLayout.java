package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {
    public FlowLayout(Context context) {
        super (context);
    }

    public FlowLayout(Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);
    }

    public FlowLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount ( );
        int x = 0;
        int y = 0;
        int lineHeight = 0;

        for (int index = 0; index < childCount; index++) {
            View view = getChildAt (index);
            MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams ( );
            int height = view.getMeasuredHeight ( );
            int width = view.getMeasuredWidth ( );
            lineHeight = Math.max (lineHeight, lp.topMargin + height + lp.bottomMargin);

            if (width + x > getWidth ( )) {
                y += lineHeight;
                x = 0;
                lineHeight = 0;
            }
            view.layout (x + lp.leftMargin, y + lp.topMargin, x + lp.leftMargin + width, y + lp.topMargin + height);
            x += lp.leftMargin + width + lp.rightMargin;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure (widthMeasureSpec, heightMeasureSpec);

        measureChildren (widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode (widthMeasureSpec);
        int widthSize = MeasureSpec.getSize (widthMeasureSpec);
        int heightMode = MeasureSpec.getMode (heightMeasureSpec);
        int heightSize = MeasureSpec.getSize (heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST) {
            switch (widthMode) {
                case MeasureSpec.UNSPECIFIED:
                case MeasureSpec.EXACTLY:
                    setMeasuredDimension (widthSize, calculateHeight (getMinimumHeight ( ), widthSize));
                    break;
                case MeasureSpec.AT_MOST:
                    break;
            }
        }
    }

    private int calculateHeight(int defaultHeightSize, int widthSize) {
        int x = 0;
        int y = 0;
        int lineHeight = 0;
        int childCount = getChildCount ( );
        boolean isNewLine = false;
        for (int index = 0; index < childCount; index++) {
            View view = getChildAt (index);
            MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams ( );
            int height = lp.topMargin + view.getMeasuredHeight ( ) + lp.bottomMargin;
            int width = lp.leftMargin + view.getMeasuredWidth ( ) + lp.rightMargin;
            if (isNewLine) {
                lineHeight = 0;
                isNewLine = false;
            }
            lineHeight = Math.max (lineHeight, height);
            if (width + x < widthSize) {
                x += width;
            } else {
                x = width;
                y += lineHeight;
                isNewLine = true;
            }
        }
        y += lineHeight;
        return Math.max (y, defaultHeightSize);
    }
}
