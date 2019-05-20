package com.example.administrator.activityloghelper.window.floatview;

import android.annotation.SuppressLint
import android.content.Context;
import android.text.method.Touch.onTouchEvent
import android.view.*
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.activityloghelper.LogHelperApplication;
import com.example.administrator.activityloghelper.event.MessageEvent;
import com.example.administrator.activityloghelper.R;
import com.example.administrator.activityloghelper.services.ViewDebugService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;

/**
 * view不应该有业务逻辑
 */
class FloatWindowView(context: Context) : LinearLayout(context) {

    private var yInView: Float = 0f
    private var xInView: Float = 0f
    private var isDragging: Boolean = false
    private var yInScreen: Float = 0f
    /**
     * 用于更新小悬浮窗的位置
     */
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager


    /**
     * 记录系统状态栏的高度
     */
    private var statusBarHeight: Int = 0

    private var mTouchSlop: Int

    private var debugView: View

    init {
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this)
        debugView = findViewById(R.id.float_textview)
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //        //获取到手指处的横坐标和纵坐标
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = false//重置状态
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                yInView = event.y
                xInView = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (event.x - xInView).toInt()
                val dy = (event.y - yInView).toInt()
                if (checkTouchSlop(dx, dy)) {
                    isDragging = true//正在移动ing
                }
                yInScreen = event.rawY - getStatusBarHeight()
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition()
            }
            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    //说明是拖拽中，放手的时候不让onClick执行
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun updateViewPosition() {

    }

    @SuppressLint("PrivateApi")
    private fun getStatusBarHeight(): Int {
        if (statusBarHeight == 0) {
            try {
                val c = Class.forName("com.android.internal.R\$dimen")
                val o = c.newInstance()
                val field = c.getField("status_bar_height")
                val x = field.get(o) as Int
                statusBarHeight = resources.getDimensionPixelSize(x)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return statusBarHeight
    }

    private fun checkTouchSlop(dx: Int, dy: Int): Boolean {
        return dx * dx + dy * dy > mTouchSlop * mTouchSlop
    }

    //    /**
//     * 记录系统状态栏的高度
//     */
//    private int statusBarHeight;
//
//
//    /**
//     * 用于更新小悬浮窗的位置
//     */
//    private final WindowManager windowManager;
//
//
//    /**
//     * 小悬浮窗的参数
//     */
//    private WindowManager.LayoutParams mParams;
//
//
//    /**
//     * 记录当前手指位置在屏幕上的纵坐标值
//     */
//    private float yInScreen;
//
//
//    /**
//     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
//     */
//    private float yInView;
//
//    private final TextView debugView;
//
//    private boolean isShowing = false;
//
//    private Position currentPosition = new Position();
//
//    private final int mTouchSlop;
//    private float xInView;
//
//    constructor (context Context)
//    {
//        super(context);
//        windowManager = (WindowManager) context . getSystemService (Context.WINDOW_SERVICE);
//        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
//        debugView = (TextView) findViewById (R.id.float_textview);
//        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
//    }
//
//    /**
//     * debugView是否在拖拽中
//     */
//    private boolean isDragging = false;
//
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event)
//    {
//        //获取到手指处的横坐标和纵坐标
//        int x =(int) event . getX ();
//        int y =(int) event . getY ();
//        switch(event.getAction()) {
//            case MotionEvent . ACTION_DOWN :
//            isDragging = false;//重置状态
//            // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
//            yInView = event.getY();
//            xInView = event.getX();
//            break;
//            case MotionEvent . ACTION_MOVE :
//            int dx =(int)(event.getX() - xInView);
//            int dy =(int)(event.getY() - yInView);
//            if (checkTouchSlop(dx, dy)) {
//                isDragging = true;//正在移动ing
//            }
//            yInScreen = event.getRawY() - getStatusBarHeight();
//            // 手指移动的时候更新小悬浮窗的位置
//            updateViewPosition();
//            break;
//            case MotionEvent . ACTION_UP :
//            if (isDragging) {//说明是拖拽中，放手的时候不让onClick执行
//                return true;
//            }
//            break;
//            default:
//            break;
//        }
//        return super.onTouchEvent(event);
//    }
//
//    /**
//     * 表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件
//     *
//     * @param dx
//     * @param dy
//     * @return
//     */
//    public boolean checkTouchSlop(int dx, int dy)
//    {
//        return dx * dx + dy * dy > mTouchSlop * mTouchSlop;
//    }

//
//
//    /**
//     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
//     *
//     * @param params 小悬浮窗的参数
//     */
//    public void setParams(WindowManager.LayoutParams params)
//    {
//        mParams = params;
//    }
//
//    /**
//     * 更新小悬浮窗在屏幕中的位置。
//     */
//    private void updateViewPosition()
//    {
//        mParams.y = (int)(yInScreen - yInView);
//        setCurrentPosition();
//        windowManager.updateViewLayout(this, mParams);
//    }
//
//    public Position setCurrentPosition()
//    {
//        currentPosition.X = mParams.x;
//        currentPosition.Y = mParams.y;
//        return currentPosition;
//    }
//
//    public Position getCurrentPosition()
//    {
//        return currentPosition;
//    }
//
//    public static
//    class Position {
//        int X;
//        int Y;
//    }
//
//    /**
//     * 用于获取状态栏的高度。
//     *
//     * @return 返回状态栏高度的像素值。
//     */
//    private int getStatusBarHeight()
//    {
//        if (statusBarHeight == 0) {
//            try {
//                Class<?> c = Class . forName ("com.android.internal.R$dimen");
//                Object o = c . newInstance ();
//                Field field = c . getField ("status_bar_height");
//                int x =(Integer) field . get (o);
//                statusBarHeight = getResources().getDimensionPixelSize(x);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return statusBarHeight;
//    }
//
//
//    @Override
//    protected void onAttachedToWindow()
//    {
//        super.onAttachedToWindow();
//        EventBus.getDefault().register(this);
//        if (ViewDebugService.isServiceEnabled()) {
//            MessageEvent msg = new MessageEvent();
//            msg.info = LogHelperApplication.getInstance().getPackageName() + "/.MainActivity";
//            EventBus.getDefault().post(msg);
//        }
//    }
//
//    @Override
//    protected void onDetachedFromWindow()
//    {
//        super.onDetachedFromWindow();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onWindowChanged(MessageEvent msg)
//    {
//        if (debugView != null) {
//            debugView.setText(msg.info);
//        }
//    }
//
//    public void setShowing(boolean flag)
//    {
//        isShowing = flag;
//    }
//
//    public boolean isShowing()
//    {
//        return isShowing;
//    }
}
