package com.example.administrator.activityloghelper

//in and out 在kotlin里是泛型的一种
interface IConsumer<in T> {
    fun apply(params: T?)
}