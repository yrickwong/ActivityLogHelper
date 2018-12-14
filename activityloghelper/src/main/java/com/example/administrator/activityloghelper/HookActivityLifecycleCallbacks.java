package com.example.administrator.activityloghelper;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class HookActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "wangyi";

    private String currentActivity;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            fragmentActivity.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new HookFragmentLifecycleCallbacks(), true);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity.getClass().getCanonicalName();
        Log.d(TAG, String.format("onActivityCreated: currentActivity=%s", currentActivity));
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
