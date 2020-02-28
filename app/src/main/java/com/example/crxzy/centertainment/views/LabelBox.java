package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LabelBox extends FlowLayout {
    public Set <CharSequence> mLabelSet = new HashSet <> ( );

    public LabelBox(Context context) {
        super (context);
    }

    public LabelBox(Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);
        CancelAbleLabel label = new CancelAbleLabel (context);
    }

    public LabelBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
    }

    public List <String> getAllLabels() {
        List <String> labels = new ArrayList <> ( );
        for (CharSequence key : mLabelSet) {
            labels.add ((String) key);
        }
        return labels;
    }

    public static class LabelBase extends LinearLayout {
        public TextView mTextView;

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
            mTextView.setPadding (10, 0, 10, 5);
            LayoutParams mainParams = new LayoutParams (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            mainParams.setMargins (5, 5, 5, 5);
            setLayoutParams (mainParams);
            addView (mTextView);
        }

        public void setText(int resId) {
            mTextView.setText (resId);
        }

        public void setText(String content) {
            mTextView.setText (content);
        }

        public void setTextColor(int color) {
            mTextView.setTextColor (color);
        }

        public void setTextSize(int size) {
            mTextView.setTextSize (size);
        }
    }

    public static class Label extends LabelBase {
        public Label(Context context) {
            super (context);
        }

        public Label(Context context, @Nullable AttributeSet attrs) {
            super (context, attrs);
        }

        public Label(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super (context, attrs, defStyleAttr);
        }

        public Label(Context context, String content) {
            super (context);
            setText (content);
        }

        public void onInitiation() {
            super.onInitiation ( );
            setBackground (getContext ( ).getDrawable (R.drawable.labelbox_label));
            setTextColor (getContext ( ).getColor (R.color.white));
            setElevation (Tool.dip2px (getContext ( ), 2));
            setClickable (true);
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

        public CancelAbleLabel(Context context, String content) {
            super (context);
            setText (content);
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
            mCancelButton.setOnClickListener (new OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    CancelAbleLabel label = (CancelAbleLabel) v.getParent ( );
                    LabelBox labelBox = (LabelBox) label.getParent ( );
                    labelBox.mLabelSet.remove (label.mTextView.getText ( ));
                    labelBox.removeView (label);
                }
            });
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
        if (!mLabelSet.contains (label.mTextView.getText ( ))) {
            mLabelSet.add (label.mTextView.getText ( ));
            addView (label);
        }
    }
}
