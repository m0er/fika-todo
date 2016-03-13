package todo.fika.fikatodo.today;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

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

@EActivity(R.layout.activity_fika_today)
public class FikaTodayActivity extends AppCompatActivity {
    final Logger logger = Logger.Factory.getLogger(getClass());

    @ViewById
    RecyclerView recyclerView;

    @ViewById
    DrawerLayout drawerLayout;

    @ViewById
    RecyclerView drawerContent;

    private ActionBarDrawerToggle drawerToggle;

    @InstanceState
    int weekDay;

    private List<FikaTodo> todos;
    private SwipeToAction swipeToAction;

    @AfterInject
    void afterInject() {
        weekDay = DateUtils.getWeekDay();
        logger.d("weekDay: %d", weekDay);
        todos = getDummyData();
    }

    @AfterViews
    void afterViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("TODAY");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_first);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            // 드로어 관련 리스너 등록.
        };
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new FikaTodayAdapter(weekDay, todos));

        swipeToAction = new SwipeToAction(recyclerView, new SwipeToAction.SwipeListener<FikaTodo>() {
            @Override
            public boolean swipeLeft(FikaTodo itemData) {
                int pos = todos.indexOf(itemData);
                todos.remove(itemData);
                recyclerView.getAdapter().notifyItemRemoved(pos + 1);
                recyclerView.getAdapter().notifyItemChanged(0);
                return true;
            }

            @Override
            public boolean swipeRight(FikaTodo itemData) {
                itemData.setChecked(true);
                int pos = todos.indexOf(itemData);
                recyclerView.getAdapter().notifyItemChanged(pos + 1);
                recyclerView.getAdapter().notifyItemChanged(0);
                return true;
            }

            @Override
            public void onClick(FikaTodo itemData) {
                Snackbar.make(recyclerView, "Click! " + (itemData == null ? "null" : itemData.toString()), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(FikaTodo itemData) {
                Snackbar.make(recyclerView, "LongClick! " + (itemData == null ? "null" : itemData.toString()), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private List<FikaTodo> getDummyData() {
        List<FikaTodo> todos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            FikaTodo todo = new FikaTodo();
            todo.setId(i);
            todo.setContent("못할 목숨이 어디 쓸쓸한 때문이다. 인간이 사랑의 같으며");
            todos.add(todo);
        }

        return todos;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}
