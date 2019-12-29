package com.example.administrator.activityloghelper.services;

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import com.example.administrator.activityloghelper.WindowManager


private const val FLAG_VIEW_DEBUG_SERVICE: String = "/.services.ViewDebugService"

/**
 * 获取 ViewDebugService 是否启用状态
 *
 * @return true:serviceEnable
 */
fun isViewDebugServiceEnabled(context: Context): Boolean {
    val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val accessibilityServices =
        accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
    for (info: AccessibilityServiceInfo in accessibilityServices) {
        if (info.id == context.packageName + FLAG_VIEW_DEBUG_SERVICE) {
            return true
        }
    }
    return false
}


class ViewDebugService : AccessibilityService() {

    override fun onInterrupt() {

    }

    private var currentActivityName: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event != null) {
            getCurrentActivityName(event)
        }
    }

    private fun getCurrentActivityName(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }
        try {
            val pkgName = event.packageName.toString()
            val className = event.className.toString()
            val componentName = ComponentName(pkgName, className)
            packageManager.getActivityInfo(componentName, 0)
            currentActivityName = componentName.flattenToShortString()
            Log.d("ViewDebugService", "cur=$currentActivityName")
        } catch (e: PackageManager.NameNotFoundException) {
            //只是窗口
            // 变化，并无activity调转
            Log.d("ViewDebugService", "e=${e.localizedMessage}")
        }
        ViewDebugMonitor.onUpdateMsg(msg = currentActivityName)
    }


    override fun onDestroy() {
        super.onDestroy()
        WindowManager.hideWindow()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
    }

}

interface ViewDebugObserver {
    fun updateCurrentActivityName(msg: String?)
}