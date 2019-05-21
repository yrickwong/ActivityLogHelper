package com.example.administrator.activityloghelper;

import android.content.Context
import com.example.administrator.activityloghelper.window.IXWindow
import com.example.administrator.activityloghelper.window.floatview.FloatViewLogic

object WindowManager {

    private const val TYPE_FLOAT_WINDOW = 1

    //lateinit代表不为空的话 就一定抛出异常,所以lateinit是一种不为null的表现,但它也可以复制
    @JvmStatic
    private var mFloatWindowLogic: IXWindow? = null


    @JvmStatic
    fun showWindow(context: Context) {
        if (mFloatWindowLogic == null) {
            mFloatWindowLogic = createWindowView(TYPE_FLOAT_WINDOW)
        }
        mFloatWindowLogic?.onCreate(context)
    }

    @JvmStatic
    fun hideWindow() {
        mFloatWindowLogic?.onDestroy()
    }

    @JvmStatic
    fun isWindowShowing(): Boolean {
//        if(mFloatWindowLogic!=null){
//            return mFloatWindowLogic.isShowing
//        }else{
//            return false
//        }
        return mFloatWindowLogic?.isShowing() ?: false //等同于上面的代码,相当简化
    }

    @JvmStatic
    private fun createWindowView(type: Int): IXWindow? {
        when (type) {
            TYPE_FLOAT_WINDOW -> return FloatViewLogic()
        }
        return null
    }

}
