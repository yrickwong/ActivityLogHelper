package com.example.administrator.activityloghelper.window.floatview;

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.airbnb.mvrx.MvRxView
import com.airbnb.mvrx.fragmentViewModel
import com.example.administrator.activityloghelper.model.AccessibilityViewModel
import com.example.administrator.activityloghelper.services.ViewDebugMonitor
import com.example.administrator.activityloghelper.services.ViewDebugObserver

import com.example.administrator.activityloghelper.window.XWindowView


/**
 * Created by wangyi on 2017/3/25.
 */

class FloatViewLogic : XWindowView(), ViewDebugObserver {

    private lateinit var floatWindowView: FloatWindowView

    private val accessibilityViewModel: AccessibilityViewModel by lazy {
        ViewModelProviders.of(getContext() as FragmentActivity)
            .get(AccessibilityViewModel::class.java)
    }

    override fun onCreate(context: Context) {
        super.onCreate(context)
        ViewDebugMonitor.addObserver(this)
        accessibilityViewModel.updateWindowStatus(isShowing())
    }

    override fun onDestroy() {
        super.onDestroy()
        ViewDebugMonitor.removeObserver(this)
        accessibilityViewModel.updateWindowStatus(isShowing())
    }


    override fun onCreateView(context: Context): View {
        floatWindowView = FloatWindowView(context)
        return floatWindowView
    }

    override fun updateCurrentActivityName(msg: String?) {
        floatWindowView.text = msg
    }

}
