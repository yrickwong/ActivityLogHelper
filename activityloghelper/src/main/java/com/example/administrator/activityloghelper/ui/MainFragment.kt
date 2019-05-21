package com.example.administrator.activityloghelper.ui;

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.administrator.activityloghelper.IConsumer
import com.example.administrator.activityloghelper.R
import com.example.administrator.activityloghelper.WindowManager
import com.example.administrator.activityloghelper.event.WindowEvent
import com.example.administrator.activityloghelper.model.AccessibilityViewModel
import com.example.administrator.activityloghelper.model.MainModelFactory
import com.example.administrator.activityloghelper.services.ViewDebugService
import com.example.administrator.activityloghelper.utils.PermissionUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainFragment : Fragment() {

    private val mAccessibleIntent: Intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)

    private lateinit var accessibilityViewModel: AccessibilityViewModel

    private lateinit var modelFactory: ViewModelProvider.Factory

    @BindView(R.id.openWindowBtn)
    lateinit var openWindowBtn: Button

    @BindView(R.id.openServerBtn)
    lateinit var switchPlugin: Button


    @OnClick(R.id.openWindowBtn, R.id.openServerBtn)
    fun onBtnClick(view: View) {
        when (view.id) {
            R.id.openWindowBtn -> openWindow()
            R.id.openServerBtn -> openServer()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PermissionUtils.PERMISSION_CODE) {
            if (!PermissionUtils.checkFloatWindowPermission(context!!)) {
                Toast.makeText(activity, R.string.permission_decline, Toast.LENGTH_SHORT).show()
            } else {
                context?.let { WindowManager.showWindow(it) }
            }
        }
    }

    /**
     * 引导AccessibilityManager授权
     */
    private fun openServer() {
        startActivity(mAccessibleIntent)
    }

    private fun openWindow() {
        if (!WindowManager.isWindowShowing()) {
            //是否需要授权?
            if (!PermissionUtils.checkFloatWindowPermission(context!!)) {
                PermissionUtils.applyAuthorizePermission(context!!, object : IConsumer<Intent> {

                    override fun apply(params: Intent?) {
                        startActivityForResult(params, PermissionUtils.PERMISSION_CODE)
                    }

                })
            } else {
                activity?.let { WindowManager.showWindow(it) }
            }
        } else {
            WindowManager.hideWindow()
        }
    }


    companion object {
        @JvmStatic
        fun createMainFragment(): Fragment {
            return createMainFragment(null)
        }

        @JvmStatic
        fun createMainFragment(bundle: Bundle?): Fragment {
            val mainFragment = MainFragment()
            bundle?.let { mainFragment.arguments = bundle }
            return mainFragment
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_main, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
        EventBus.getDefault().register(this)
        updateServiceStatus()
        updateWindowStatus()
        modelFactory = MainModelFactory.get(view.context)
        accessibilityViewModel = ViewModelProviders.of(this, modelFactory).get(AccessibilityViewModel::class.java)
        accessibilityViewModel.changedLiveData.observeForever(accessibilityStatus)
        lifecycle.addObserver(accessibilityViewModel)
    }

    var accessibilityStatus: Observer<String> =
        Observer { showText -> switchPlugin.text = showText }


    /**
     * 更新当前 ViewDebugService 显示状态
     */
    private fun updateServiceStatus() {
        val text: String = if (ViewDebugService.isServiceEnabled()) {
            getString(R.string.service_off)
        } else {
            getString(R.string.service_on)
        }
        switchPlugin.text = text
    }


    private fun updateWindowStatus() {
        val text: String = if (WindowManager.isWindowShowing()) {
            getString(R.string.window_off)
        } else {
            getString(R.string.window_on)
        }
        openWindowBtn.text = text
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateWindow(event: WindowEvent) {
        updateWindowStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        accessibilityViewModel.changedLiveData.removeObserver(accessibilityStatus)
        EventBus.getDefault().unregister(this)
    }


}
