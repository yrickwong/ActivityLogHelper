package com.example.administrator.activityloghelper;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import org.greenrobot.eventbus.EventBus;

public class ViewDebugService extends AccessibilityService {


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        getCurrentActivityName(event);
    }

    private void getCurrentActivityName(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return;
        }
        String currentActivityName = null;
        try {
            String pkgName = event.getPackageName().toString();
            String className = event.getClassName().toString();
            ComponentName componentName = new ComponentName(pkgName, className);
            getPackageManager().getActivityInfo(componentName, 0);
            currentActivityName = componentName.flattenToShortString();
            Log.d("ViewDebugService", "cur=" + currentActivityName);
        } catch (PackageManager.NameNotFoundException e) {
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
        WindowUtils.hideWindow(this);
    }
}
