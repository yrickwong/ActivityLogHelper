package com.example.administrator.activityloghelper;

import android.content.Context;

public class WindowUtils {

    private static IFloatWindowLifecycle mFloatWindowLogic;


    public static void showWindow(final Context context) {
        if (mFloatWindowLogic == null) {
            mFloatWindowLogic = new FloatWindowViewLogic();
        }
        mFloatWindowLogic.onCreate(context);
    }

    public static void hideWindow() {
        if (mFloatWindowLogic != null) {
            mFloatWindowLogic.onDestroy();
        }
    }

    public static boolean isWindowShowing() {
        if (mFloatWindowLogic == null) {
            return false;
        }
        return mFloatWindowLogic.isShowing();
    }

}
