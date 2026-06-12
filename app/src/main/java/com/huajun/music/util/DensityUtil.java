package com.huajun.music.util;

import android.content.Context;
import android.util.TypedValue;

public class DensityUtil {
    public static int dp2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
