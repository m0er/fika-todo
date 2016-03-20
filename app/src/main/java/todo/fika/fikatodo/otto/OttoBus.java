package todo.fika.fikatodo.otto;

import com.squareup.otto.Bus;

import org.androidannotations.annotations.EBean;

/**
 * Created by AidenChoi on 2016. 3. 20..
 */
@EBean(scope = EBean.Scope.Singleton)
public class OttoBus extends Bus {
}
