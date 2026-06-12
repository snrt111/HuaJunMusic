package com.huajun.music.app;

import android.app.Application;
import android.content.Context;

import com.huajun.music.manager.PlayManager;

public class HuaJunApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        PlayManager.getInstance().init(this);
    }

    public static Context getContext() {
        return sContext;
    }
}
