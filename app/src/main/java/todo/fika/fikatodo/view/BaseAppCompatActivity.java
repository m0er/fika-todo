package todo.fika.fikatodo.view;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;

import io.realm.Realm;
import todo.fika.fikatodo.otto.OttoBus;
import todo.fika.fikatodo.realm.TransactionHelper;
import todo.fika.fikatodo.util.Logger;

/**
 * Created by AidenChoi on 2016. 3. 20..
 */
@EActivity
public class BaseAppCompatActivity extends AppCompatActivity {
    protected final Logger logger = Logger.Factory.getLogger(getClass());
    protected Realm realm;

    @SystemService
    protected InputMethodManager inputMethodManager;

    @Bean
    protected OttoBus bus;

    @Bean
    protected TransactionHelper transactionHelper;

    @AfterInject
    protected void _afterInject() {
        realm = Realm.getDefaultInstance();
        bus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;
        bus.unregister(this);
    }

    /**
     * @see http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
     */
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * @see http://stackoverflow.com/questions/7200281/programatically-hide-show-android-soft-keyboard
     */
    public void showKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            inputMethodManager.showSoftInput(view, 0);
        }
    }
}
