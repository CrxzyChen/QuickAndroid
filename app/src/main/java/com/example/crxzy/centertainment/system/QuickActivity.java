package com.example.crxzy.centertainment.system;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public abstract class QuickActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        onInitiation();
    }

    abstract public void onInitiation();

    public static abstract class ActivityHandler extends Handler {
        protected WeakReference <QuickActivity> mOuterClass;

        public ActivityHandler(QuickActivity activity) {
            mOuterClass = new WeakReference <> (activity);
        }

        @Override
        abstract public void handleMessage(Message msg);
    }
}
