package it.polito.group2.restaurantowner.user.restaurant_page;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.Restaurant;
import it.polito.group2.restaurantowner.data.TableReservation;

/**
 * Created by Alessio on 27/04/2016.
 */
public class My_Reservations_Adapter extends RecyclerView.Adapter<My_Reservations_Adapter.ReservationViewHolder>{

    ArrayList<TableReservation> user_table_reservations = new ArrayList<TableReservation>();
    List<Restaurant> resList = new ArrayList<Restaurant>();
    private String chosen_year, chosen_month, chosen_day, chosen_hour, chosen_minute, chosen_weekday;
    private Calendar last_chosen_date;

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView date;
        TextView restaurant_name;
        TextView guests;
        ImageButton call_button;
        ImageButton delete_button;
        TextView notes;

        ReservationViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            date = (TextView)itemView.findViewById(R.id.date_my_reservation);
            restaurant_name = (TextView)itemView.findViewById(R.id.restaurant_name_my_reservation);
            guests = (TextView)itemView.findViewById(R.id.guests_my_reservation);
            call_button = (ImageButton)itemView.findViewById(R.id.call_button_my_reservation);
            delete_button = (ImageButton)itemView.findViewById(R.id.delete_button_my_reservation);
            notes = (TextView)itemView.findViewById(R.id.notes_my_reservation);
        }
    }



    My_Reservations_Adapter(Context context, ArrayList<TableReservation> user_table_reservations){
        this.user_table_reservations = user_table_reservations;
        try {
            resList = JSONUtil.readJSONResList(context);
        } catch (JSONException e) {
            Log.e("EXCEPTION", "EXCETPION IN READING THE FILE IN My_Reservations_Adapter");
        }
    }

    @Override
    public int getItemCount() {
        return user_table_reservations.size();
    }

    @Override
    public ReservationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_my_reservation, viewGroup, false);
        ReservationViewHolder pvh = new ReservationViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ReservationViewHolder reservationViewHolder, int i) {
        //prepare all data
        String res_name = findRestaurantNameById(user_table_reservations.get(i).getRestaurantId());
        last_chosen_date = user_table_reservations.get(i).getDate();
        chosen_day = Integer.toString(last_chosen_date.get(Calendar.DAY_OF_MONTH));
        chosen_weekday = Integer.toString(last_chosen_date.get(Calendar.DAY_OF_WEEK));
        chosen_month = Integer.toString(last_chosen_date.get(Calendar.MONTH) + 1); //need to correct +1
        chosen_year = Integer.toString(last_chosen_date.get(Calendar.YEAR));
        chosen_hour = Integer.toString(last_chosen_date.get(Calendar.HOUR));
        chosen_minute = Integer.toString(last_chosen_date.get(Calendar.MINUTE));
        String date_to_display = chosen_day + "/" + chosen_month + "/" + chosen_year + " " + chosen_hour + ":" + chosen_minute;

        //display data
        if(date_to_display != null)
            reservationViewHolder.date.setText(date_to_display);
        if(res_name != null)
            reservationViewHolder.restaurant_name.setText(res_name);
        reservationViewHolder.guests.setText("x"+user_table_reservations.get(i).getN_people());
        reservationViewHolder.notes.setText(user_table_reservations.get(i).getNotes());
        if(last_chosen_date.after(Calendar.getInstance())){
            //reservationViewHolder.delete_button.setVisibility(View.GONE);
            reservationViewHolder.delete_button.setImageResource(R.mipmap.ic_calendar_appointment);
            reservationViewHolder.delete_button.setEnabled(false);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private String findRestaurantNameById(String res_id){
        for(Restaurant r : resList){
            if(r.getRestaurantId().equals(res_id)){
                return r.getName();
            }
        }
        return null;
    }
}
