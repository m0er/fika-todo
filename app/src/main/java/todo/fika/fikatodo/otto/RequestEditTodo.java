package todo.fika.fikatodo.otto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by AidenChoi on 2016. 3. 20..
 */
@NoArgsConstructor
public class RequestEditTodo {

    public RequestEditTodo(long todoId) {
        this.todoId = todoId;
    }

    @Getter
    private long todoId;
}
