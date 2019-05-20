package com.example.administrator.activityloghelper

interface IConsumer<in T> {
    fun apply(params: T)
}