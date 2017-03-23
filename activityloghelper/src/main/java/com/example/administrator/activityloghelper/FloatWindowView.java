package com.example.administrator.activityloghelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;

public class FloatWindowView extends LinearLayout {


    /**
     * 记录系统状态栏的高度
     */
    private int statusBarHeight;


    /**
     * 用于更新小悬浮窗的位置
     */
    private final WindowManager windowManager;


    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;


    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;


    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;

    private final TextView debugView;

    private boolean isShowing = false;

    private Position currentPosition = new Position();

    public FloatWindowView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
        debugView = (TextView) findViewById(R.id.float_textview);
    }

    /**
     * debugView是否在拖拽中
     */
    private boolean isDragging = false;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDragging = false;//重置状态
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                yInView = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                isDragging = true;//正在移动ing
                yInScreen = event.getRawY() - getStatusBarHeight();
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                if (isDragging) {//说明是拖拽中，放手的时候不让onClick执行
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.y = (int) (yInScreen - yInView);
        setCurrentPosition();
        windowManager.updateViewLayout(this, mParams);
    }

    public Position setCurrentPosition() {
        currentPosition.X = mParams.x;
        currentPosition.Y = mParams.y;
        return currentPosition;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public static class Position {
        int X;
        int Y;
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWindowChanged(MessageEvent msg) {
        if (debugView != null) {
            debugView.setText(msg.info);
        }
    }

    public void setShowing(boolean flag) {
        isShowing = flag;
    }

    public boolean isShowing() {
        return isShowing;
    }
}
