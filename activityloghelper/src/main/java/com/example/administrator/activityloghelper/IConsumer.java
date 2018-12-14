package com.example.administrator.activityloghelper;

public interface IConsumer<T> {
    void apply(T params);
}
