package todo.fika.fikatodo;

import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.util.Log;

import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by AidenChoi on 2016. 1. 24..
 */
@RunWith(AndroidJUnit4.class)
public class DateTimeTest extends AndroidTestCase {

    public void testDateTime() {
        DateTime dateTime = DateTime.now(TimeZone.getDefault());
        DateTime startOfMonth = dateTime.getStartOfMonth();
        assertThat(DateTime.forDateOnly(2016, 1, 2).getWeekIndex(startOfMonth), is(1));
        assertThat(DateTime.forDateOnly(2016, 1, 3).getWeekIndex(startOfMonth), is(1));
        assertThat(DateTime.forDateOnly(2016, 1, 30).getWeekIndex(startOfMonth), is(5));
        assertThat(DateTime.forDateOnly(2016, 1, 31).getWeekIndex(startOfMonth), is(5));
    }

    public void testJavaCalendar() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(2016, Calendar.JANUARY, 2);
        assertThat(calendar.get(Calendar.WEEK_OF_MONTH), is(1));

        calendar.set(Calendar.DAY_OF_MONTH, 3);
        assertThat(calendar.get(Calendar.WEEK_OF_MONTH), is(2));

        calendar.set(Calendar.DAY_OF_MONTH, 30);
        assertThat(calendar.get(Calendar.WEEK_OF_MONTH), is(5));

        calendar.set(Calendar.DAY_OF_MONTH, 31);
        assertThat(calendar.get(Calendar.WEEK_OF_MONTH), is(6));
    }
}
