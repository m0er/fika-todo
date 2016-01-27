package todo.fika.fikatodo.month;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import todo.fika.fikatodo.R;
import todo.fika.fikatodo.util.DateUtils;
import todo.fika.fikatodo.util.ViewUtils;
import todo.fika.fikatodo.view.decoration.GridSpaceItemDecoration;

@EActivity(R.layout.activity_fika_month)
public class FikaMonthActivity extends AppCompatActivity {

    @ViewById
    TextView title;

    @ViewById
    RecyclerView monthListView;

    private FikaMonthAdapter adapter;

    @AfterViews
    void afterViews() {
        title.setText(DateUtils.getTitle().toUpperCase());

        monthListView.addItemDecoration(new GridSpaceItemDecoration(((GridLayoutManager) monthListView.getLayoutManager()).getSpanCount(), ViewUtils.pixelByDp(getResources(), 8), false));

        if (adapter == null) {
            adapter = new FikaMonthAdapter();
        }

        monthListView.setAdapter(adapter);
    }
}
