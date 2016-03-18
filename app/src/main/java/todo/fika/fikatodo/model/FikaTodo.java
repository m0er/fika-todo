package todo.fika.fikatodo.model;

import java.util.Date;

import lombok.Data;

/**
 * Created by AidenChoi on 2016. 2. 9..
 */
@Data
public class FikaTodo {
    private int id;
    private Date createdDate;
    private Date updatedDate;
    private String content;
    private boolean completed;
}
