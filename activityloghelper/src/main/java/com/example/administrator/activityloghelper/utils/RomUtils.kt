package com.example.administrator.activityloghelper.utils;

import android.os.Build
import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by wangyi on 2017/3/6.
 */

object RomUtils {
    private const val TAG = "RomUtils"

    @JvmStatic
    fun getSystemProperty(propName: String): String? {
        val line: String?
        var input: BufferedReader? = null
        try {
            val p: Process = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            Log.e(TAG, "Unable to read sysprop $propName", ex)
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Exception while closing InputStream", e)
                }
            }
        }
        return line
    }

    /**
     * 判断是否为华为系统
     */
    @JvmStatic
    fun isHuaweiRom(): Boolean {
        val manufacturer = Build.MANUFACTURER
        return !TextUtils.isEmpty(manufacturer) && manufacturer.contains("HUAWEI")
    }

    /**
     * 获取华为EmotionUI版本号，失败默认返回-1
     */
    @JvmStatic
    fun getEmotionUiVersion(): Double {
        try {
            val emuiVersion = getSystemProperty("ro.build.version.emui")
            val version = emuiVersion?.substring(emuiVersion.indexOf("_") + 1)
            return version!!.toDouble()
        } catch (e: Exception) {
            Log.e(TAG, "get emui version code error")
        }
        return -1.0
    }

    /**
     * 判断是否为小米系统
     */
    @JvmStatic
    fun isMiuiRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"))
    }

    /**
     * 获取小米MIUI版本号，失败默认返回-1
     */
    @JvmStatic
    fun getMiuiVersion(): Int {
        val version = getSystemProperty("ro.miui.ui.version.name");
        if (TextUtils.isEmpty(version)) {
            try {
                return Integer.parseInt(version!!.substring(1))
            } catch (e: Exception) {
                Log.e(TAG, "get miui version code error")
            }
        }
        return -1
    }

    /**
     * 判断是否为魅族系统
     */
    @JvmStatic
    fun isMeizuRom(): Boolean {
        val meizuFlymeOSFlag = getSystemProperty("ro.build.display.id")
        return !TextUtils.isEmpty(meizuFlymeOSFlag) && meizuFlymeOSFlag!!.toLowerCase().contains("flyme")
    }

    /**
     * 判断是否为360系统
     */
    @JvmStatic
    fun checkIs360Rom(): Boolean {
        val manufacturer = Build.MANUFACTURER
        return !TextUtils.isEmpty(manufacturer) && manufacturer.contains("QiKU")
    }

    /**
     * 判断是否为乐视系统
     */
    @JvmStatic
    fun isLetvRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty("ro.letv.eui"))
    }

    /**
     * 判断是否为Oppo系统
     */
    @JvmStatic
    fun isOppoRom(): Boolean {
        val a = getSystemProperty("ro.product.brand")
        return !TextUtils.isEmpty(a) && a!!.toLowerCase().contains("oppo")
    }

    /**
     * 判断是否为Vivo系统
     */
    @JvmStatic
    fun isVivoRom(): Boolean {
        val a = getSystemProperty("ro.vivo.os.name")
        return !TextUtils.isEmpty(a) && a!!.toLowerCase().contains("funtouch")
    }

    /**
     * 判断是否为联想系统
     */
    @JvmStatic
    fun isLenovoRom(): Boolean {
        val fingerPrint = Build.FINGERPRINT
        if (!TextUtils.isEmpty(fingerPrint)) {
            return fingerPrint.contains("VIBEUI_V2")
        }
        val a = getSystemProperty("ro.build.version.incremental")
        return !TextUtils.isEmpty(a) && a!!.contains("VIBEUI_V2")
    }

    /**
     * 判断是否为CoolPad系统
     */
    @JvmStatic
    fun isCoolPadRom(): Boolean {
        val model = Build.MODEL
        val fingerPrint = Build.FINGERPRINT
        return !TextUtils.isEmpty(model) && model.toLowerCase().contains("coolpad") || !TextUtils.isEmpty(fingerPrint) && fingerPrint.toLowerCase().contains("coolpad")
    }

    /**
     * 判断是否为中兴系统
     */
    @JvmStatic
    fun isZTERom(): Boolean {
        val manufacturer = Build.MANUFACTURER
        if (!TextUtils.isEmpty(manufacturer)) {
            return manufacturer.toLowerCase().contains("nubia") || manufacturer.toLowerCase().contains("zte")
        }
        val fingerPrint = Build.FINGERPRINT
        return if (!TextUtils.isEmpty(fingerPrint)) {
            fingerPrint.toLowerCase().contains("nubia") || fingerPrint.toLowerCase().contains("zte")
        } else false
    }

    /**
     * 判断是否为锤子系统
     */
    @JvmStatic
    fun isSmartisanRom(): Boolean {
        // TODO: 2017/2/15
        return false;
    }

    /**
     * 判断是否为海尔系统
     */
    @JvmStatic
    fun isHaierRom(): Boolean {
        // TODO: 2017/2/15
        return false
    }

    @JvmStatic
    fun isDomesticSpecialRom(): Boolean {
        return RomUtils.isMiuiRom()
                || RomUtils.isHuaweiRom()
                || RomUtils.isMeizuRom()
                || RomUtils.checkIs360Rom()
                || RomUtils.isOppoRom()
                || RomUtils.isVivoRom()
                || RomUtils.isLetvRom()
                || RomUtils.isZTERom()
                || RomUtils.isLenovoRom()
                || RomUtils.isCoolPadRom();
    }
}
