package com.example.administrator.activityloghelper;

import android.content.Context
import com.example.administrator.activityloghelper.window.IXWindow
import com.example.administrator.activityloghelper.window.floatview.FloatViewLogic


object WindowManager {

    private val mFloatWindowLogic: IXWindow by lazy {
        FloatViewLogic()
    }


    fun showWindow(context: Context) {
        mFloatWindowLogic.onCreate(context)
    }

    fun hideWindow() {
        mFloatWindowLogic.onDestroy()
    }

    fun isWindowShowing(): Boolean {
        return mFloatWindowLogic.isShowing() //等同于上面的代码,相当简化
    }


}
