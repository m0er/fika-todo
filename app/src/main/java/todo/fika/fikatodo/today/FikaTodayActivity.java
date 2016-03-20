package todo.fika.fikatodo.today;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.github.pavlospt.CircleView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import co.dift.ui.SwipeToAction;
import hirondelle.date4j.DateTime;
import io.realm.RealmResults;
import todo.fika.fikatodo.R;
import todo.fika.fikatodo.model.FikaTodo;
import todo.fika.fikatodo.realm.PrimaryKeyFactory;
import todo.fika.fikatodo.util.Const.Animation;
import todo.fika.fikatodo.util.DateUtils;
import todo.fika.fikatodo.util.ViewUtils;
import todo.fika.fikatodo.view.BaseAppCompatActivity;

@EActivity(R.layout.activity_fika_today)
public class FikaTodayActivity extends BaseAppCompatActivity {

    @ViewById
    View rootView;

    @ViewById
    RecyclerView recyclerView;

    @ViewById
    DrawerLayout drawerLayout;

    @ViewById
    RecyclerView drawerContent;

    @ViewById
    CircleView todoCount;

    @ViewById
    TextView dayTitle;

    @ViewById
    TextView todayText;

    private ActionBarDrawerToggle drawerToggle;

    @InstanceState
    DateTime now;

    @InstanceState
    int weekDay;

    @Bean
    PrimaryKeyFactory primaryKeyFactory;

    private SwipeToAction swipeToAction;

    @AfterInject
    void afterInject() {
        now = DateUtils.now();
        weekDay = DateUtils.getWeekDay();
        logger.d("weekDay: %d", weekDay);
    }

    @AfterViews
    void afterViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("DAY BY");
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
        recyclerView.setAdapter(new FikaTodayAdapter(primaryKeyFactory, getTodayTodos()));

        swipeToAction = new SwipeToAction(recyclerView, new SwipeToAction.SimpleSwipeListener<FikaTodo>() {
            @Override
            public boolean swipeLeft(FikaTodo itemData) {
                FikaTodo target = realm.where(FikaTodo.class).equalTo("id", itemData.getId()).findFirst();
                if (target != null) {
                    realm.beginTransaction();
                    target.removeFromRealm();
                    realm.commitTransaction();
                }

                recyclerView.getAdapter().notifyDataSetChanged();
                updateInCompleteTodoCount();
                return true;
            }

            @Override
            public boolean swipeRight(FikaTodo itemData) {
                FikaTodo target = realm.where(FikaTodo.class).equalTo("id", itemData.getId()).findFirst();
                if (target != null) {
                    realm.beginTransaction();
                    target.toggleCompleted();
                    realm.commitTransaction();
                }

                recyclerView.getAdapter().notifyDataSetChanged();
                updateInCompleteTodoCount();
                return true;
            }
        });
        swipeToAction.setResetAnimationDuration(Animation.SHORT);
        swipeToAction.setSwipeThresholdWidthRatio(3);
        swipeToAction.setMaxSwipeXPosition(ViewUtils.dpToPx(getResources(), 180));

        todoCount.setShowSubtitle(false);
        todoCount.setStrokeColor(ViewUtils.colorByWeekDay(weekDay));
        todoCount.setBackgroundColor(ViewUtils.colorByWeekDay(weekDay));
        todoCount.setFillColor(ViewUtils.colorByWeekDay(weekDay));
        dayTitle.setTextColor(ViewUtils.colorByWeekDay(weekDay));

        updateInCompleteTodoCount();
        dayTitle.setText(DateUtils.getTodayTitle().toUpperCase());
        todayText.setText(DateUtils.getTodayDateTime());
    }

    @Click
    void goCompletedTodos() {
        Snackbar.make(rootView, "Go history view", Snackbar.LENGTH_SHORT).show();
    }

    public void updateInCompleteTodoCount() {
        todoCount.setTitleText(String.valueOf(getIncompleteTodoCount()));
    }

    private int getIncompleteTodoCount() {
        return (int) realm.where(FikaTodo.class)
                .equalTo("completed", false)
                .between("createdDate", DateUtils.startDate(now), DateUtils.endDate(now))
                .count();
    }

    private RealmResults<FikaTodo> getTodayTodos() {
        return realm.where(FikaTodo.class)
                .between("createdDate", DateUtils.startDate(now), DateUtils.endDate(now))
                .findAll();
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

    /**
     * @see http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
     */
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
