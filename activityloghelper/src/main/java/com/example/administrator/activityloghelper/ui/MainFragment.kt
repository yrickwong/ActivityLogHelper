package com.example.administrator.activityloghelper.ui;

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.activityViewModel
import com.example.administrator.activityloghelper.R
import com.example.administrator.activityloghelper.WindowManager
import com.example.administrator.activityloghelper.model.AccessibilityState
import com.example.administrator.activityloghelper.model.AccessibilityViewModel
import com.example.administrator.activityloghelper.utils.PERMISSION_CODE
import com.example.administrator.activityloghelper.utils.applyAuthorizePermission
import com.example.administrator.activityloghelper.utils.checkFloatWindowPermission
import kotlinx.android.synthetic.main.content_main.*


class MainFragment : BaseMvRxFragment() {

    private val mAccessibleIntent: Intent by lazy {
        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    }
    private val accessibilityViewModel: AccessibilityViewModel by activityViewModel()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_CODE) {
            if (!checkFloatWindowPermission(requireContext())) {
                Toast.makeText(activity, R.string.permission_decline, Toast.LENGTH_SHORT).show()
            } else {
                WindowManager.showWindow(requireContext())
            }
        }
    }

    override fun invalidate() {

    }

    /**
     * 引导AccessibilityManager授权
     */
    private fun openServer() {
        startActivity(mAccessibleIntent)
    }

    private fun switchWindow() {
        if (!WindowManager.isWindowShowing()) {
            //是否需要授权?
            if (!checkFloatWindowPermission(requireContext())) {
                applyAuthorizePermission(requireContext()) { intent ->
                    startActivityForResult(intent, PERMISSION_CODE)
                }
            } else {
                activity?.let { WindowManager.showWindow(it) }
            }
        } else {
            WindowManager.hideWindow()
        }
    }


    companion object {
        @JvmStatic
        fun createMainFragment(bundle: Bundle? = null): Fragment = MainFragment().apply {
            arguments = bundle
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.content_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openServerBtn.setOnClickListener {
            openServer()
        }
        openWindowBtn.setOnClickListener {
            switchWindow()
        }

        accessibilityViewModel.selectSubscribe(AccessibilityState::status) {
            openServerBtn.text = it
        }
        accessibilityViewModel.selectSubscribe(AccessibilityState::windowStatus) {
            openWindowBtn.text = it
        }
    }
}
