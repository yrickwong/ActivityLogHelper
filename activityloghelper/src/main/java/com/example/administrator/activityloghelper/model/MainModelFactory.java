package com.example.administrator.activityloghelper.model;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

public class MainModelFactory implements ViewModelProvider.Factory {

    private final Context mContext;

    private MainModelFactory(Context context) {
        mContext = context;
    }

    public static ViewModelProvider.Factory get(Context context) {
        return new MainModelFactory(context);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AccessibilityViewModel.class)) {
            return (T) new AccessibilityViewModel(mContext);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
