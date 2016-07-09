package it.polito.group2.restaurantowner.user.restaurant_page;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.firebasedata.TableReservation;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.Review;
import it.polito.group2.restaurantowner.owner.ItemTouchHelperAdapter;
import it.polito.group2.restaurantowner.owner.SimpleItemTouchHelperCallback;

/**
 * Created by Alessio on 27/04/2016.
 */
public class My_Reservations_Adapter extends RecyclerView.Adapter<My_Reservations_Adapter.ReservationViewHolder> {

    ArrayList<TableReservation> user_TableReservations = new ArrayList<TableReservation>();
    private Context context;
    private ProgressDialog progressDialog;

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
            cv = (CardView) itemView.findViewById(R.id.cv);
            date = (TextView) itemView.findViewById(R.id.date_my_reservation);
            restaurant_name = (TextView) itemView.findViewById(R.id.restaurant_name_my_reservation);
            guests = (TextView) itemView.findViewById(R.id.guests_my_reservation);
            call_button = (ImageButton) itemView.findViewById(R.id.call_button_my_reservation);
            delete_button = (ImageButton) itemView.findViewById(R.id.delete_button_my_reservation);
            notes = (TextView) itemView.findViewById(R.id.notes_my_reservation);
        }
    }


    My_Reservations_Adapter(Context context, ArrayList<TableReservation> user_TableReservations, ProgressDialog progressDialog) {
        this.user_TableReservations = user_TableReservations;
        this.context = context;
        this.progressDialog = progressDialog;
    }

    @Override
    public ReservationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_my_reservation_item, viewGroup, false);
        ReservationViewHolder pvh = new ReservationViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final ReservationViewHolder reservationViewHolder, final int i) {
        //prepare all data

        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(user_TableReservations.get(i).getTable_reservation_date());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        //display data
        reservationViewHolder.date.setText(format.format(date.getTime()));

        String resId = user_TableReservations.get(i).getRestaurant_id();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants/" + resId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Restaurant snap_restaurant = snapshot.getValue(Restaurant.class);
                String restaurant_name = snap_restaurant.getRestaurant_name();
                final String restaurant_telephone_number = snap_restaurant.getRestaurant_telephone_number();
                reservationViewHolder.restaurant_name.setText(restaurant_name);

                //call setting
                reservationViewHolder.call_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //restaurant_number = "+39095365265";
                        if (restaurant_telephone_number == null) {
                            Toast.makeText(context, R.string.telephone_number_not_provided, Toast.LENGTH_SHORT);
                        } else {
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

                reservationViewHolder.guests.setText("x" + user_TableReservations.get(i).getTable_reservation_guests_number());
                reservationViewHolder.notes.setText(user_TableReservations.get(i).getTable_reservation_notes());
                FirebaseUtil.hideProgressDialog(progressDialog);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
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


}
