package com.example.administrator.activityloghelper;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

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
        registerActivityLifecycleCallbacks(new HookActivityLifecycleCallbacks());
    }

    /**
     * 反射调用trimMemory()回收纹理内存
     */
    public static void clearTextureCache() {
        new Thread() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Class cls;
                    try {
                        cls = Class.forName("android.view.WindowManagerGlobal");
                        try {
                            Method getInstance = cls.getDeclaredMethod("getInstance", new Class[0]);
                            Object windowManagerGlobal = getInstance.invoke(null, new Object[0]);
                            if (windowManagerGlobal != null) {
                                Log.d("wangyi", "windowManagerGlobal getInstance() called success!");
                                Method trimMemory = cls.getDeclaredMethod("trimMemory", new Class[]{Integer.TYPE});
                                trimMemory.invoke(windowManagerGlobal, new Object[]{Integer.valueOf(80)});
                            }
                            Log.d("wangyi", "trimMemory() called success!");
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        }.start();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d("wangyi", "onTrimMemory: " + level);
    }
}
