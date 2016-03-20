package todo.fika.fikatodo.util;

import android.util.Log;

/**
 * Created by AidenChoi on 2016. 2. 9..
 */
public class Logger {
    private String tag;

    public Logger(String tag) {
        this.tag = tag;
    }

    public void d(Object obj) {
        Log.d(tag, obj == null ? "null" : obj.toString());
    }

    public void d(String formatter, Object... objs) {
        if (objs == null) {
            d("null");
            return;
        }

        Log.d(tag, String.format(formatter, objs));
    }

    public void d(Throwable t, String formatter, Object... objs) {
        if (objs == null) {
            d("null");
            return;
        }

        Log.d(tag, String.format(formatter, objs), t);
    }

    public void i(Object obj) {
        Log.i(tag, obj == null ? "null" : obj.toString());
    }

    public void i(String formatter, Object... objs) {
        if (objs == null) {
            i("null");
            return;
        }

        Log.i(tag, String.format(formatter, objs));
    }

    public void i(Throwable t, String formatter, Object... objs) {
        if (objs == null) {
            i("null");
            return;
        }

        Log.i(tag, String.format(formatter, objs), t);
    }

    public void w(Object obj) {
        Log.w(tag, obj == null ? "null" : obj.toString());
    }

    public void w(String formatter, Object... objs) {
        if (objs == null) {
            w("null");
            return;
        }

        Log.w(tag, String.format(formatter, objs));
    }

    public void w(Throwable t, String formatter, Object... objs) {
        if (objs == null) {
            w("null");
            return;
        }

        Log.w(tag, String.format(formatter, objs), t);
    }

    public static class Factory {
        public static Logger getLogger(Class clz) {
            return new Logger(clz.getName());
        }
    }

}
