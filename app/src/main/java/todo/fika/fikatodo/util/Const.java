package todo.fika.fikatodo.util;

/**
 * Created by AidenChoi on 2016. 1. 27..
 */
public interface Const {
    String DEFAULT_TIME_ZONE = "Asia/Seoul";
    int TYPE_HEADER = 0;
    int TYPE_TODO = 1;
    int TYPE_FOOTER = 2;

    interface Animation {
        int SHORT = 150;
        int NORMAL = 300;
        int LONG = 600;
    }
}
