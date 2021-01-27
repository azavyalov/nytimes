package com.azavyalov.nytimes.util;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.disposables.Disposable;

import static android.text.format.DateUtils.DAY_IN_MILLIS;
import static android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE;
import static android.text.format.DateUtils.HOUR_IN_MILLIS;

public final class Util {

    public static CharSequence formatDateTime(Context context, Date dateTime) {
        return DateUtils.getRelativeDateTimeString(
                context,
                dateTime.getTime(),
                HOUR_IN_MILLIS,
                5 * DAY_IN_MILLIS,
                FORMAT_ABBREV_RELATIVE
        );
    }

    public static String formatDateString(String date) {
        String formattedDate = date.replaceAll("[T]", "");
        SimpleDateFormat initialFormat = new SimpleDateFormat(
                "yyyy-MM-ddHH:mm:ss", Locale.getDefault());
        SimpleDateFormat desiredFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.getDefault());
        Date newDate = null;
        try {
            newDate = initialFormat.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return desiredFormat.format(newDate);
    }

    public static void setVisibility(@Nullable View view, boolean show) {
        if (view == null) return;
        int visibility = show ? View.VISIBLE : View.GONE;
        view.setVisibility(visibility);
    }

    public static void disposeSafe(@Nullable Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
