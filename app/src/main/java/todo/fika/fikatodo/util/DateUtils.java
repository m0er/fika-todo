package todo.fika.fikatodo.util;

import java.util.Locale;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

/**
 * Created by AidenChoi on 2016. 1. 24..
 */
public class DateUtils {

    public static String getOrdinalString(int number) {
        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (number % 100) {
            case 11:
            case 12:
            case 13:
                return number + "th";
            default:
                return number + sufixes[number % 10];
        }
    }

    public static String getTitle() {
        DateTime now = DateTime.now(TimeZone.getDefault());
        return now.format("YYYY MMM", Locale.ENGLISH);
    }
}
