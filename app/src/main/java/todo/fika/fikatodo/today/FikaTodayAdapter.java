package todo.fika.fikatodo.today;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.Date;

import co.dift.ui.SwipeToAction;
import io.realm.RealmResults;
import todo.fika.fikatodo.R;
import todo.fika.fikatodo.model.FikaTodo;
import todo.fika.fikatodo.otto.OttoBus;
import todo.fika.fikatodo.otto.RequestEditTodo;
import todo.fika.fikatodo.realm.PrimaryKeyFactory;
import todo.fika.fikatodo.realm.TransactionHelper;
import todo.fika.fikatodo.util.Const;
import todo.fika.fikatodo.util.FikaCallback;
import todo.fika.fikatodo.util.FikaUtils;
import todo.fika.fikatodo.util.Logger;
import todo.fika.fikatodo.util.ViewUtils;
import todo.fika.fikatodo.view.FikaEditText;

import static todo.fika.fikatodo.util.Const.TYPE_FOOTER;
import static todo.fika.fikatodo.util.Const.TYPE_TODO;

/**
 * Created by AidenChoi on 2016. 2. 9..
 */
public class FikaTodayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Logger logger = Logger.Factory.getLogger(getClass());
    private final OttoBus bus;
    private final PrimaryKeyFactory primaryKeyFactory;
    private RealmResults<FikaTodo> todos;
    private RequestEditTodo requestEditTodo;
    private TransactionHelper transactionHelper;

    public FikaTodayAdapter(OttoBus bus, PrimaryKeyFactory primaryKeyFactory, TransactionHelper transactionHelper, RealmResults<FikaTodo> todos) {
        this.bus = bus;
        this.todos = todos;
        this.primaryKeyFactory = primaryKeyFactory;
        this.transactionHelper = transactionHelper;
    }

    private class FikaTodoViewHolder extends SwipeToAction.ViewHolder<FikaTodo> {
        View revealLeft;
        View revealRight;
        TextView revealRightText;
        TextView todoContent;
        FikaEditText editTodoContent;

        public FikaTodoViewHolder(View v) {
            super(v);

            revealLeft = v.findViewById(R.id.revealLeft);
            revealRight = v.findViewById(R.id.revealRight);
            revealRightText = (TextView) v.findViewById(R.id.revealRightText);
            todoContent = (TextView) v.findViewById(R.id.todoContent);
            editTodoContent = (FikaEditText) v.findViewById(R.id.editTodoContent);
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
            return new FikaTodoViewHolder(inflater.inflate(R.layout.item_today_todo, parent, false));
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
                        addFikaTodo(v);
                        v.setText("");
                        ((FikaTodayActivity) v.getContext()).hideKeyboard();
                        ((FikaTodayActivity) v.getContext()).updateInCompleteTodoCount();
                        notifyDataSetChanged();
                        return true;
                    }
                    return false;
                }

                @NonNull
                private FikaTodo addFikaTodo(final TextView v) {
                    Date nowDate = new Date();
                    FikaTodo newTodo = new FikaTodo();
                    newTodo.setId(primaryKeyFactory.nextKey(FikaTodo.class));
                    newTodo.setContent(v.getText().toString());
                    newTodo.setCreatedDate(nowDate);
                    newTodo.setUpdatedDate(nowDate);

                    return transactionHelper.create(newTodo);
                }
            });
        } else {
            final FikaTodo todo = todos.get(position);
            final FikaTodoViewHolder viewHolder = (FikaTodoViewHolder) holder;
            viewHolder.data = todo;
            viewHolder.todoContent.setText(todo.getContent());

            if (requestEditTodo != null && requestEditTodo.getTodoId() == todo.getId()) {
                requestEditTodo = null;

                viewHolder.editTodoContent.setText(todo.getContent());
                viewHolder.editTodoContent.setVisibility(View.VISIBLE);
                viewHolder.editTodoContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            ((FikaTodayActivity) v.getContext()).hideKeyboard();

                            transactionHelper.transaction(new FikaCallback() {
                                @Override
                                public void callback() {
                                    // 내용이 없는 경우 제거.
                                    if (FikaUtils.isTextEmpty(v.getText().toString())) {
                                        todo.removeFromRealm();
                                        return;
                                    }

                                    todo.setContent(v.getText().toString().trim());
                                    todo.setUpdatedDate(new Date());
                                }
                            });

                            notifyDataSetChanged();

                            return true;
                        }
                        return false;
                    }
                });

                // 백버튼 눌러서 키보드 내린경우.
                viewHolder.editTodoContent.setOnKeyboardHideCallback(new FikaCallback() {
                    @Override
                    public void callback() {
                        notifyDataSetChanged();
                    }
                });

                // 키보드 업.
                viewHolder.editTodoContent.requestFocus();
                ((FikaTodayActivity) viewHolder.editTodoContent.getContext()).showKeyboard();

                viewHolder.todoContent.setVisibility(View.GONE);
            } else {
                viewHolder.editTodoContent.setVisibility(View.GONE);
                viewHolder.todoContent.setVisibility(View.VISIBLE);
            }

            if (todo.isCompleted()) {
                viewHolder.todoContent.setTextColor(ViewUtils.getColor(R.color.colorTextCompleted));
                viewHolder.todoContent.setPaintFlags(viewHolder.todoContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.revealRightText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.revealRightText.setText("cancel");
                    }
                }, Const.Animation.SHORT);
            } else {
                viewHolder.todoContent.setTextColor(ViewUtils.getColor(R.color.colorTextPrimary));
                viewHolder.todoContent.setPaintFlags(viewHolder.todoContent.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                viewHolder.revealRightText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.revealRightText.setText("complete");
                    }
                }, Const.Animation.SHORT);
            }
        }
    }

    @Subscribe
    public void requestEditTodo(RequestEditTodo requestEditTodo) {
        this.requestEditTodo = requestEditTodo;
        notifyDataSetChanged();
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        bus.register(this);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        bus.unregister(this);
    }
}
