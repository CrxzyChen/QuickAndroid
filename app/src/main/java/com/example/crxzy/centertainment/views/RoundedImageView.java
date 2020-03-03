package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.example.crxzy.centertainment.R;

import java.util.Arrays;
import java.util.Objects;

public class RoundedImageView extends ImageView {
    private RectF mRect = new RectF ( );
    private Path mPath = new Path ( );
    private Paint mPaint;
    private float[] mRadiusArray = new float[8];

    PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode (PorterDuff.Mode.SRC_IN);

    public RoundedImageView(Context context) {
        super (context);
        initPaint (context);
        setLayerType (LAYER_TYPE_HARDWARE, null);
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes (attrs, R.styleable.RoundedImageView);
        if (typedArray != null) {
            Arrays.fill (mRadiusArray, typedArray.getDimension (R.styleable.RoundedImageView_radius, 0));
            mRadiusArray[0] = mRadiusArray[1] = typedArray.getDimension (R.styleable.RoundedImageView_bottomLeftRadius, mRadiusArray[0]);
            mRadiusArray[2] = mRadiusArray[3] = typedArray.getDimension (R.styleable.RoundedImageView_bottomRightRadius, mRadiusArray[2]);
            mRadiusArray[4] = mRadiusArray[5] = typedArray.getDimension (R.styleable.RoundedImageView_topLeftRadius, mRadiusArray[4]);
            mRadiusArray[6] = mRadiusArray[7] = typedArray.getDimension (R.styleable.RoundedImageView_topRightRadius, mRadiusArray[6]);
        }
        Objects.requireNonNull (typedArray).recycle ( );
        initPaint (context);
        setLayerType (LAYER_TYPE_HARDWARE, null);
    }


    private void initPaint(Context context) {
        mPaint = new Paint (Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle (Paint.Style.FILL);
        mPaint.setAntiAlias (true);//消除锯齿
    }

    @Override
    public void draw(Canvas canvas) {
        //判断Drawable是否是Bitmap
        if (getDrawable ( ) instanceof BitmapDrawable) {
            //获取当前View尺寸建立Rect
            mRect.set (0, 0, getWidth ( ), getHeight ( ));
            //创建目标Bitmap
            Bitmap target = Bitmap.createBitmap (getWidth ( ), getHeight ( ), Bitmap.Config.ARGB_8888);
            //使用View.draw方法得到经过拉升变换后的bitmap
            super.draw (new Canvas (target));
            //利用目标Bitmap创建设置着色器
            BitmapShader shader = new BitmapShader (target, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader (shader);
            //绘制View形状
            mPath.addRoundRect (mRect, mRadiusArray, Path.Direction.CCW);
            canvas.drawPath (mPath, mPaint);
        } else {
            super.draw (canvas);
        }
    }


    public void setCornerSize(float cornerSize) {
        Arrays.fill (mRadiusArray, cornerSize);
    }
}