package com.example.crxzy.centertainment.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.example.crxzy.centertainment.R;

public class Tool {
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources ( ).getDisplayMetrics ( ).density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources ( ).getDisplayMetrics ( ).density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        int width = getDisplayMetrics (context).widthPixels;
        return width;
    }

    public static int getScreenHeight(Context context) {
        int height = getDisplayMetrics (context).heightPixels;
        return height;
    }

    public static float getScreenDensity(Context context) {
        float density = getDisplayMetrics (context).density;
        return density;
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        Resources resources = context.getResources ( );
        DisplayMetrics dm = resources.getDisplayMetrics ( );
        return dm;
    }
}
