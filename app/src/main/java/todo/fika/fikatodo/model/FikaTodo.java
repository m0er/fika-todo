package todo.fika.fikatodo.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

/**
 * Created by AidenChoi on 2016. 2. 9..
 */
@Data
public class FikaTodo extends RealmObject {

    @PrimaryKey
    private long id;

    private Date createdDate;
    private Date updatedDate;
    private String content;
    private boolean completed;

    public void toggleCompleted() {
        setCompleted(!completed);
    }
}
