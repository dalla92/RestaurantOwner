package it.polito.group2.restaurantowner.owner.reservations;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.RemoveListenerUtil;
import it.polito.group2.restaurantowner.firebasedata.TableReservation;

public class TableFragment extends Fragment {

    private ArrayList<TableReservation> reservation_list;
    private ArrayList<TableReservation> reservationDate;
    private BaseAdapter adapter;
    private View rootView;
    private FirebaseDatabase firebase;
    private Query q;
    private ValueEventListener l;
    private ProgressDialog mProgressDialog;
    private Calendar targetDate;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_table_reservation, container, false);

        Bundle bundle = getArguments();
        String restaurantId = bundle.getString("restaurant_id");
        
        mProgressDialog = FirebaseUtil.initProgressDialog(getActivity());
        FirebaseUtil.showProgressDialog(mProgressDialog);

        firebase = FirebaseDatabase.getInstance();

        targetDate = Calendar.getInstance();

        if(reservation_list==null)
            reservation_list = new ArrayList<>();

        q = firebase.getReference("table_reservations").orderByChild("restaurant_id").equalTo(restaurantId);
        l = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reservation_list = new ArrayList<>();
                reservationDate = new ArrayList<>();
                for(DataSnapshot data: dataSnapshot.getChildren()) {
                    TableReservation res = data.getValue(TableReservation.class);
                    reservation_list.add(res);
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(res.getTable_reservation_date());
                    if (isEqualTo(c, targetDate))
                        reservationDate.add(res);
                }

                TextView reservation_title = (TextView) rootView.findViewById(R.id.reservation_list_title);
                if(reservationDate.isEmpty()) {
                    reservation_title.setText("");
                    Toast.makeText(getActivity(), getResources().getString(R.string.none_reservations), Toast.LENGTH_SHORT).show();
                }
                else
                    reservation_title.setVisibility(View.GONE);

                FirebaseUtil.hideProgressDialog(mProgressDialog);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        ListView lv = (ListView) rootView.findViewById(R.id.table_list_view);
        adapter = new BaseAdapter() {

            @Override
            public int getCount() {
                if(reservationDate == null)
                    return 0;
                return reservationDate.size();
            }

            @Override
            public Object getItem(int position) {
                return reservationDate.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = inflater.inflate(R.layout.table_reservation_item, parent, false);
                }

                SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                TextView text_client_name = (TextView) convertView.findViewById(R.id.reservation_client);
                TextView text_time = (TextView) convertView.findViewById(R.id.table_reservation_time);
                TextView text_people = (TextView) convertView.findViewById(R.id.reservation_people);
                TextView text_notes = (TextView) convertView.findViewById(R.id.reservation_notes);

                TableReservation reservation = reservationDate.get(position);
                text_client_name.setText(reservation.getUser_full_name());
                Calendar c =  Calendar.getInstance();
                c.setTimeInMillis(reservation.getTable_reservation_date());
                text_time.setText(timeFormat.format(c.getTime()));
                text_people.setText(String.format("%d %s", reservation.getTable_reservation_guests_number(), getString(R.string.reservation_people)));
                text_notes.setText(reservation.getTable_reservation_notes());

                ImageView delete = (ImageView) convertView.findViewById(R.id.table_reservation_delete);
                Calendar today = Calendar.getInstance();
                Calendar target = Calendar.getInstance();
                target.setTimeInMillis(reservation.getTable_reservation_date());
                if(target.after(today)) {

                    delete.setClickable(true);
                    delete.setVisibility(View.VISIBLE);

                    delete.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setTitle(getResources().getString(R.string.action_confirm));
                            alert.setMessage(getResources().getString(R.string.sure_delete_reservation));
                            alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reservationDate.remove(position);
                                    notifyDataSetChanged();
                                    dialog.dismiss();

                                }
                            });
                            alert.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });

                            alert.show();
                        }
                    });
                }
                else{
                    delete.setClickable(false);
                    delete.setVisibility(View.GONE);
                }
                return convertView;
            }
        };

        lv.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        q.addValueEventListener(l);
    }
    @Override
    public void onStop() {
        super.onStop();
        RemoveListenerUtil.remove_value_event_listener(q, l);
    }

    public void changeData(Calendar date){
        targetDate = date;
        reservationDate = new ArrayList<>();
        for(TableReservation res: reservation_list){
            Calendar c =  Calendar.getInstance();
            c.setTimeInMillis(res.getTable_reservation_date());
            if(isEqualTo(c, date))
                reservationDate.add(res);
        }

        adapter.notifyDataSetChanged();
    }

    private boolean isEqualTo(Calendar target, Calendar date){
        return (target.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                target.get(Calendar.MONTH) == date.get(Calendar.MONTH) &&
                target.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH));
    }
}
