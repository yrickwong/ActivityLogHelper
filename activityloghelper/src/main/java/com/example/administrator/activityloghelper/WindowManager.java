package com.example.administrator.activityloghelper;

import android.content.Context;

import com.example.administrator.activityloghelper.window.floatview.FloatViewLogic;
import com.example.administrator.activityloghelper.window.IXWindow;

public class WindowManager {

    private static final int TYPE_FLOAT_WINDOW = 1;

    private static IXWindow mFloatWindowLogic;


    static void showWindow(final Context context) {
        if (mFloatWindowLogic == null) {
            mFloatWindowLogic = createWindowView(TYPE_FLOAT_WINDOW);
        }
        mFloatWindowLogic.onCreate(context);
    }

    public static void hideWindow() {
        if (mFloatWindowLogic != null) {
            mFloatWindowLogic.onDestroy();
        }
    }

    static boolean isWindowShowing() {
        return mFloatWindowLogic != null && mFloatWindowLogic.isShowing();
    }

    //factory
    private static IXWindow createWindowView(int type) {
        IXWindow window = null;
        if (type == TYPE_FLOAT_WINDOW) {
            window = new FloatViewLogic();
        }
        return window;
    }
}
