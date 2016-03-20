package todo.fika.fikatodo.util;

/**
 * Created by AidenChoi on 2016. 3. 20..
 */
public class FikaUtils {

    public static boolean isTextEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
}
