package com.example.administrator.activityloghelper.services

object ViewDebugMonitor {

    private val mObservers = mutableListOf<ViewDebugObserver>()

    @Synchronized
    fun addObserver(observer: ViewDebugObserver) {
        mObservers.add(observer)
    }

    @Synchronized
    fun removeObserver(observer: ViewDebugObserver) {
        mObservers.remove(observer)
    }

    @Synchronized
    fun onUpdateMsg(msg: String? = null) {
        mObservers.forEach {
            it.updateCurrentActivityName(msg)
        }
    }
}