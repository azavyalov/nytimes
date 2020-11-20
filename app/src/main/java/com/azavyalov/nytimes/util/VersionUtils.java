package com.azavyalov.nytimes.util;

import android.os.Build;

public class VersionUtils {

    public static boolean atLeastOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean atLeastNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }
}
