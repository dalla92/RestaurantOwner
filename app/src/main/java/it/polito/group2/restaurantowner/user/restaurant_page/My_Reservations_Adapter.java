package it.polito.group2.restaurantowner.user.restaurant_page;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.firebasedata.TableReservation;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.Review;
import it.polito.group2.restaurantowner.owner.ItemTouchHelperAdapter;

/**
 * Created by Alessio on 27/04/2016.
 */
public class My_Reservations_Adapter extends RecyclerView.Adapter<My_Reservations_Adapter.ReservationViewHolder> implements ItemTouchHelperAdapter {

    ArrayList<TableReservation> user_TableReservations = new ArrayList<TableReservation>();
    List<Restaurant> resList = new ArrayList<Restaurant>();
    private String chosen_year, chosen_month, chosen_day, chosen_hour, chosen_minute, chosen_weekday;
    private Calendar last_chosen_date;
    private int index;
    private String restaurant_name;
    private Context context;
    private String restaurant_telephone_number;

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



    My_Reservations_Adapter(Context context, ArrayList<TableReservation> user_TableReservations){
        this.user_TableReservations = user_TableReservations;
        this.context = context;
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
        index = i;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot resSnapshot: snapshot.getChildren()) {
                    Restaurant snap_restaurant = resSnapshot.getValue(Restaurant.class);
                    String snap_restaurant_id = snap_restaurant.getRestaurant_id();
                    if(snap_restaurant_id.equals(user_TableReservations.get(index).getRestaurant_id())){
                        restaurant_name = snap_restaurant.getRestaurant_name();
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        last_chosen_date = user_TableReservations.get(i).getTable_reservation_date();
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
        if(restaurant_name != null)
            reservationViewHolder.restaurant_name.setText(restaurant_name);
        reservationViewHolder.guests.setText("x"+user_TableReservations.get(i).getTable_reservation_guests_number());
        reservationViewHolder.notes.setText(user_TableReservations.get(i).getTable_reservation_notes());

        //call setting
        reservationViewHolder.call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants/");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot resSnapshot: snapshot.getChildren()) {
                            Restaurant snap_restaurant = resSnapshot.getValue(Restaurant.class);
                            String snap_restaurant_id = snap_restaurant.getRestaurant_id();
                            if(snap_restaurant_id.equals(user_TableReservations.get(index).getRestaurant_id())){
                                restaurant_telephone_number = snap_restaurant.getRestaurant_telephone_number();
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });
                //restaurant_number = "+39095365265";
                if(restaurant_telephone_number==null){
                    Toast.makeText(context, R.string.telephone_number_not_provided, Toast.LENGTH_SHORT);
                }
                else {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getResources().getString(R.string.call_confirmation))
                            .setMessage(
                                    context.getResources().getString(R.string.call_confirmation_message))
                            .setIcon(
                                    context.getResources().getDrawable(
                                            android.R.drawable.ic_dialog_alert))
                            .setPositiveButton(
                                    context.getResources().getString(R.string.yes),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            //call
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel:" + restaurant_telephone_number));
                                            try {
                                                context.startActivity(callIntent);
                                            } catch (SecurityException s) {
                                                Log.e("EXCEPTION", "EXCEPTION SECURITY IN my_reservation_call");
                                            }
                                        }

                                    })
                            .setNegativeButton(
                                    context.getResources().getString(R.string.no),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            //Do Something Here
                                        }
                                    }).show();
                }
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return user_TableReservations.size();
    }

    public void addItem(int position, TableReservation m) {
        user_TableReservations.add(position, m);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, user_TableReservations.size());
    }

    public void removeItem(int position) {
        user_TableReservations.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, user_TableReservations.size());
    }


    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        TableReservation prev = user_TableReservations.remove(fromPosition);
        user_TableReservations.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(final int position) {
        if(!user_TableReservations.get(position).getTable_reservation_date().after(Calendar.getInstance())){
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Attention!");
            alert.setMessage("You can not delete a reservation at this moment! Please call the restaurant");
        }
        else {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Confirmation!");
            alert.setMessage("Are you sure you want to delete the restaurant?\nThe operation cannot be undone!");
            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String TableReservation_key = user_TableReservations.get(position).getTable_reservation_id();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/table_reservations/" + TableReservation_key);
                    //delete
                    ref.setValue(null);
                    removeItem(position);
                    dialog.dismiss();

                }
            });
            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    notifyDataSetChanged();
                    dialog.dismiss();
                }
            });

            alert.show();
        }
    }
}
