package com.ivring.drag.deskfloating;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * 悬浮框工具类
 */
public class SuspendUtils {
    private static WindowManager mWindowManager;

    /**
     * 判断是否有悬浮框的权限，如果没有就去申请页面。
     *
     * @param context
     * @param REQUEST_CODE
     */
    public static void canDrawOverlays(Activity context, int REQUEST_CODE) {
        //判断有没有悬浮窗权限，没有去申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    /**
     * 显示悬浮框
     *
     * @param view     :显示的view
     * @param activity
     */
    public static void showDragTableButton(View view, Activity activity) {

        if (mWindowManager == null) {
            mWindowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        }
        int screenWidth = 0, screenHeight = 0;
        if (mWindowManager != null) {
            //获取屏幕的宽和高
            Point point = new Point();
            mWindowManager.getDefaultDisplay().getSize(point);
            screenWidth = point.x;
            screenHeight = point.y;
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //设置type
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //26及以上必须使用TYPE_APPLICATION_OVERLAY   @deprecated TYPE_PHONE
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            //设置flags
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            //背景设置成透明
            layoutParams.format = PixelFormat.TRANSPARENT;
            layoutParams.x = screenWidth;
            layoutParams.y = screenHeight / 2;
            //将View添加到屏幕上
            mWindowManager.addView(view, layoutParams);
        }
    }

    /**
     * 移除windowView
     * @param view
     * @param activity
     */
    public static void removeWindowView(View view, Activity activity) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        }
        mWindowManager.removeView(view);
    }
}
