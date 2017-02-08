package com.example.administrator.activityloghelper;

import android.app.Application;

/**
 * Created by wangyi on 2017/2/8.
 */

public class LogHelperApplication extends Application {

    private static LogHelperApplication sInstance;

    public LogHelperApplication() {
        sInstance = this;
    }

    public static LogHelperApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
