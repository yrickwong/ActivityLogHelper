package com.example.administrator.activityloghelper;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.activityloghelper.services.ViewDebugService;
import com.example.administrator.activityloghelper.utils.PermissionUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainFragment extends Fragment {

    public static final int REQUEST_CODE = 1;

    private final Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

    private AccessibilityViewModel accessibilityViewModel;


    ViewModelProvider.Factory modelFactory;

    @BindView(R.id.openWindowBtn)
    Button openWindowBtn;

    @BindView(R.id.openServerBtn)
    Button switchPlugin;


    @OnClick({R.id.openWindowBtn, R.id.openServerBtn})
    public void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.openWindowBtn:
                openWindow();
                break;
            case R.id.openServerBtn:
                openServer();
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (!PermissionUtils.checkFloatWindowPermission()) {
                Toast.makeText(getActivity(), R.string.permission_decline, Toast.LENGTH_SHORT).show();
            } else {
                WindowManager.showWindow(getContext());
            }
        }
    }

    /**
     * 引导AccessibilityManager授权
     */
    private void openServer() {
        startActivity(mAccessibleIntent);
    }

    private void openWindow() {
        if (!WindowManager.isWindowShowing()) {
            //是否需要授权?
            if (!PermissionUtils.checkFloatWindowPermission()) {
                PermissionUtils.applyAuthorizePermission(getContext());
            } else {
                WindowManager.showWindow(getContext());
            }
        } else {
            WindowManager.hideWindow();
        }
    }

    public static Fragment createMainFragment() {
        return createMainFragment(null);
    }

    public static Fragment createMainFragment(@Nullable Bundle bundle) {
        MainFragment mainFragment = new MainFragment();
        if (bundle != null) {
            mainFragment.setArguments(bundle);
        }
        return mainFragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        updateServiceStatus();
        updateWindowStatus();
        modelFactory = MainModelFactory.get(getContext());
        accessibilityViewModel = ViewModelProviders.of(this, modelFactory).get(AccessibilityViewModel.class);
        accessibilityViewModel.getChangeLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String showText) {
                switchPlugin.setText(showText);
            }
        });
    }

    /**
     * 更新当前 ViewDebugService 显示状态
     */
    private void updateServiceStatus() {
        switchPlugin.setText(ViewDebugService.isServiceEnabled() ? R.string.service_off : R.string.service_on);
    }


    private void updateWindowStatus() {
        openWindowBtn.setText(WindowManager.isWindowShowing() ? R.string.window_off : R.string.window_on);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateWindow(WindowEvent event) {
        updateWindowStatus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
