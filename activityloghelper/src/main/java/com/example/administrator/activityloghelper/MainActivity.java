package com.example.administrator.activityloghelper;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AccessibilityManager.AccessibilityStateChangeListener {

    private final Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE = 1;

    //AccessibilityService 管理
    private AccessibilityManager accessibilityManager;

    @Bind(R.id.openWindowBtn)
    Button openWindowBtn;

    @Bind(R.id.openServerBtn)
    Button switchPlugin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        openWindowBtn.setOnClickListener(this);
        switchPlugin.setOnClickListener(this);
        //监听AccessibilityService 变化
        accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(this);
        updateServiceStatus();
        updateWindowStatus();
        EventBus.getDefault().register(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openServerBtn:
                openServer();
                break;
            case R.id.openWindowBtn:
                openWindow();
                break;
        }
    }

    /**
     * 引导AccessibilityManager授权
     */
    private void openServer() {
        startActivity(mAccessibleIntent);
    }

    private void openWindow() {
        if (!WindowUtils.isWindowShowing()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                checkNeedRequestPermission();
            } else {
                WindowUtils.showWindow(this);
            }
        } else {
            WindowUtils.hideWindow(this);
        }
    }

    private void updateWindowStatus() {
        if (WindowUtils.isWindowShowing()) {
            openWindowBtn.setText(R.string.window_off);
        } else {
            openWindowBtn.setText(R.string.window_on);
        }
    }

    @SuppressLint("NewApi")
    private void checkNeedRequestPermission() {
        //未授权，引导
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            //已经授权，直接show
            WindowUtils.showWindow(this);
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                WindowUtils.showWindow(this);
            } else {
                Toast.makeText(this, R.string.permission_decline, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAccessibilityStateChanged(boolean enabled) {
        updateServiceStatus();
    }

    /**
     * 更新当前 ViewDebugService 显示状态
     */
    private void updateServiceStatus() {
        if (isServiceEnabled()) {
            switchPlugin.setText(R.string.service_off);
        } else {
            switchPlugin.setText(R.string.service_on);
        }
    }

    /**
     * 获取 ViewDebugService 是否启用状态
     *
     * @return true:serviceEnable
     */
    private boolean isServiceEnabled() {
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.ViewDebugService")) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        //移除监听服务
        accessibilityManager.removeAccessibilityStateChangeListener(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateWindowStatus(WindowEvent event) {
        updateWindowStatus();
    }
}
