package com.example.crxzy.centertainment.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

import com.example.crxzy.centertainment.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Tool {
    public static int getViewHeight(Context context, View view) {
        final DisplayMetrics dm = context.getResources ( ).getDisplayMetrics ( );
        view.measure (
                View.MeasureSpec.makeMeasureSpec (dm.widthPixels, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec (dm.heightPixels, View.MeasureSpec.AT_MOST));

        return view.getMeasuredHeight ( );
    }

    public static int getViewWidth(Context context, View view) {
        final DisplayMetrics dm = context.getResources ( ).getDisplayMetrics ( );
        view.measure (
                View.MeasureSpec.makeMeasureSpec (dm.widthPixels, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec (dm.heightPixels, View.MeasureSpec.AT_MOST));
        return view.getMeasuredWidth ( );
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources ( ).getDisplayMetrics ( ).density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources ( ).getDisplayMetrics ( ).density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        return getDisplayMetrics (context).widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return getDisplayMetrics (context).heightPixels;
    }

    public static float getScreenDensity(Context context) {
        return getDisplayMetrics (context).density;
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources ( ).getDisplayMetrics ( );
    }

    public static void setFullWindow(AppCompatActivity context) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = context.getWindow ( ).getDecorView ( );
            v.setSystemUiVisibility (View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = context.getWindow ( ).getDecorView ( );
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility (uiOptions);
        }
    }

    public static void setNormalWindow(AppCompatActivity context) {
        //恢复普通状态
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = context.getWindow ( ).getDecorView ( );
            v.setSystemUiVisibility (View.VISIBLE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = context.getWindow ( ).getDecorView ( );
            int uiOptions = View.SCREEN_STATE_OFF;
            decorView.setSystemUiVisibility (uiOptions);
        }
    }

    public static int getResourceIdByName(Context context, String name) {
        return context.getResources ( ).getIdentifier (name, "id", context.getPackageName ( ));
    }

    public static int getResId(String variableName, Class <?> c) {
        try {
            Field idField = c.getDeclaredField (variableName);
            return idField.getInt (idField);
        } catch (Exception e) {
            e.printStackTrace ( );
            return -1;
        }
    }
}
