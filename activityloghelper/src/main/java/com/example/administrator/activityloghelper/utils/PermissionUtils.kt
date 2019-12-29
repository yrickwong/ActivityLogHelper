package com.example.administrator.activityloghelper.utils;

import android.app.AppOpsManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi


/**
 * Created by wangyi on 2017/3/6.
 */


const val PERMISSION_CODE = 1

fun checkFloatWindowPermission(context: Context): Boolean {
    return when {
        Build.VERSION.SDK_INT > Build.VERSION_CODES.N -> Settings.canDrawOverlays(context)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> !isDomesticSpecialRom() || Settings.canDrawOverlays(context)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ->  checkOps(context)//AppOpsManager添加于API 19
        else -> //4.4以下一般都可以直接添加悬浮窗
            true
    }
}

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
private fun checkOps(context: Context): Boolean {
    try {
        val `object` = context.getSystemService(Context.APP_OPS_SERVICE) ?: return false
        val localClass = `object`.javaClass
        val arrayOfClass = arrayOfNulls<Class<*>>(3)
        arrayOfClass[0] = Integer.TYPE
        arrayOfClass[1] = Integer.TYPE
        arrayOfClass[2] = String::class.java
        val method = localClass.getMethod("checkOp", *arrayOfClass) ?: return false
        val arrayOfObject1 = arrayOfNulls<Any>(3)
        /*
            24位于AppOpsManager类里面，悬浮窗的权限检测在个类里
            @hide public static final int OP_SYSTEM_ALERT_WINDOW = 24;
        */
        arrayOfObject1[0] = 24
        arrayOfObject1[1] = Binder.getCallingUid()
        arrayOfObject1[2] = context.packageName
        val m = method.invoke(`object`, *arrayOfObject1) as Int
        //4.4至6.0之间的非国产手机，例如samsung，sony一般都可以直接添加悬浮窗
        return m == AppOpsManager.MODE_ALLOWED || !isDomesticSpecialRom()
    } catch (ignore: Exception) {
    }

    return false
}

/**
 * 6.0以上检测悬浮窗授权(通用的)
 *
 * @param context
 */
private fun applyCommonPermission(context: Context, block: (Intent?) -> Unit) {
    try {
        val clazz = Settings::class.java
        val field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION")
        val intent = Intent(field.get(null).toString())
        intent.data = Uri.parse("package:" + context.packageName)
        block(intent)
    } catch (e: Exception) {
        block(null)
    }

}


/**
 * 360权限申请
 */
private fun apply360Permission(context: Context) {
    val intent = Intent()
    intent.setClassName("com.android.settings", "com.android.settings.Settings\$OverlaySettingsActivity")
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    if (isIntentAvailable(intent, context)) {
        context.startActivity(intent)
    } else {
        intent.setClassName(
            "com.qihoo360.mobilesafe",
            "com.qihoo360.mobilesafe.ui.index.AppEnterActivity"
        )
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
        }
    }
}

/**
 * 小米权限申请
 */
private fun applyMiuiPermission(context: Context) {
    when (getMiuiVersion()) {
        5 -> {
            goToMiuiPermissionActivity_V5(context)
        }
        6 -> {
            goToMiuiPermissionActivity_V6(context)
        }
        7 -> {
            goToMiuiPermissionActivity_V7(context)
        }
        8 -> {
            goToMiuiPermissionActivity_V8(context)
        }
        else -> {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
        }
    }
}

/**
 * 小米V5版本权限申请
 */
private fun goToMiuiPermissionActivity_V5(context: Context) {
    val packageName = context.packageName
    val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    if (isIntentAvailable(intent, context)) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
    }
}

/**
 * 小米V6版本权限申请
 */
private fun goToMiuiPermissionActivity_V6(context: Context) {
    val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
    intent.setClassName(
        "com.miui.securitycenter",
        "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
    )
    intent.putExtra("extra_pkgname", context.packageName)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

    if (isIntentAvailable(intent, context)) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
    }
}

/**
 * 小米V7版本权限申请
 */
private fun goToMiuiPermissionActivity_V7(context: Context) {
    val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
    intent.setClassName(
        "com.miui.securitycenter",
        "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
    )
    intent.putExtra("extra_pkgname", context.packageName)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

    if (isIntentAvailable(intent, context)) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
    }
}

/**
 * 小米V8版本权限申请
 */
private fun goToMiuiPermissionActivity_V8(context: Context) {
    var intent = Intent("miui.intent.action.APP_PERM_EDITOR")
    intent.setClassName(
        "com.miui.securitycenter",
        "com.miui.permcenter.permissions.PermissionsEditorActivity"
    )
    intent.putExtra("extra_pkgname", context.packageName)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

    if (isIntentAvailable(intent, context)) {
        context.startActivity(intent)
    } else {
        intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setPackage("com.miui.securitycenter")
        intent.putExtra("extra_pkgname", context.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
        }
    }
}

/**
 * 魅族权限申请
 */
private fun applyMeizuPermission(context: Context) {
    val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
    intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity")
    intent.putExtra("packageName", context.packageName)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    if (isIntentAvailable(intent, context)) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
    }
}

/**
 * 华为权限申请
 */
private fun applyHuaweiPermission(context: Context) {
    try {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        var comp = ComponentName(
            "com.huawei.systemmanager",
            "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity"
        )
        intent.component = comp
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent)
        } else {
            comp = ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.notificationmanager.ui.NotificationManagmentActivity"
            )
            intent.component = comp
            context.startActivity(intent)
        }
    } catch (e: SecurityException) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val comp = ComponentName(
            "com.huawei.systemmanager",
            "com.huawei.permissionmanager.ui.MainActivity"
        )
        intent.component = comp
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val comp =
            ComponentName("com.Android.settings", "com.android.settings.permission.TabItem")
        intent.component = comp
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
    }

}

/**
 * CoolPad权限申请
 */
private fun applyCoolpadPermission(context: Context) {
    try {
        val intent = Intent()
        intent.setClassName(
            "com.yulong.android.seccenter",
            "com.yulong.android.seccenter.dataprotection.ui.AppListActivity"
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
    }

}

/**
 * 联想权限申请
 */
private fun applyLenovoPermission(context: Context) {
    try {
        val intent = Intent()
        intent.setClassName(
            "com.lenovo.safecenter",
            "com.lenovo.safecenter.MainTab.LeSafeMainActivity"
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
    }

}

/**
 * 中兴权限申请
 */
private fun applyZTEPermission(context: Context) {
    try {
        val intent = Intent()
        intent.action = "com.zte.heartyservice.intent.action.startActivity.PERMISSION_SCANNER"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
    }

}

/**
 * 乐视权限申请
 */
private fun applyLetvPermission(context: Context) {
    try {
        val intent = Intent()
        intent.setClassName(
            "com.letv.android.letvsafe",
            "com.letv.android.letvsafe.AppActivity"
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
    }

}

/**
 * Vivo权限申请
 */
private fun applyVivoPermission(context: Context) {
    try {
        val intent = Intent()
        intent.setClassName(
            "com.iqoo.secure",
            "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager"
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
    }

}

/**
 * Oppo权限申请
 */
private fun applyOppoPermission(context: Context) {
    try {
        val intent = Intent()
        intent.setClassName(
            "com.oppo.safe",
            "com.oppo.safe.permission.PermissionAppListActivity"
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
    }

}

private fun isIntentAvailable(intent: Intent?, context: Context): Boolean {
    return intent != null && context.packageManager.queryIntentActivities(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    ).size > 0
}

/**
 * 锤子权限申请
 */
fun applySmartisanPermission(context: Context): Boolean {
    Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
    // TODO
    return false
}

/**
 * 海尔权限申请
 */
fun applyHaierPermission(context: Context): Boolean {
    // TODO
    Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
    return false
}

fun applyAuthorizePermission(context: Context, callBack: (Intent?) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        applyCommonPermission(context, callBack)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        when {
            checkIs360Rom() -> {
                apply360Permission(context)
            }
            isMiuiRom() -> {
                applyMiuiPermission(context)
            }
            isHuaweiRom() -> {
                applyHuaweiPermission(context)
            }
            isOppoRom() -> {
                applyHuaweiPermission(context)
            }
            else -> {
                Toast.makeText(context, "此rom未适配,请联系开发！", Toast.LENGTH_LONG).show()
            }
        }
    }
}
