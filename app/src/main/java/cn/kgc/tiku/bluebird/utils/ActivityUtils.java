package cn.kgc.tiku.bluebird.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityUtils {
    private static ProgressDialog progressDialog;
    private static SharedPreferences sharedPreferences;
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
    private static SharedPreferences.Editor editor;
    /**
     * 之前显示的内容
     */
    private static String oldMsg;
    /**
     * Toast对象
     */
    private static Toast toast = null;
    /**
     * 第一次时间
     */
    private static long oneTime = 0;
    /**
     * 第二次时间
     */
    private static long twoTime = 0;

    public static void showProgressDialog(Context mContext, String text) {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(text);    //设置内容
        progressDialog.setCancelable(false);//点击屏幕和按返回键都不能取消加载框
        progressDialog.show();
    }

    public static Boolean dismissProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                return true;//取消成功
            }
        }
        return false;//已经取消过了，不需要取消
    }

    /**
     * 禁止EditText输入空格
     *
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    public static void showAlert(Context context, String title, String message, DialogInterface.OnClickListener negative, DialogInterface.OnClickListener positive) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("取消", negative)
                .setPositiveButton("确定", positive)
                .create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void showAlert(Context context, String title, String message, DialogInterface.OnClickListener positive) {
        try {
            Looper.prepare();
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("确定", positive)
                    .create();
            dialog.setCancelable(false);
            dialog.show();
        } finally {
            Looper.loop();
        }
    }

    public static void showAlertMainThread(Context context, String title, String message, DialogInterface.OnClickListener positive) {

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", positive)
                .create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public static String convertToDataString(long time) {
        return DATE_FORMAT.format(new Date(time));
    }

    public static String getSaveValue(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public static boolean saveValue(String key, String value) {
        editor.putString(key, value);
        return editor.commit();
    }

    public static void setSharedPreferences(SharedPreferences sharedPreferences) {
        ActivityUtils.sharedPreferences = sharedPreferences;
        editor = sharedPreferences.edit();
    }

    /**
     * 显示Toast
     *
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (message.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = message;
                toast.setText(message);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    //检查是否加群
    public static boolean c() {
        for (int i = 0; i < Contant.accountList.size(); i++) {
            if (Contant.accountList.get(i).equalsIgnoreCase(Contant.LOGIN_ACCOUNT)) {
                return true;
            }
        }
        return false;
    }
}
