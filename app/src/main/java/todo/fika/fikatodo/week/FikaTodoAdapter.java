package todo.fika.fikatodo.week;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.github.pavlospt.CircleView;

import java.util.List;

import co.dift.ui.SwipeToAction;
import todo.fika.fikatodo.R;
import todo.fika.fikatodo.model.FikaTodo;
import todo.fika.fikatodo.util.DateUtils;
import todo.fika.fikatodo.util.Logger;
import todo.fika.fikatodo.util.ViewUtils;

import static todo.fika.fikatodo.util.Const.TYPE_FOOTER;
import static todo.fika.fikatodo.util.Const.TYPE_HEADER;
import static todo.fika.fikatodo.util.Const.TYPE_TODO;

/**
 * Created by AidenChoi on 2016. 2. 9..
 */
public class FikaTodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Logger logger = Logger.Factory.getLogger(getClass());
    private final int weekDay;
    private List<FikaTodo> todos;

    public FikaTodoAdapter(int weekDay, List<FikaTodo> todos) {
        this.weekDay = weekDay;
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

    private class FikaHeaderViewHolder extends RecyclerView.ViewHolder {
        CircleView day;
        TextView dayTitle;
        TextView todoProgress;

        public FikaHeaderViewHolder(View itemView) {
            super(itemView);

            day = (CircleView) itemView.findViewById(R.id.day);
            day.setShowSubtitle(false);
            day.setStrokeColor(ViewUtils.colorByWeekDay(weekDay));
            day.setBackgroundColor(ViewUtils.colorByWeekDay(weekDay));
            day.setFillColor(ViewUtils.colorByWeekDay(weekDay));

            dayTitle = (TextView) itemView.findViewById(R.id.dayTitle);
            dayTitle.setTextColor(ViewUtils.colorByWeekDay(weekDay));

            todoProgress = (TextView) itemView.findViewById(R.id.todoProgress);
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
        if (viewType == TYPE_HEADER) {
            return new FikaHeaderViewHolder(inflater.inflate(R.layout.item_day_header, parent, false));
        } else if (viewType == TYPE_FOOTER) {
            return new FikaFooterViewHolder(inflater.inflate(R.layout.item_day_footer, parent, false));
        } else {
            return new FikaTodoViewHoler(inflater.inflate(R.layout.item_day_todo, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_HEADER) {
            FikaHeaderViewHolder viewHolder = (FikaHeaderViewHolder) holder;
            viewHolder.day.setTitleText(String.valueOf(DateUtils.getDay()));
            viewHolder.dayTitle.setText(DateUtils.getDayTitle().toUpperCase());
            updateTodoProgress(viewHolder.todoProgress);
        } else if (viewType == TYPE_FOOTER) {
            FikaFooterViewHolder viewHolder = (FikaFooterViewHolder) holder;
        } else {
            FikaTodo todo = todos.get(position - 1);
            FikaTodoViewHoler viewHoler = (FikaTodoViewHoler) holder;
            viewHoler.data = todo;
            viewHoler.todoContent.setText(todo.getContent());

            if (todo.isChecked()) {
                viewHoler.todoContent.setTextColor(ViewUtils.getColor(R.color.colorTextSecondary));
            } else {
                viewHoler.todoContent.setTextColor(ViewUtils.getColor(R.color.colorTextPrimary));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == TYPE_HEADER) {
            return TYPE_HEADER;
        } else if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_TODO;
        }
    }

    @Override
    public int getItemCount() {
        return todos.size() + 2; // header and footer
    }

    public void updateTodoProgress(TextView todoProgress) {
        int checkedCount = 0;
        for (FikaTodo todo : todos) {
            if (todo.isChecked()) {
                checkedCount++;
            }
        }
        todoProgress.setText(String.valueOf((int) (checkedCount / (float) todos.size() * 100)) + "% COMPLETED");
    }
}
