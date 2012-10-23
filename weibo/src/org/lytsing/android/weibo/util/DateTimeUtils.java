package org.lytsing.android.weibo.util;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import org.lytsing.android.weibo.R;

import android.content.Context;
import android.text.format.DateUtils;

/**
 *  Helper class, extending the standard DateUtils of Android. 
 *  It has an advanced logic, that for timestamps of today,
 *  it would display the seconds or minutes or hours,
 *  while for other timestamps it would display the date.
 *  
 * @see http://stackoverflow.com/questions/3910042/javaandroid-convert-sqlite-date-to-x-days-ago
 */
public class DateTimeUtils extends DateUtils {

	private static String mTimestampLabelYesterday;
	private static String mTimestampLabelToday;
	private static String mTimestampLabelJustNow;
	private static String mTimestampLabelMinutesAgo;
	private static String mTimestampLabelHoursAgo;
	private static String mTimestampLabelHourAgo;
	
	static Context mCtx;
	
	private static DateTimeUtils instance = null;

	/**
	 * Singleton contructor, needed to get access to the application context &
	 * strings for i18n
	 * 
	 * @param context
	 *            Context
	 * @return DateTimeUtils singleton instanec
	 * @throws Exception
	 */
	public static DateTimeUtils getInstance(Context context) {
		mCtx = context;
		if (instance == null) {
			instance = new DateTimeUtils();
			mTimestampLabelYesterday = context.getResources().getString(
					R.string.WidgetProvider_timestamp_yesterday);
			mTimestampLabelToday = context.getResources().getString(
					R.string.WidgetProvider_timestamp_today);
			mTimestampLabelJustNow = context.getResources().getString(
					R.string.WidgetProvider_timestamp_just_now);
			mTimestampLabelMinutesAgo = context.getResources().getString(
					R.string.WidgetProvider_timestamp_minutes_ago);
			mTimestampLabelHoursAgo = context.getResources().getString(
					R.string.WidgetProvider_timestamp_hours_ago);
			mTimestampLabelHourAgo = context.getResources().getString(
					R.string.WidgetProvider_timestamp_hour_ago);
		}
		return instance;
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

	public static String[] weekdays = new DateFormatSymbols().getWeekdays(); // get
																				// day
																				// names
	public static final long millisInADay = 1000 * 60 * 60 * 24;

	/**
	 * Displays a user-friendly date difference string
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
			return hours == 1 ? String.format(mTimestampLabelHourAgo, hours)
					: String.format(mTimestampLabelHoursAgo, hours);
		} else if (hours <= 0) {
			if (minutes > 0)
				return String.format(mTimestampLabelMinutesAgo, minutes);
			else {
				return mTimestampLabelJustNow;
			}
		} else if (isToday) {
			return mTimestampLabelToday;
		} else if (isYesterday) {
			return mTimestampLabelYesterday;
		} else if (startDateTime.getTimeInMillis() - timedate < millisInADay * 6) {
			return weekdays[endDateTime.get(Calendar.DAY_OF_WEEK)];
		} else {
			return formatDateTime(mCtx, timedate, DateUtils.FORMAT_NUMERIC_DATE);
		}
	}

}