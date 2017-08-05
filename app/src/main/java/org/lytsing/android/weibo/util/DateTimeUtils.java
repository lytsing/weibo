/*
 * Copyright (C) 2012 http://lytsing.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lytsing.android.weibo.util;

import android.content.Context;
import android.text.format.DateUtils;

import org.lytsing.android.weibo.R;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 *  Helper class, extending the standard DateUtils of Android.
 *  It has an advanced logic, that for timestamps of today,
 *  it would display the seconds or minutes or hours,
 *  while for other timestamps it would display the date.
 *
 * @see http://stackoverflow.com/questions/3910042/javaandroid-convert-sqlite-date-to-x-days-ago
 */
public class DateTimeUtils extends DateUtils {

    private static String sTimestampLabelYesterday;
    private static String sTimestampLabelToday;
    private static String sTimestampLabelJustNow;
    private static String sTimestampLabelMinutesAgo;
    private static String sTimestampLabelHoursAgo;
    private static String sTimestampLabelHourAgo;

    private static Context sCtx;

    private static DateTimeUtils sInstance = null;

    /**
     * Singleton contructor. needed to get access to the application context &
     * strings for i18n
     *
     * @param context
     *            Context
     * @return DateTimeUtils singleton instanec
     * @throws Exception
     */
    public static DateTimeUtils getInstance(Context context) {
        sCtx = context;
        if (sInstance == null) {
            sInstance = new DateTimeUtils();
            sTimestampLabelYesterday = context.getResources().getString(
                    R.string.WidgetProvider_timestamp_yesterday);
            sTimestampLabelToday = context.getResources().getString(
                    R.string.WidgetProvider_timestamp_today);
            sTimestampLabelJustNow = context.getResources().getString(
                    R.string.WidgetProvider_timestamp_just_now);
            sTimestampLabelMinutesAgo = context.getResources().getString(
                    R.string.WidgetProvider_timestamp_minutes_ago);
            sTimestampLabelHoursAgo = context.getResources().getString(
                    R.string.WidgetProvider_timestamp_hours_ago);
            sTimestampLabelHourAgo = context.getResources().getString(
                    R.string.WidgetProvider_timestamp_hour_ago);
        }
        return sInstance;
    }

    /**
     * Checks if the given date is yesterday.
     *
     * @param date
     *            - Date to check.
     * @return TRUE if the date is yesterday, FALSE otherwise.
     */
    public static boolean isYesterday(long date) {

        final Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(date);

        final Calendar yesterdayDate = Calendar.getInstance();
        yesterdayDate.add(Calendar.DATE, -1);

        return yesterdayDate.get(Calendar.YEAR) == currentDate
            .get(Calendar.YEAR)
            && yesterdayDate.get(Calendar.DAY_OF_YEAR) == currentDate
            .get(Calendar.DAY_OF_YEAR);
    }

    public static String[] sWeekdays = new DateFormatSymbols().getWeekdays(); // get
    // day
    // names
    public static final long millisInADay = 1000 * 60 * 60 * 24;

    /**
     * Displays a user-friendly date difference string.
     *
     * @param timedate
     *            Timestamp to format as date difference from now
     * @return Friendly-formatted date diff string
     */
    public String getTimeDiffString(long timedate) {
        Calendar startDateTime = Calendar.getInstance();
        Calendar endDateTime = Calendar.getInstance();
        endDateTime.setTimeInMillis(timedate);
        long milliseconds1 = startDateTime.getTimeInMillis();
        long milliseconds2 = endDateTime.getTimeInMillis();
        long diff = milliseconds1 - milliseconds2;

        long hours = diff / (60 * 60 * 1000);
        long minutes = diff / (60 * 1000);
        minutes = minutes - 60 * hours;
        //long seconds = diff / (1000);

        boolean isToday = DateTimeUtils.isToday(timedate);
        boolean isYesterday = DateTimeUtils.isYesterday(timedate);

        if (hours > 0 && hours < 12) {
            return hours == 1 ? String.format(sTimestampLabelHourAgo, hours)
                : String.format(sTimestampLabelHoursAgo, hours);
        } else if (hours <= 0) {
            if (minutes > 0) {
                return String.format(sTimestampLabelMinutesAgo, minutes);
            } else {
                return sTimestampLabelJustNow;
            }
        } else if (isToday) {
            return sTimestampLabelToday;
        } else if (isYesterday) {
            return sTimestampLabelYesterday;
        } else if (startDateTime.getTimeInMillis() - timedate < millisInADay * 6) {
            return sWeekdays[endDateTime.get(Calendar.DAY_OF_WEEK)];
        } else {
            return formatDateTime(sCtx, timedate, DateUtils.FORMAT_NUMERIC_DATE);
        }
    }
}

