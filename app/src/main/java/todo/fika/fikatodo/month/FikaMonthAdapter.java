package todo.fika.fikatodo.month;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import todo.fika.fikatodo.R;
import todo.fika.fikatodo.util.DateUtils;

/**
 * Created by AidenChoi on 2016. 1. 24..
 */
public class FikaMonthAdapter extends RecyclerView.Adapter<FikaMonthAdapter.FikaMonthViewHolder> {
    private final int START_WEEK_OF_MONTH = 1;
    private final int CURRRENT_WEEK_OF_MONTH;
    private final int END_WEEK_OF_MONTH;

    public FikaMonthAdapter() {
        Calendar calendar = Calendar.getInstance();
        CURRRENT_WEEK_OF_MONTH = calendar.get(Calendar.WEEK_OF_MONTH);
        END_WEEK_OF_MONTH = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
    }

    @Override
    public FikaMonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FikaMonthViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_month, parent, false));
    }

    @Override
    public void onBindViewHolder(FikaMonthViewHolder holder, int position) {
        holder.monthTitle.setText(DateUtils.getOrdinalString(position + 1).toUpperCase());
        setBackground(holder.monthTitle, position);
    }

    @Override
    public int getItemCount() {
        return END_WEEK_OF_MONTH;
    }

    public void setBackground(@NonNull TextView textView, int position) {
        switch (position) {
            case 0:
                textView.setBackgroundResource(R.color.color1stWeekBackground);
                break;
            case 1:
                textView.setBackgroundResource(R.color.color2ndWeekBackground);
                break;
            case 2:
                textView.setBackgroundResource(R.color.color3rdWeekBackground);
                break;
            case 3:
                textView.setBackgroundResource(R.color.color4thWeekBackground);
                break;
            case 4:
                textView.setBackgroundResource(R.color.color5thWeekBackground);
                break;
            case 5:
                textView.setBackgroundResource(R.color.color6thWeekBackground);
                break;
            case 6:
                textView.setBackgroundResource(R.color.color7thWeekBackground);
                break;
        }
    }

    class FikaMonthViewHolder extends RecyclerView.ViewHolder {
        TextView monthTitle;

        public FikaMonthViewHolder(View itemView) {
            super(itemView);

            monthTitle = (TextView) itemView.findViewById(R.id.monthTitle);
        }
    }
}
