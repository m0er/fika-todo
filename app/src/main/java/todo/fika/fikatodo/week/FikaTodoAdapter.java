package todo.fika.fikatodo.week;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.dift.ui.SwipeToAction;
import todo.fika.fikatodo.R;
import todo.fika.fikatodo.model.FikaTodo;

/**
 * Created by AidenChoi on 2016. 2. 9..
 */
public class FikaTodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<FikaTodo> todos;

    public FikaTodoAdapter(List<FikaTodo> todos) {
        this.todos = todos;
    }

    class FikaTodoViewHoler extends SwipeToAction.ViewHolder<FikaTodo> {
        View revealLeft;
        View revealRight;
        TextView todoContent;

        public FikaTodoViewHoler(View v) {
            super(v);

            revealLeft = v.findViewById(R.id.revealLeft);
            revealRight = v.findViewById(R.id.revealRight);
            todoContent = (TextView) v.findViewById(R.id.todoContent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FikaTodoViewHoler(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_week_todo, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FikaTodo todo = todos.get(position);
        FikaTodoViewHoler viewHoler = (FikaTodoViewHoler) holder;
        viewHoler.todoContent.setText(todo.getContent());
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }
}
