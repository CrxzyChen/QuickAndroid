package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.tools.Network;

import java.lang.ref.WeakReference;

public class ImageView extends android.support.v7.widget.AppCompatImageView {
    protected Handler mHandler;
    protected String mImageUrl;
    protected boolean mImageIsLoaded = false;

    public ImageView(Context context) {
        super (context);
        mHandler = new ImageViewHandler (this);
    }

    public ImageView(Context context, AttributeSet attrs) {
        super (context, attrs);
        mHandler = new ImageViewHandler (this);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        mHandler = new ImageViewHandler (this);
    }

    public void setImageURL(String url) {
        mImageUrl = url;
        load ( );
    }

    public void setImageURL(String url, boolean isLoad) {
        if (!isLoad) {
            mImageUrl = url;
        } else {
            load ( );
        }
    }

    public void loadImageSuccess(Network.Response response) {
        Message message = Message.obtain ( );
        message.obj = response;
        message.what = ImageViewHandler.IMAGE_DOWNLOAD_SUCCESS;
        mHandler.sendMessage (message);
    }

    public void load() {
        if (!mImageIsLoaded) {
            Network network = new Network ( );
            Network.Request request = network.InstanceRequest (mImageUrl);
            request.setSuccess (this, "loadImageSuccess");
            network.send (request);
            mImageIsLoaded = true;
        }
    }

    public boolean release() {
        Drawable drawable = this.getDrawable ( );
        if (drawable instanceof BitmapDrawable) {
            Bitmap bmp = ((BitmapDrawable) drawable).getBitmap ( );
            if (bmp != null && !bmp.isRecycled ( )) {
                this.setImageBitmap (null);
                bmp.recycle ( );
                mImageIsLoaded = false;
                return true;
            }
        }
        return false;
    }

    public static class ImageViewHandler extends Handler {
        static final int IMAGE_DOWNLOAD_SUCCESS = 100;
        WeakReference <ImageView> outerClass;

        ImageViewHandler(ImageView view) {
            outerClass = new WeakReference <ImageView> (view);
        }

        @Override
        public void handleMessage(Message msg) {
            ImageView ImageView = outerClass.get ( );
            if (msg.what == IMAGE_DOWNLOAD_SUCCESS) {
                ImageView.setImageBitmap ((Bitmap) ((Network.Response) msg.obj).content);
            }
        }
    }

}
