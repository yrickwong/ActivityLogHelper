package com.example.administrator.activityloghelper.model;

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.example.administrator.activityloghelper.LogHelperApplication
import com.example.administrator.activityloghelper.MvRxViewModel
import com.example.administrator.activityloghelper.R

data class AccessibilityState(val status: String = "", val windowStatus: String = "") :
    MvRxState

class AccessibilityViewModel(
    accessibilityState: AccessibilityState,
    private val appContext: Context
) : MvRxViewModel<AccessibilityState>(accessibilityState),
    AccessibilityManager.AccessibilityStateChangeListener {


    init {
        //监听AccessibilityService 变化
        val accessibilityManager =
            appContext.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        accessibilityManager.addAccessibilityStateChangeListener(this)
    }


    override fun onCleared() {
        super.onCleared()
        //监听AccessibilityService 变化
        val accessibilityManager =
            appContext.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        accessibilityManager.removeAccessibilityStateChangeListener(this)
    }

    override fun onAccessibilityStateChanged(enabled: Boolean) {
        setState {
            val result: String = if (enabled) {
                appContext.getString(R.string.service_off)
            } else {
                appContext.getString(R.string.service_on)
            }
            copy(status = result)
        }
    }

    fun updateWindowStatus(isShowing: Boolean) {
        setState {
            val text: String = if (isShowing) {
                appContext.getString(R.string.window_off)
            } else {
                appContext.getString(R.string.window_on)
            }
            copy(windowStatus = text)
        }
    }

    companion object : MvRxViewModelFactory<AccessibilityViewModel, AccessibilityState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: AccessibilityState
        ): AccessibilityViewModel? {
            val app = viewModelContext.app<LogHelperApplication>()
            return AccessibilityViewModel(state, app)
        }

    }
}
