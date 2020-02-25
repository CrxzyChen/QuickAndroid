package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Tool;

public class LabelBox extends FlowLayout {
    public LabelBox(Context context) {
        super (context);
    }

    public LabelBox(Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);
        CancelAbleLabel label = new CancelAbleLabel (context);
        label.setText (R.string.setting);
        addLabel (label);
        label = new CancelAbleLabel (context);
        label.setText (R.string.string_chinese);
        addLabel (label);
        label = new CancelAbleLabel (context);
        label.setText (R.string.string_english);
        addLabel (label);
        label = new CancelAbleLabel (context);
        label.setText (R.string.string_filter);
        addLabel (label);
        label = new CancelAbleLabel (context);
        label.setText (R.string.string_japanese);
        addLabel (label);
    }

    public LabelBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
    }

    public static class LabelBase extends LinearLayout {
        TextView mTextView;

        public LabelBase(Context context) {
            super (context);
            onInitiation ( );
        }

        public LabelBase(Context context, @Nullable AttributeSet attrs) {
            super (context, attrs);
            onInitiation ( );
        }

        public LabelBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super (context, attrs, defStyleAttr);
            onInitiation ( );
        }


        protected void onInitiation() {
            mTextView = new TextView (getContext ( ));
            mTextView.setPadding (5, 0, 5, 0);
            LayoutParams mainParams = new LayoutParams (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            mainParams.setMargins (5, 5, 5, 5);
            setLayoutParams (mainParams);
            addView (mTextView);
        }

        public void setText(int resId) {
            mTextView.setText (resId);
        }

        public void setTextColor(int color) {
            mTextView.setTextColor (color);
        }

        public void setTextSize(int size) {
            mTextView.setTextSize (size);
        }
    }

    public static class CancelAbleLabel extends LabelBase {
        TextView mCancelButton;
        View mSplitLine;

        public CancelAbleLabel(Context context) {
            super (context);
        }

        public CancelAbleLabel(Context context, @Nullable AttributeSet attrs) {
            super (context, attrs);
        }

        public CancelAbleLabel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super (context, attrs, defStyleAttr);
        }

        @Override
        protected void onInitiation() {
            super.onInitiation ( );


            mCancelButton = new TextView (getContext ( ));
            mSplitLine = new LinearLayout (getContext ( ));

            LayoutParams splitLineParam = new LayoutParams (1, LayoutParams.MATCH_PARENT);
            mSplitLine.setLayoutParams (splitLineParam);
            addView (mSplitLine);

            mCancelButton.setPadding (5, 0, 5, 0);
            mCancelButton.setGravity (TEXT_ALIGNMENT_CENTER);
            mCancelButton.setText (R.string.false_icon);

            setBackground (getContext ( ).getDrawable (R.drawable.labelbox_label));
            setTextColor (getContext ( ).getColor (R.color.white));
            setTextSize (Tool.sp2px (getContext ( ), 6));
            setClickable (true);

            addView (mCancelButton);

        }

        @Override
        public void setTextColor(int color) {
            mTextView.setTextColor (color);
            mSplitLine.setBackgroundColor (color);
            mCancelButton.setTextColor (color);
        }

        @Override
        public void setTextSize(int size) {
            mTextView.setTextSize (size);
            mCancelButton.setTextSize (size);
        }
    }

    public void addLabel(LabelBase label) {
        addView (label);
    }
}
