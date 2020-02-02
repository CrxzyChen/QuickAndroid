package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Network;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

public class RoundedImageView extends android.support.v7.widget.AppCompatImageView {
    private int mCornerSize = 30;
    private Paint mPaint;
    private Handler mHandler;

    public static class RoundedImageViewHandler extends Handler {
        static final int IMAGE_DOWNLOAD_SUCCESS = 100;
        WeakReference <RoundedImageView> outerClass;

        RoundedImageViewHandler(RoundedImageView view) {
            outerClass = new WeakReference <RoundedImageView> (view);
        }

        @Override
        public void handleMessage(Message msg) {
            RoundedImageView roundedImageView = outerClass.get ( );
            if (msg.what == IMAGE_DOWNLOAD_SUCCESS) {
                roundedImageView.setImageBitmap ((Bitmap) ((Network.Response) msg.obj).content);
            }
        }
    }

    public RoundedImageView(Context context) {
        super (context);
        mPaint = new Paint ( );
        mPaint.setColor (context.getColor (R.color.colorBackground));
        mPaint.setAntiAlias (true);//消除锯齿
        mHandler = new RoundedImageViewHandler (this);
    }

    public void setImageURL(String url) {
        Network network = new Network ( );
        Network.Request request = network.InstanceRequest (url);
        request.setSuccess (this, "loadImageSuccess");
        network.send (request);
    }

    public void loadImageSuccess(Network.Response response) {
        Message message = Message.obtain ( );
        message.obj = response;
        message.what = RoundedImageViewHandler.IMAGE_DOWNLOAD_SUCCESS;
        mHandler.sendMessage (message);
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
                mCornerSize * 2), -90, 90);
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