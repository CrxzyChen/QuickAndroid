package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.example.crxzy.centertainment.R;

import java.util.Objects;

public class RoundedImageView extends ImageView {
    private float mRadius;
    private float mBottomLeftRadius;
    private float mBottomRightRadius;
    private float mTopLeftRadius;
    private float mTopRadiusRadius;

    private Paint mPaint;

    public RoundedImageView(Context context) {
        super (context);
        mPaint = new Paint ( );
        mPaint.setColor (context.getColor (R.color.colorBackground));
        mPaint.setAntiAlias (true);//消除锯齿
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes (attrs, R.styleable.RoundedImageView);
        if (typedArray != null) {
            mRadius = typedArray.getDimension (R.styleable.RoundedImageView_radius, 0);
            mBottomLeftRadius = typedArray.getDimension (R.styleable.RoundedImageView_bottomLeftRadius, 0);
            mBottomRightRadius = typedArray.getDimension (R.styleable.RoundedImageView_bottomRightRadius, 0);
            mTopLeftRadius = typedArray.getDimension (R.styleable.RoundedImageView_topLeftRadius, 0);
            mTopRadiusRadius = typedArray.getDimension (R.styleable.RoundedImageView_topRightRadius, 0);
        }
        Objects.requireNonNull (typedArray).recycle ( );

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
        float radius = mTopLeftRadius != 0 ? mTopLeftRadius : mRadius;

        Path path = new Path ( );
        path.moveTo (0, radius);
        path.lineTo (0, 0);
        path.lineTo (radius, 0);
        path.arcTo (new RectF (0, 0, radius * 2, radius * 2), -90, -90);
        path.close ( );
        canvas.drawPath (path, mPaint);
    }

    private void drawLeftBottom(Canvas canvas) {
        float radius = mBottomLeftRadius != 0 ? mBottomLeftRadius : mRadius;

        Path path = new Path ( );
        path.moveTo (0, getHeight ( ) - radius);
        path.lineTo (0, getHeight ( ));
        path.lineTo (radius, getHeight ( ));
        path.arcTo (new RectF (0, //
                getHeight ( ) - radius * 2,// y
                radius * 2,//
                getHeight ( )// getWidth()// y
        ), 90, 90);
        path.close ( );
        canvas.drawPath (path, mPaint);
    }

    private void drawRightBottom(Canvas canvas) {
        float radius = mBottomRightRadius != 0 ? mBottomRightRadius : mRadius;

        Path path = new Path ( );
        path.moveTo (getWidth ( ) - radius, getHeight ( ));
        path.lineTo (getWidth ( ), getHeight ( ));
        path.lineTo (getWidth ( ), getHeight ( ) - radius);
        RectF oval = new RectF (getWidth ( ) - radius * 2, getHeight ( )
                - radius * 2, getWidth ( ), getHeight ( ));
        path.arcTo (oval, 0, 90);
        path.close ( );
        canvas.drawPath (path, mPaint);
    }

    private void drawRightTop(Canvas canvas) {
        float radius = mTopRadiusRadius != 0 ? mTopRadiusRadius : mRadius;

        Path path = new Path ( );
        path.moveTo (getWidth ( ), radius);
        path.lineTo (getWidth ( ), 0);
        path.lineTo (getWidth ( ) - radius, 0);
        path.arcTo (new RectF (getWidth ( ) - radius * 2, 0, getWidth ( ),
                radius * 2), -90, 90);
        path.close ( );

        canvas.drawPath (path, mPaint);
    }

    public float getCornerSize() {
        return mRadius;
    }

    public void setCornerSize(float cornerSize) {
        mRadius = cornerSize;
    }
}