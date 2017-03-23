package com.example.administrator.activityloghelper;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.activityloghelper.utils.PermissionUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AccessibilityManager.AccessibilityStateChangeListener {

    private final Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

    private static final String TAG = "MainActivity";

    public static final int REQUEST_CODE = 1;

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
            //是否需要授权?
            if (!PermissionUtils.checkFloatWindowPermission()) {
                PermissionUtils.applyAuthorizePermission(this);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (!PermissionUtils.checkFloatWindowPermission()) {
                Toast.makeText(this, R.string.permission_decline, Toast.LENGTH_SHORT).show();
            } else {
                WindowUtils.showWindow(this);
            }
        }
    }

    @Override
    public void onAccessibilityStateChanged(boolean enabled) {
        Log.d("wangyi","en="+enabled);
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
            Log.d("wangyi","info="+info.getId());
            if (info.getId().equals(getPackageName() + "/.services.ViewDebugService")) {
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
