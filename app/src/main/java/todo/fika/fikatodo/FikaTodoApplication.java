package todo.fika.fikatodo;

import android.app.Application;

import org.androidannotations.annotations.EApplication;

import todo.fika.fikatodo.util.ViewUtils;

/**
 * Created by AidenChoi on 2016. 1. 14..
 */
@EApplication
public class FikaTodoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ViewUtils.init(this);
    }
}
