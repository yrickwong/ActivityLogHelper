package com.example.administrator.activityloghelper.window.floatview;

import android.content.Context;
import android.view.View;

import com.example.administrator.activityloghelper.window.XWindowView;


/**
 * Created by wangyi on 2017/3/25.
 */

public class FloatViewLogic extends XWindowView {

    @Override
    protected View onCreateView(Context context) {
        View view = new FloatWindowView(context);
        return view;
    }
}
