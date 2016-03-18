package todo.fika.fikatodo.today;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import co.dift.ui.SwipeToAction;
import todo.fika.fikatodo.R;
import todo.fika.fikatodo.model.FikaTodo;
import todo.fika.fikatodo.util.Logger;
import todo.fika.fikatodo.util.ViewUtils;

import static todo.fika.fikatodo.util.Const.TYPE_FOOTER;
import static todo.fika.fikatodo.util.Const.TYPE_TODO;

/**
 * Created by AidenChoi on 2016. 2. 9..
 */
public class FikaTodayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Logger logger = Logger.Factory.getLogger(getClass());
    private List<FikaTodo> todos;

    public FikaTodayAdapter(List<FikaTodo> todos) {
        this.todos = todos;
    }

    private class FikaTodoViewHoler extends SwipeToAction.ViewHolder<FikaTodo> {
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

    private class FikaFooterViewHolder extends RecyclerView.ViewHolder {
        EditText typeNewTodo;

        public FikaFooterViewHolder(View itemView) {
            super(itemView);

            typeNewTodo = (EditText) itemView.findViewById(R.id.typeNewTodo);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_FOOTER) {
            return new FikaFooterViewHolder(inflater.inflate(R.layout.item_today_footer, parent, false));
        } else {
            return new FikaTodoViewHoler(inflater.inflate(R.layout.item_today_todo, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_FOOTER) {
            final FikaFooterViewHolder viewHolder = (FikaFooterViewHolder) holder;
            viewHolder.typeNewTodo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        FikaTodo newTodo = new FikaTodo();
                        newTodo.setContent(v.getText().toString());
                        newTodo.setCreatedDate(new Date());
                        todos.add(newTodo);
                        v.setText("");
                        ((FikaTodayActivity) v.getContext()).hideKeyboard();
                        ((FikaTodayActivity) v.getContext()).updateInCompleteTodoCount();
                        notifyDataSetChanged();
                        return true;
                    }
                    return false;
                }
            });
        } else {
            FikaTodo todo = todos.get(position);
            FikaTodoViewHoler viewHoler = (FikaTodoViewHoler) holder;
            viewHoler.data = todo;
            viewHoler.todoContent.setText(todo.getContent());

            if (todo.isCompleted()) {
                viewHoler.todoContent.setTextColor(ViewUtils.getColor(R.color.colorTextCompleted));
                viewHoler.todoContent.setPaintFlags(viewHoler.todoContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                viewHoler.todoContent.setTextColor(ViewUtils.getColor(R.color.colorTextPrimary));
                viewHoler.todoContent.setPaintFlags(viewHoler.todoContent.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_TODO;
        }
    }

    @Override
    public int getItemCount() {
        return todos.size() + 1; // footer
    }

}
