package todo.fika.fikatodo.week;

import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.dift.ui.SwipeToAction;
import todo.fika.fikatodo.R;
import todo.fika.fikatodo.model.FikaTodo;
import todo.fika.fikatodo.util.DateUtils;
import todo.fika.fikatodo.util.Logger;
import todo.fika.fikatodo.util.ViewUtils;

@EActivity(R.layout.activity_fika_week)
public class FikaWeekActivity extends AppCompatActivity {
    final Logger logger = Logger.Factory.getLogger(getClass());

    @ViewById
    TextView toolbarTitle;

    @ViewById
    RecyclerView recyclerView;

    @InstanceState
    int weekDay;

    private SwipeToAction swipeToAction;

    @AfterInject
    void afterInject() {
        weekDay = DateUtils.getWeekDay();
        logger.d("weekDay: %d", weekDay);
    }

    @AfterViews
    void afterViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        toolbarTitle.setText(String.format("%s %s", DateUtils.getTitle().toUpperCase(), DateUtils.getOrdinalString(DateUtils.getWeekOfMonth()).toUpperCase()));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new FikaTodoAdapter(getDummyData()));

        swipeToAction = new SwipeToAction(recyclerView, new SwipeToAction.SwipeListener() {
            @Override
            public boolean swipeLeft(Object itemData) {
                return true;
            }

            @Override
            public boolean swipeRight(Object itemData) {
                return true;
            }

            @Override
            public void onClick(Object itemData) {

            }

            @Override
            public void onLongClick(Object itemData) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(ViewUtils.colorByWeekDay(weekDay)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private List<FikaTodo> getDummyData() {
        List<FikaTodo> todos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            FikaTodo todo = new FikaTodo();
            todo.setContent("못할 목숨이 어디 쓸쓸한 때문이다. 인간이 사랑의 같으며");
            todos.add(todo);
        }

        return todos;
    }

}
