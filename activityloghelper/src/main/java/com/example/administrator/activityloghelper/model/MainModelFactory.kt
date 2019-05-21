package com.example.administrator.activityloghelper.model;

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccessibilityViewModel::class.java)) {
            return AccessibilityViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @JvmStatic
        fun get(context: Context): ViewModelProvider.Factory {
            return MainModelFactory(context)
        }
    }

}
