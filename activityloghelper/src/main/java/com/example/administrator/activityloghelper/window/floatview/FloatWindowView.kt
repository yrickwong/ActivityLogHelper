package com.example.administrator.activityloghelper.window.floatview;

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.example.administrator.activityloghelper.R
import com.example.administrator.activityloghelper.services.ViewDebugMonitor
import com.example.administrator.activityloghelper.services.isViewDebugServiceEnabled

/**
 * view不应该有业务逻辑
 */
class FloatWindowView(context: Context) : LinearLayout(context) {

    private var yInView: Float = 0f
    private var xInView: Float = 0f

    private var yInScreen: Float = 0f

    private var currentPosition = Position()

    /**
     * 用于更新小悬浮窗的位置
     */
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager


    /**
     * 小悬浮窗的参数
     */
    lateinit var mParams: WindowManager.LayoutParams

    /**
     * debugView是否在拖拽中
     *
     */
    private var isDragging: Boolean = false

    /**
     * 记录系统状态栏的高度
     */
    private var statusBarHeight: Int = 0  //lateinit不能用于基础数据类型

    private var mTouchSlop: Int  //默认构造代码块里面有初始化，所以不需要lateinit 或赋值

    private var debugView: TextView

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


    /**
     * 表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件
     *
     * @param dx
     * @param dy
     * @return
     */
    private fun checkTouchSlop(dx: Int, dy: Int): Boolean {
        return dx * dx + dy * dy > mTouchSlop * mTouchSlop
    }


    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private fun updateViewPosition() {
        mParams.y = (yInScreen - yInView).toInt()
        setCurrentPosition()
        windowManager.updateViewLayout(this, mParams)
    }

    private fun setCurrentPosition(): Position {
        currentPosition.x = mParams.x
        currentPosition.y = mParams.y
        return currentPosition
    }

    fun getCurrentPosition(): Position {
        return currentPosition;
    }

    //
    inner class Position {
        var x: Int = 0
        var y: Int = 0
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isViewDebugServiceEnabled(context)) {
            ViewDebugMonitor.onUpdateMsg(context.packageName + "/.MainActivity")
        }
    }


    var text: String? = null
        set(value) {
            debugView.text = value
            field = value
        }

}
