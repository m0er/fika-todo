package todo.fika.fikatodo.util;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by AidenChoi on 2016. 1. 24..
 */
public class ViewUtils {

    public static int pixelByDp(Resources resources, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }
}
