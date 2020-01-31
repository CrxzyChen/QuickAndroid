package com.example.crxzy.centertainment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

public class Tool {
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources ( ).getDisplayMetrics ( ).density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources ( ).getDisplayMetrics ( ).density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        int width = getDisplayMetrics (context).widthPixels;
        return width;
    }

    public static int getScreenHeight(Context context) {
        int height = getDisplayMetrics (context).heightPixels;
        return height;
    }

    public static float getScreenDensity(Context context) {
        float density = getDisplayMetrics (context).density;
        return density;
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        Resources resources = context.getResources ( );
        DisplayMetrics dm = resources.getDisplayMetrics ( );
        return dm;
    }

    public static class RoundedImageView extends android.support.v7.widget.AppCompatImageView {
        private int mCornerSize = 30;
        private Paint mPaint;

        public RoundedImageView(Context context) {
            super (context);
            mPaint = new Paint ( );
            mPaint.setColor (context.getColor (R.color.colorBackground));
            mPaint.setAntiAlias (true);//消除锯齿
        }

        public RoundedImageView(Context context, @Nullable AttributeSet attrs) {
            super (context, attrs);
            mPaint = new Paint ( );
            mPaint.setColor (context.getColor (R.color.colorBackground));
            mPaint.setAntiAlias (true);//消除锯齿
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw (canvas);
            drawLeftTop (canvas);
            drawRightTop (canvas);
            drawLeftBottom (canvas);
            drawRightBottom (canvas);
        }

        private void drawLeftTop(Canvas canvas) {
            Path path = new Path ( );
            path.moveTo (0, mCornerSize);
            path.lineTo (0, 0);
            path.lineTo (mCornerSize, 0);
            path.arcTo (new RectF (0, 0, mCornerSize * 2, mCornerSize * 2), -90, -90);
            path.close ( );
            canvas.drawPath (path, mPaint);
        }

        private void drawLeftBottom(Canvas canvas) {
            Path path = new Path ( );
            path.moveTo (0, getHeight ( ) - mCornerSize);
            path.lineTo (0, getHeight ( ));
            path.lineTo (mCornerSize, getHeight ( ));
            path.arcTo (new RectF (0, //
                    getHeight ( ) - mCornerSize * 2,// y
                    mCornerSize * 2,//
                    getHeight ( )// getWidth()// y
            ), 90, 90);
            path.close ( );
            canvas.drawPath (path, mPaint);
        }

        private void drawRightBottom(Canvas canvas) {
            Path path = new Path ( );
            path.moveTo (getWidth ( ) - mCornerSize, getHeight ( ));
            path.lineTo (getWidth ( ), getHeight ( ));
            path.lineTo (getWidth ( ), getHeight ( ) - mCornerSize);
            RectF oval = new RectF (getWidth ( ) - mCornerSize * 2, getHeight ( )
                    - mCornerSize * 2, getWidth ( ), getHeight ( ));
            path.arcTo (oval, 0, 90);
            path.close ( );
            canvas.drawPath (path, mPaint);
        }

        private void drawRightTop(Canvas canvas) {
            Path path = new Path ( );
            path.moveTo (getWidth ( ), mCornerSize);
            path.lineTo (getWidth ( ), 0);
            path.lineTo (getWidth ( ) - mCornerSize, 0);
            path.arcTo (new RectF (getWidth ( ) - mCornerSize * 2, 0, getWidth ( ),
                    0 + mCornerSize * 2), -90, 90);
            path.close ( );

            canvas.drawPath (path, mPaint);
        }

        public int getCornerSize() {
            return mCornerSize;
        }

        public void setCornerSize(int cornerSize) {
            this.mCornerSize = cornerSize;
        }
    }

}
