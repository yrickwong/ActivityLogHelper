package com.example.administrator.activityloghelper.window.floatview;

import android.content.Context
import android.view.View

import com.example.administrator.activityloghelper.window.XWindowView


/**
 * Created by wangyi on 2017/3/25.
 */

class FloatViewLogic : XWindowView() {

    override fun onCreateView(context: Context): View {
        return FloatWindowView(context)
    }
}
