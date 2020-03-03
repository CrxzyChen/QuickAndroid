package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.crxzy.centertainment.R;

import java.util.Arrays;
import java.util.Objects;

public class RoundedImageView extends ImageView {
    private RectF mRect = new RectF ( );
    private float[] mRadiusArray = new float[8];
    private Path mPath = new Path ( );
    PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode (PorterDuff.Mode.SRC_IN);
    private Paint mBackgroundPaint;
    private Paint mContentPaint;

    public RoundedImageView(Context context) {
        super (context);
        initBackgroundPaint (context);
        initContentPaint ( );
        setLayerType (LAYER_TYPE_HARDWARE, null);
    }

    private void initContentPaint() {
        mContentPaint = new Paint (Paint.ANTI_ALIAS_FLAG);
        mContentPaint.setXfermode (porterDuffXfermode);
    }

    private void initBackgroundPaint(Context context) {
        mBackgroundPaint = new Paint (Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle (Paint.Style.FILL);
        mBackgroundPaint.setColor (context.getColor (R.color.gray));
        mBackgroundPaint.setAntiAlias (true);//消除锯齿
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes (attrs, R.styleable.RoundedImageView);
        if (typedArray != null) {
            Arrays.fill (mRadiusArray, typedArray.getDimension (R.styleable.RoundedImageView_radius, 0));
            mRadiusArray[0] = mRadiusArray[1] = typedArray.getDimension (R.styleable.RoundedImageView_bottomLeftRadius, 0);
            mRadiusArray[2] = mRadiusArray[3] = typedArray.getDimension (R.styleable.RoundedImageView_bottomRightRadius, 0);
            mRadiusArray[4] = mRadiusArray[5] = typedArray.getDimension (R.styleable.RoundedImageView_topLeftRadius, 0);
            mRadiusArray[6] = mRadiusArray[7] = typedArray.getDimension (R.styleable.RoundedImageView_topRightRadius, 0);
        }
        Objects.requireNonNull (typedArray).recycle ( );
        initBackgroundPaint (context);
        initContentPaint ( );
        setLayerType (LAYER_TYPE_HARDWARE, null);
    }

    @Override
    public void draw(Canvas canvas) {
        if (getDrawable ( ) instanceof BitmapDrawable) {
            mRect.set (0, 0, getWidth ( ), getHeight ( ));
            Bitmap bitmap = ((BitmapDrawable) getDrawable ( )).getBitmap ( );
            if (bitmap != null) {
                Bitmap target = Bitmap.createBitmap (getWidth ( ), getHeight ( ), Bitmap.Config.ARGB_8888);
                super.draw (new Canvas (target));
                canvas.drawColor (Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                mPath.addRoundRect (mRect, mRadiusArray, Path.Direction.CCW);
                canvas.drawPath (mPath, mBackgroundPaint);
                canvas.drawBitmap (target, 0, 0, mContentPaint);
            }
        } else {
            super.draw (canvas);
        }
    }

    public void setCornerSize(float cornerSize) {
        Arrays.fill (mRadiusArray, cornerSize);
    }
}