package com.example.administrator.activityloghelper;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import org.greenrobot.eventbus.EventBus;

public class WindowUtils {

    private static FloatWindowView mFloatWindow;

    private static IWindowPolicy mPolicy;

    static {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            mPolicy = new IWindowPolicy() {
                @Override
                public int getWindowManagerParamsType() {
                    return WindowManager.LayoutParams.TYPE_PHONE;
                }
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mPolicy = new IWindowPolicy() {
                @Override
                public int getWindowManagerParamsType() {
                    return LayoutParams.TYPE_TOAST;
                }
            };
        } else {
            mPolicy = new IWindowPolicy() {
                @Override
                public int getWindowManagerParamsType() {
                    return WindowManager.LayoutParams.TYPE_PHONE;
                }
            };
        }
    }

    @SuppressWarnings("WrongConstant")
    public static void showWindow(final Context context) {
        Context appContext = context.getApplicationContext();
        WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        View mView = setUpView(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = mPolicy.getWindowManagerParamsType();
        params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_FULLSCREEN;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = screenWidth;
        params.y = screenHeight / 2;//初始化位置在屏幕中心
        mFloatWindow.setParams(params);
        mFloatWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideWindow();
            }
        });
        windowManager.addView(mView, params);
        updateWindowStatus(true);
    }

    private static void updateWindowStatus(boolean flag) {
        mFloatWindow.setShowing(flag);
        WindowEvent event = new WindowEvent();
        event.windowstatus = flag;
        EventBus.getDefault().post(event);
    }

    public static void hideWindow(Context context) {
        Context appContext = context.getApplicationContext();
        WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.removeView(mFloatWindow);
        updateWindowStatus(false);
    }

    public static void hideWindow() {
        Context appContext = LogHelperApplication.getInstance();
        WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.removeView(mFloatWindow);
        updateWindowStatus(false);
    }


    public static View setUpView(Context context) {
        if (mFloatWindow == null) {
            mFloatWindow = new FloatWindowView(context);
        }
        return mFloatWindow;
    }

    public static boolean isWindowShowing() {
        if (mFloatWindow == null) {
            return false;
        }
        return mFloatWindow.isShowing();
    }

}
