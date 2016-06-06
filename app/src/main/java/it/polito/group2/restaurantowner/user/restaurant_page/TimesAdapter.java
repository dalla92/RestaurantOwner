package it.polito.group2.restaurantowner.user.restaurant_page;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.RestaurantTimeSlot;

/**
 * Created by TheChuck on 05/06/2016.
 */
public class TimesAdapter extends  RecyclerView.Adapter<TimesAdapter.TimesViewHolder> {

    private ArrayList<RestaurantTimeSlot> times;
    private Context context;

    public TimesAdapter(ArrayList<RestaurantTimeSlot> times, Context context) {
        this.times = times;
        this.context = context;
    }

    @Override
    public TimesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_item, parent, false);
        return new TimesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TimesViewHolder holder, int position) {
        RestaurantTimeSlot time = times.get(position);
        holder.time_day.setText(getNameFromInt(time.getDay_of_week()));

        if(!time.getLunch() && !time.getDinner()) {
            holder.time_lunch.setText(context.getString(R.string.close));
            holder.time_dinner.setVisibility(View.GONE);
        }

        if(time.getLunch() && time.getDinner()) {
            holder.time_lunch.setText(time.getOpen_lunch_time() + "-" + time.getClose_lunch_time());
            holder.time_dinner.setText(time.getOpen_dinner_time() + "-" + time.getClose_dinner_time());


        }

        if(time.getLunch() && !time.getDinner()){
            holder.time_lunch.setText(time.getOpen_lunch_time() + "-" + time.getClose_lunch_time());
            holder.time_dinner.setVisibility(View.GONE);
        }

        if(!time.getLunch() && time.getDinner()){
            holder.time_lunch.setVisibility(View.GONE);
            holder.time_dinner.setText(time.getOpen_dinner_time() + "-" + time.getClose_dinner_time());

        }

    }

    private String getNameFromInt(int day_of_week) {
        switch(day_of_week){
            case 0:
                return context.getString(R.string.times_Mon);
            case 1:
                return context.getString(R.string.times_Tue);
            case 2:
                return context.getString(R.string.times_Wed);
            case 3:
                return context.getString(R.string.times_Thu);
            case 4:
                return context.getString(R.string.times_Fri);
            case 5:
                return context.getString(R.string.times_Sat);
            case 6:
                return context.getString(R.string.times_Sun);
            default:
                return "";
        }
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    public class TimesViewHolder extends RecyclerView.ViewHolder {
        public TextView time_day, time_lunch, time_dinner;

        public TimesViewHolder(View view) {
            super(view);
            time_day = (TextView) view.findViewById(R.id.time_day);
            time_lunch = (TextView) view.findViewById(R.id.time_lunch);
            time_dinner = (TextView) view.findViewById(R.id.time_dinner);
        }

    }
}
