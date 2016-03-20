package todo.fika.fikatodo;

import android.app.Application;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import todo.fika.fikatodo.realm.PrimaryKeyFactory;
import todo.fika.fikatodo.util.ViewUtils;

/**
 * Created by AidenChoi on 2016. 1. 14..
 */
@EApplication
public class FikaTodoApplication extends Application {

    @Bean
    PrimaryKeyFactory primaryKeyFactory;

    @Override
    public void onCreate() {
        super.onCreate();

        ViewUtils.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("fika.realm")
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(config);

        primaryKeyFactory.initialize(Realm.getDefaultInstance());
    }

    private RealmMigration getRealmMigration(){
        return new RealmMigration() {

            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                RealmSchema schema = realm.getSchema();

                if (oldVersion == 0) {
                    schema.create("FikaTodo")
                            .addPrimaryKey("id")
                            .addField("createdDate", Date.class)
                            .addField("updatedDate", Date.class)
                            .addField("content", String.class)
                            .addField("completed", boolean.class);
                    oldVersion++;
                }
            }
        };
    }
}
