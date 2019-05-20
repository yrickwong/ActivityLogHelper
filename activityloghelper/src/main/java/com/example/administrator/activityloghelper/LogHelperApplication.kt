package com.example.administrator.activityloghelper

import android.app.Application
import android.content.Context
import android.util.Log

/**
 * Created by wangyi on 2017/2/8.
 *
 */
class LogHelperApplication : Application() {

    //在默认构造函数进行赋值
    init {
        instance = this
    }

    companion object {

        private lateinit var instance: LogHelperApplication

        @JvmStatic
        fun getInstance(): Context {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
    }


    //kotlin 不需要返回void
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.d("wangyi", "onTrimMemory: " + level)
    }


}
