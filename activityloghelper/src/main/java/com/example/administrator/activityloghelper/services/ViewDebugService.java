package com.example.administrator.activityloghelper.services;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.example.administrator.activityloghelper.MessageEvent;
import com.example.administrator.activityloghelper.WindowUtils;

import org.greenrobot.eventbus.EventBus;

public class ViewDebugService extends AccessibilityService {

    private String currentActivityName;
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        getCurrentActivityName(event);
    }

    private void getCurrentActivityName(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return;
        }
        try {
            String pkgName = event.getPackageName().toString();
            String className = event.getClassName().toString();
            ComponentName componentName = new ComponentName(pkgName, className);
            getPackageManager().getActivityInfo(componentName, 0);
            currentActivityName = componentName.flattenToShortString();
            Log.d("ViewDebugService", "cur=" + currentActivityName);
        } catch (PackageManager.NameNotFoundException e) {
            //只是窗口变化，并无activity调转
            Log.d("ViewDebugService", "e=" + e.getLocalizedMessage());
        }
        MessageEvent msg = new MessageEvent();
        msg.info = currentActivityName;
        EventBus.getDefault().post(msg);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WindowUtils.hideWindow();
    }
}
