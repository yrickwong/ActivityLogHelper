package com.example.administrator.activityloghelper.window;

import android.content.Context

/**
 * Created by wangyi on 2017/3/24.
 * 为几个view添加生命周期
 */

interface IXWindow {

    fun onCreate(context: Context)


    fun onDestroy()


    fun isShowing(): Boolean
}
