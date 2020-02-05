package com.example.crxzy.centertainment.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.example.crxzy.centertainment.tools.Network;

import java.lang.ref.WeakReference;

public class ImageView extends android.support.v7.widget.AppCompatImageView {
    protected Handler mHandler;

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
        Network network = new Network ( );
        Network.Request request = network.InstanceRequest (url);
        request.setSuccess (this, "loadImageSuccess");
        network.send (request);
    }

    public void loadImageSuccess(Network.Response response) {
        Message message = Message.obtain ( );
        message.obj = response;
        message.what = ImageViewHandler.IMAGE_DOWNLOAD_SUCCESS;
        mHandler.sendMessage (message);
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
