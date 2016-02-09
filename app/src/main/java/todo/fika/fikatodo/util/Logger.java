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
        Log.d(tag, String.format(formatter, objs));
    }

    public static class Factory {

        public static Logger getLogger(Class clz) {
            return new Logger(clz.getName());
        }
    }

}
