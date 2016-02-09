package todo.fika.fikatodo.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import java.lang.ref.WeakReference;

import todo.fika.fikatodo.R;

/**
 * Created by AidenChoi on 2016. 1. 24..
 */
public class ViewUtils {
    private static Context context;

    public static void init(Context context) {
        ViewUtils.context = context;
    }

    public static int pixelByDp(Resources resources, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    /**
     * @param weekDay 1..7 0이 일요일 7이 토요일.
     * @return
     */
    public static int colorByWeekDay(int weekDay) {
        switch (weekDay) {
            case 1:
                return context.getResources().getColor(R.color.colorSunday);
            case 2:
                return context.getResources().getColor(R.color.colorMonday);
            case 3:
                return context.getResources().getColor(R.color.colorTuesday);
            case 4:
                return context.getResources().getColor(R.color.colorWednesday);
            case 5:
                return context.getResources().getColor(R.color.colorThursday);
            case 6:
                return context.getResources().getColor(R.color.colorFriday);
            case 7:
                return context.getResources().getColor(R.color.colorSaturday);
            default:
                return context.getResources().getColor(R.color.colorPrimary);
        }
    }
}
