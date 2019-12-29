package com.example.administrator.activityloghelper.ui;

import android.os.Bundle
import com.airbnb.mvrx.BaseMvRxActivity

import com.example.administrator.activityloghelper.R


class MainActivity : BaseMvRxActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, MainFragment.createMainFragment())
                .commitNow()
        }
    }
}
