package com.example.crxzy.centertainment.system;

import android.app.Application;

import com.example.crxzy.centertainment.models.User;
import com.example.crxzy.centertainment.tools.SPUtils;

public class MainApplication extends Application {
    public User mUser;

    @Override
    public void onCreate() {
        super.onCreate ( );
        initUserInfo ( );
    }

    private void initUserInfo() {
        mUser = new User (this );
    }
}
