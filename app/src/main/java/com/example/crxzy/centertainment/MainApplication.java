package com.example.crxzy.centertainment;

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
        SPUtils.clear (this);
        SPUtils.put (this, "uid", 1);
        SPUtils.put (this, "username", "admin");
        mUser = new User ( );
        String username = (String) SPUtils.get (this, "username", "");
        int uid = (int) SPUtils.get (this, "uid", -1);
        assert username != null;
        if (username.equals ("")) {
            mUser.mUsername = "unknown";
        }
        mUser.mUid = uid;
    }
}
