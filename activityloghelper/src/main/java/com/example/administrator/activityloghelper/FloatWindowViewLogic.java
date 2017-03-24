package com.example.administrator.activityloghelper;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by wangyi on 2017/3/24.
 * 整个floatview的业务逻辑全部在这里来实现
 */

public class FloatWindowViewLogic implements IFloatWindowLifecycle {

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

    private FloatWindowView mFloatWindow;

    private boolean showing;

    private View setUpView(Context context) {
        mFloatWindow = new FloatWindowView(context);
        return mFloatWindow;
    }

    private void updateWindowStatus(boolean flag) {
        showing = flag;
        WindowEvent event = new WindowEvent();
        event.windowstatus = flag;
        EventBus.getDefault().post(event);
    }

    private int getLastFloatViewPositionY() {
        return mFloatWindow.getCurrentPosition().Y;
    }

    @Override
    public void onCreate(Context context) {
        Context appContext = context.getApplicationContext();
        WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        View mView = setUpView(appContext);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = mPolicy.getWindowManagerParamsType();
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_FULLSCREEN;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = screenWidth;
        params.y = screenHeight / 2;//初始化位置在屏幕中心
        if (getLastFloatViewPositionY() != 0) {
            params.y = getLastFloatViewPositionY();
        }
        mFloatWindow.setParams(params);
        mFloatWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatWindowViewLogic.this.onDestroy();
            }
        });
        windowManager.addView(mView, params);
        updateWindowStatus(true);
    }

    @Override
    public void onDestroy() {
        Context appContext = LogHelperApplication.getInstance();
        WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.removeView(mFloatWindow);
        updateWindowStatus(false);
    }

    @Override
    public boolean isShowing() {
        return showing;
    }
}
