package com.example.administrator.activityloghelper.model;

import android.annotation.SuppressLint
import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.lifecycle.*
import com.example.administrator.activityloghelper.R


class AccessibilityViewModel(context: Context) : ViewModel(), LifecycleObserver,
    AccessibilityManager.AccessibilityStateChangeListener {
    @SuppressLint("StaticFieldLeak")
    private val appContext: Context = context.applicationContext

    val changedLiveData: MutableLiveData<String> = MutableLiveData()


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        //监听AccessibilityService 变化
        val accessibilityManager = appContext.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        accessibilityManager.addAccessibilityStateChangeListener(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        //监听AccessibilityService 变化
        val accessibilityManager = appContext.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        accessibilityManager.removeAccessibilityStateChangeListener(this)
    }

    override fun onAccessibilityStateChanged(enabled: Boolean) {
        val result: String? = if (enabled) {
            appContext.getString(R.string.service_off)
        } else {
            appContext.getString(R.string.service_on)
        }
        changedLiveData.value = result
    }

}
