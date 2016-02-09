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

    /**
     * 타임존 설정을 해도 n주차가 맞지 않음.
     */
    public void testDateTime() {
        DateTime dateTime = DateTime.now(TimeZone.getTimeZone("Asia/Seoul"));
        DateTime startOfMonth = dateTime.getStartOfMonth();
        assertThat(DateTime.forDateOnly(2016, 2, 1).getWeekIndex(startOfMonth), is(1));
        assertThat(DateTime.forDateOnly(2016, 2, 7).getWeekIndex(startOfMonth), is(1));
        assertThat(DateTime.forDateOnly(2016, 2, 28).getWeekIndex(startOfMonth), is(4));
        assertThat(DateTime.forDateOnly(2016, 2, 29).getWeekIndex(startOfMonth), is(5));
    }

    public void testJavaCalendar() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(2016, Calendar.FEBRUARY, 1);
        assertThat(calendar.get(Calendar.WEEK_OF_MONTH), is(1));

        calendar.set(Calendar.DAY_OF_MONTH, 7);
        assertThat(calendar.get(Calendar.WEEK_OF_MONTH), is(2));

        calendar.set(Calendar.DAY_OF_MONTH, 28);
        assertThat(calendar.get(Calendar.WEEK_OF_MONTH), is(5));

        calendar.set(Calendar.DAY_OF_MONTH, 29);
        assertThat(calendar.get(Calendar.WEEK_OF_MONTH), is(5));
    }
}
