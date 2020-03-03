package com.example.crxzy.centertainment.models;

import android.app.Application;
import android.content.Context;

import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.tools.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class User {
    private boolean mIsLogin;
    private Application mApplication;
    public String username;
    public int uid;
    public boolean isUserConfigLoaded = false;
    private Map <String, Object> config = new HashMap <> ( );

    public User(Application application) {
        mApplication = application;
        username = (String) SPUtils.get (application, "username", "unknown");
        uid = (int) SPUtils.get (application, "uid", -1);
        if (uid != -1) {
            NetApi.getUserConfig (uid, this, "getUserConfigSuccess");
        }
    }

    public void getUserConfigSuccess(Network.Response response) {
        JSONObject obj = (JSONObject) response.content;
        try {
            for (Iterator <String> it = obj.keys ( ); it.hasNext ( ); ) {
                String key = it.next ( );
                config.put (key, obj.get (key));
            }
        } catch (JSONException e) {
            e.printStackTrace ( );
        }
        isUserConfigLoaded = true;
    }

    public void putConfig(String key, Object object) {
        config.put (key, object);
        NetApi.setUserConfig (uid, key, object.toString ( ));
    }

    public Object getConfig(String key) {
        return config.get (key);
//        if (config.containsKey (key)) {
//            return config.get (key);
//        } else {
//            return null;
//        }
    }

    public Object getConfig(String key, boolean isForce) {
        return config.get (key);
    }

    public boolean login() {
        SPUtils.clear (mApplication);
        SPUtils.put (mApplication, "uid", 1);
        SPUtils.put (mApplication, "username", "admin");
        return false;
    }

    public boolean logout() {
        return false;
    }

    public boolean register() {
        return false;
    }
}
