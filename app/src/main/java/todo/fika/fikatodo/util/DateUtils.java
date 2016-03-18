package todo.fika.fikatodo.util;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

/**
 * Created by AidenChoi on 2016. 1. 24..
 */
public class DateUtils {
    private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone(Const.DEFAULT_TIME_ZONE);

    public static int getWeekOfMonth() {
        return Calendar.getInstance(DEFAULT_TIMEZONE).get(Calendar.WEEK_OF_MONTH);
    }

    public static int getWeekDay() {
        return Calendar.getInstance(DEFAULT_TIMEZONE).get(Calendar.DAY_OF_WEEK);
    }

    public static int getDay() {
        return Calendar.getInstance(DEFAULT_TIMEZONE).get(Calendar.DAY_OF_MONTH);
    }

    public static String getTodayTitle() {
        DateTime now = DateTime.now(DEFAULT_TIMEZONE);
        return now.format("WWWW", Locale.ENGLISH);
    }

    public static String getTodayDateTime() {
        DateTime now = DateTime.now(DEFAULT_TIMEZONE);
        return now.format("D, MMMM YYYY", Locale.ENGLISH);
    }
}
