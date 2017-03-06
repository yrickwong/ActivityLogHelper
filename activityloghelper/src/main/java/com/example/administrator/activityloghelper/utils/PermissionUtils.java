package com.example.administrator.activityloghelper.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.example.administrator.activityloghelper.LogHelperApplication;
import com.example.administrator.activityloghelper.MainActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by wangyi on 2017/3/6.
 */

public class PermissionUtils {

    public static boolean checkFloatWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(LogHelperApplication.getInstance());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //AppOpsManager添加于API 19
            return checkOps();
        } else {
            //4.4以下一般都可以直接添加悬浮窗
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static boolean checkOps() {
        try {
            Object object = LogHelperApplication.getInstance().getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            /*
                24位于AppOpsManager类里面，悬浮窗的权限检测在个类里
                @hide public static final int OP_SYSTEM_ALERT_WINDOW = 24;
            */
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = LogHelperApplication.getInstance().getPackageName();
            int m = (Integer) method.invoke(object, arrayOfObject1);
            //4.4至6.0之间的非国产手机，例如samsung，sony一般都可以直接添加悬浮窗
            return m == AppOpsManager.MODE_ALLOWED || !RomUtils.isDomesticSpecialRom();
        } catch (Exception ignore) {
        }
        return false;
    }

    /**
     * 6.0以上检测悬浮窗授权(通用的)
     *
     * @param context
     */
    private static void applyCommonPermission(Context context) {
        try {
            Class clazz = Settings.class;
            Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
            Intent intent = new Intent(field.get(null).toString());
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            if(context instanceof Activity){
                Activity activity= (Activity) context;
                activity.startActivityForResult(intent, MainActivity.REQUEST_CODE);
            }else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 360权限申请
     */
    private static void apply360Permission(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$OverlaySettingsActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            intent.setClassName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 小米权限申请
     */
    private static void applyMiuiPermission(Context context) {
        int versionCode = RomUtils.getMiuiVersion();
        if (versionCode == 5) {
            goToMiuiPermissionActivity_V5(context);
        } else if (versionCode == 6) {
            goToMiuiPermissionActivity_V6(context);
        } else if (versionCode == 7) {
            goToMiuiPermissionActivity_V7(context);
        } else if (versionCode == 8) {
            goToMiuiPermissionActivity_V8(context);
        } else {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 小米V5版本权限申请
     */
    private static void goToMiuiPermissionActivity_V5(Context context) {
        Intent intent;
        String packageName = context.getPackageName();
        intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 小米V6版本权限申请
     */
    private static void goToMiuiPermissionActivity_V6(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 小米V7版本权限申请
     */
    private static void goToMiuiPermissionActivity_V7(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 小米V8版本权限申请
     */
    private static void goToMiuiPermissionActivity_V8(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setPackage("com.miui.securitycenter");
            intent.putExtra("extra_pkgname", context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 魅族权限申请
     */
    private static void applyMeizuPermission(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        intent.putExtra("packageName", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 华为权限申请
     */
    private static void applyHuaweiPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");
            intent.setComponent(comp);
            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                comp = new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");
                intent.setComponent(comp);
                context.startActivity(intent);
            }
        } catch (SecurityException e) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity");
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.Android.settings", "com.android.settings.permission.TabItem");
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * CoolPad权限申请
     */
    private static void applyCoolpadPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.yulong.android.seccenter", "com.yulong.android.seccenter.dataprotection.ui.AppListActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 联想权限申请
     */
    private static void applyLenovoPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.lenovo.safecenter", "com.lenovo.safecenter.MainTab.LeSafeMainActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 中兴权限申请
     */
    private static void applyZTEPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction("com.zte.heartyservice.intent.action.startActivity.PERMISSION_SCANNER");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 乐视权限申请
     */
    private static void applyLetvPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AppActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Vivo权限申请
     */
    private static void applyVivoPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Oppo权限申请
     */
    private static void applyOppoPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    private static boolean isIntentAvailable(Intent intent, Context context) {
        return intent != null && context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    /**
     * 锤子权限申请
     */
    public static boolean applySmartisanPermission(Context context) {
        Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        // TODO
        return false;
    }

    /**
     * 海尔权限申请
     */
    public static boolean applyHaierPermission(Context context) {
        // TODO
        Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        return false;
    }

    public static void applyAuthorizePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            applyCommonPermission(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(RomUtils.checkIs360Rom()){
                apply360Permission(context);
            }else if(RomUtils.isMiuiRom()){
                applyMiuiPermission(context);
            }else if(RomUtils.isHuaweiRom()){
                applyHuaweiPermission(context);
            }else if(RomUtils.isOppoRom()){
                applyHuaweiPermission(context);
            }else {
                Toast.makeText(context, "此rom未适配,请联系开发！", Toast.LENGTH_LONG).show();
            }
        }
    }
}
