package todo.fika.fikatodo.realm;

import org.androidannotations.annotations.EBean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import todo.fika.fikatodo.util.Logger;

/**
 * Created by zacheusz on 16/03/16.
 * https://github.com/realm/realm-java/issues/469#issuecomment-196798253
 */
@EBean(scope = EBean.Scope.Singleton)
public class PrimaryKeyFactory {

    /** primary key field name */
    private static final String PRIMARY_KEY_FIELD = "id";

    private final Logger logger = Logger.Factory.getLogger(getClass());

    /**
     * Maximum primary key values.
     */
    private Map<Class<? extends RealmObject>, AtomicLong> keys;

    /**
     * Initialize the factory. Must be called before any primary key is generated
     * - preferably from application class.
     */
    public synchronized void initialize(final Realm realm) {
        if (keys != null) {
            throw new IllegalStateException("already initialized");
        }
        // keys field is used as an initialization flag at the same time
        keys = new HashMap<>();
        final RealmConfiguration configuration = realm.getConfiguration();
        final RealmSchema realmSchema = realm.getSchema();
        //using RealmConfiguration#getRealmObjectClasses because
        // RealmSchema#getAll() returns RealmObjectSchema with simple class names only
        for (final Class<? extends RealmObject> c : configuration.getRealmObjectClasses()) {

            final RealmObjectSchema objectSchema = realmSchema.get(c.getSimpleName());
            logger.i("schema for class %s : %s", c.getName(), objectSchema);
            if (objectSchema != null && objectSchema.hasPrimaryKey()) {
                Number keyValue = null;
                try {
                    keyValue = realm.where(c).max(PRIMARY_KEY_FIELD);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logger.d(ex, "error while getting number primary key %s for %s", PRIMARY_KEY_FIELD, c.getName());
                }
                if (keyValue == null) {
                    logger.w("can't find number primary key %s for %s.", PRIMARY_KEY_FIELD, c.getName());
                } else {
                    keys.put(c, new AtomicLong(keyValue.longValue()));
                }
            }
        }
    }

    /**
     *  Automatically create next key for a given class.
     */
    public synchronized long nextKey(final Class<? extends RealmObject> clazz) {
        if (keys == null) {
            throw new IllegalStateException("not initialized yet");
        }
        AtomicLong l = keys.get(clazz);
        if (l == null) {
            logger.w("There was no primary keys for " + clazz.getName());
            //RealmConfiguration#getRealmObjectClasses() returns only classes with existing instances
            //so we need to store value for the first instance created
            l = new AtomicLong(0);
            keys.put(clazz, l);
        }
        return l.incrementAndGet();
    }
}
