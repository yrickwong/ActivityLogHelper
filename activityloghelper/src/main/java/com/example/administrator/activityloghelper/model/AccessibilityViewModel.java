package com.example.administrator.activityloghelper.model;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import com.example.administrator.activityloghelper.R;


public class AccessibilityViewModel extends ViewModel implements LifecycleObserver, AccessibilityManager.AccessibilityStateChangeListener {

    private Context applicationContext;

    private MutableLiveData<String> mutableLiveData = new MutableLiveData<>();


    public AccessibilityViewModel(Context context) {
        applicationContext = context.getApplicationContext();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        //监听AccessibilityService 变化
        AccessibilityManager accessibilityManager = (AccessibilityManager) applicationContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager != null) {
            accessibilityManager.addAccessibilityStateChangeListener(this);
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        //移除监听服务
        AccessibilityManager accessibilityManager = (AccessibilityManager) applicationContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager != null) {
            accessibilityManager.removeAccessibilityStateChangeListener(this);
        }
    }

    private static final String TAG = "wangyi";

    @Override
    public void onAccessibilityStateChanged(boolean enabled) {
        Log.d(TAG, "onAccessibilityStateChanged: ");
        mutableLiveData.setValue(applicationContext.getString(enabled ? R.string.service_off : R.string.service_on));
    }

    public LiveData<String> getChangeLiveData() {
        return mutableLiveData;
    }
}
