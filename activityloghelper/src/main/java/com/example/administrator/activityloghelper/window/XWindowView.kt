package com.example.administrator.activityloghelper.window;

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams

import com.example.administrator.activityloghelper.LogHelperApplication
import com.example.administrator.activityloghelper.event.WindowEvent
import com.example.administrator.activityloghelper.window.floatview.FloatWindowView

import org.greenrobot.eventbus.EventBus

/**
 * Created by wangyi on 2017/3/24.
 * 整个floatview的业务逻辑全部在这里来实现
 */

abstract class XWindowView : IXWindow {

    private lateinit var mContentView: View //不需要对mContentView判空了，若为空，直接throw exception
    private var showing = false
    override fun onCreate(context: Context) {
        mContentView = onCreateView(context)
        setContentView(mContentView)
        updateWindowStatus(true)
    }

    companion object {
        private var mPolicy: IWindowPolicy //在构造中初始化的话不用加lateinit

        init {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> mPolicy = object : IWindowPolicy {
                    override fun getWindowManagerParamsType(): Int {
                        return LayoutParams.TYPE_APPLICATION_OVERLAY
                    }
                }
                Build.VERSION.SDK_INT > Build.VERSION_CODES.N -> mPolicy = object : IWindowPolicy {
                    override fun getWindowManagerParamsType(): Int {
                        return LayoutParams.TYPE_PHONE
                    }
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> mPolicy = object : IWindowPolicy {
                    override fun getWindowManagerParamsType(): Int {
                        return LayoutParams.TYPE_TOAST
                    }
                }
                else -> mPolicy = object : IWindowPolicy {
                    override fun getWindowManagerParamsType(): Int {
                        return LayoutParams.TYPE_PHONE
                    }
                }
            }
        }
    }

    private fun setContentView(view: View) {
        val appContext = view.context.applicationContext
        val windowManager: WindowManager = appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val screenWidth = windowManager.defaultDisplay.width
        val screenHeight = windowManager.defaultDisplay.height
        val params = LayoutParams()
        params.type = mPolicy.getWindowManagerParamsType()
        params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL or LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_KEEP_SCREEN_ON or LayoutParams.FLAG_FULLSCREEN
        params.format = PixelFormat.TRANSLUCENT
        params.width = LayoutParams.MATCH_PARENT
        params.height = LayoutParams.WRAP_CONTENT
        params.gravity = Gravity.LEFT or Gravity.TOP //按位或|  用  or  表示
        params.x = screenWidth
        params.y = screenHeight / 2
        if (view is FloatWindowView) {
            view.mParams = params
        }
        view.setOnClickListener {
            onDestroy()
        }
        windowManager.addView(view, params)
    }

    abstract fun onCreateView(context: Context): View

    override fun onDestroy() {
        val appContext = LogHelperApplication.getInstance()
        val windowManager: WindowManager = appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.removeView(mContentView)
        updateWindowStatus(false)
    }


    override fun isShowing(): Boolean {
        return showing
    }

    private fun updateWindowStatus(flag: Boolean) {
        showing = flag
        val event = WindowEvent()
        event.windowstatus = flag
        EventBus.getDefault().post(event)
    }
}
