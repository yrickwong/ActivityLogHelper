package com.example.administrator.activityloghelper.window;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.example.administrator.activityloghelper.LogHelperApplication;
import com.example.administrator.activityloghelper.event.WindowEvent;
import com.example.administrator.activityloghelper.window.floatview.FloatWindowView;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by wangyi on 2017/3/24.
 * 整个floatview的业务逻辑全部在这里来实现
 */

public abstract class XWindowView implements IXWindow {

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPolicy = new IWindowPolicy() {
                @Override
                public int getWindowManagerParamsType() {
                    return LayoutParams.TYPE_APPLICATION_OVERLAY;
                }
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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

    private static IWindowPolicy mPolicy;

    private boolean showing;

    private View mContentView;


    private void updateWindowStatus(boolean flag) {
        showing = flag;
        WindowEvent event = new WindowEvent();
        event.windowstatus = flag;
        EventBus.getDefault().post(event);
    }

    @Override
    public void onCreate(Context context) {
        mContentView = onCreateView(context);
        if (mContentView == null) {
            throw new IllegalArgumentException("view can not be null!");
        }
        setContentView(mContentView);
        updateWindowStatus(true);
    }

    protected abstract View onCreateView(Context context);

    private void setContentView(View view) {
        Context appContext = view.getContext().getApplicationContext();
        WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
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
        if (view instanceof FloatWindowView) {
            ((FloatWindowView) view).setParams(params);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XWindowView.this.onDestroy();
            }
        });
        windowManager.addView(view, params);
    }

    @Override
    public void onDestroy() {
        Context appContext = LogHelperApplication.getInstance();
        WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.removeView(mContentView);
        }
        updateWindowStatus(false);
    }

    @Override
    public boolean isShowing() {
        return showing;
    }
}
