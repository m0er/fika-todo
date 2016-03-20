package todo.fika.fikatodo.view;

import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EActivity;

import io.realm.Realm;
import todo.fika.fikatodo.util.Logger;

/**
 * Created by AidenChoi on 2016. 3. 20..
 */
@EActivity
public class BaseAppCompatActivity extends AppCompatActivity {
    protected final Logger logger = Logger.Factory.getLogger(getClass());
    protected Realm realm;

    @AfterInject
    protected void _afterInject() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;
    }
}
