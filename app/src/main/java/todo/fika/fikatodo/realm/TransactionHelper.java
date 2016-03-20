package todo.fika.fikatodo.realm;

import org.androidannotations.annotations.EBean;

import io.realm.Realm;
import io.realm.RealmObject;
import todo.fika.fikatodo.util.FikaCallback;
import todo.fika.fikatodo.util.FikaReturnCallback;

/**
 * Created by AidenChoi on 2016. 3. 20..
 */
@EBean
public class TransactionHelper {

    private void template(FikaCallback callback) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        callback.callback();

        realm.commitTransaction();
        realm.close();
    }

    private <T extends RealmObject> T returnTemplate(FikaReturnCallback<T> fikaReturnCallback) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        T target = fikaReturnCallback.callback();

        realm.commitTransaction();
        realm.close();

        return target;
    }

    public void transaction(final FikaCallback callback) {
        template(callback);
    }

    public <T extends RealmObject> T create(final T newObject) {
        return returnTemplate(new FikaReturnCallback<T>() {
            @Override
            public T callback() {
                return Realm.getDefaultInstance().copyToRealm(newObject);
            }
        });
    }

    public void remove(final RealmObject target) {
        template(new FikaCallback() {
            @Override
            public void callback() {
                target.removeFromRealm();
            }
        });
    }

}
