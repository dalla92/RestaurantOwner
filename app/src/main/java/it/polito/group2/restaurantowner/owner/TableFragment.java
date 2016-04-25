package it.polito.group2.restaurantowner.owner;

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

import org.json.JSONException;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import it.polito.group2.restaurantowner.R;

public class TableFragment extends Fragment {

    private ArrayList<TableReservation> reservation_list;
    private BaseAdapter adapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_table_reservation, container, false);

        Bundle bundle = getArguments();
        long date_millis = bundle.getLong("date");
        String restaurantId = bundle.getString("id");
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(date_millis);
        reservation_list = getDataJson(date, restaurantId);

        TextView reservation_title = (TextView) rootView.findViewById(R.id.reservation_list_title);
        if(reservation_list.isEmpty()) {
            reservation_title.setVisibility(View.VISIBLE);
            reservation_title.setText(getString(R.string.no_reservation));
        }
        else
            reservation_title.setVisibility(View.GONE);

        ListView lv = (ListView) rootView.findViewById(R.id.table_list_view);
        adapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return reservation_list.size();
            }

            @Override
            public Object getItem(int position) {
                return reservation_list.get(position);
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
                TextView text_time = (TextView) convertView.findViewById(R.id.reservation_time);
                TextView text_people = (TextView) convertView.findViewById(R.id.reservation_people);
                TextView text_notes = (TextView) convertView.findViewById(R.id.reservation_notes);

                TableReservation reservation = reservation_list.get(position);
                text_client_name.setText(reservation.getUsername());
                text_time.setText(timeFormat.format(reservation.getDate().getTime()));
                text_people.setText(String.format("%d %s", reservation.getN_people(), getString(R.string.reservation_people)));
                text_notes.setText(reservation.getNotes());

                ImageView delete = (ImageView) convertView.findViewById(R.id.table_reservation_delete);
                Calendar today = Calendar.getInstance();
                Calendar target = reservation.getDate();
                if(target.after(today.getTime()) ||
                        (target.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        target.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                        target.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH))) {

                    delete.setClickable(true);
                    delete.setVisibility(View.VISIBLE);

                    delete.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setTitle("Confirmation!");
                            alert.setMessage("Are you sure you want to delete this reservation?\nThe operation cannot be undone!");
                            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reservation_list.remove(position);
                                    notifyDataSetChanged();
                                    dialog.dismiss();

                                }
                            });
                            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

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

    private ArrayList<TableReservation> getDataJson(Calendar date, String restaurantId) {
        /*ArrayList<TableReservation> reservations = new ArrayList<>();
        TableReservation res1 = new TableReservation("Andrea Cuiuli", 4 , date, "Una persona allergica alle noci", restaurantId);
        TableReservation res2 = new TableReservation("Andrea Cuiuli", 4 , date, "Una persona allergica alle noci", restaurantId);
        TableReservation res3 = new TableReservation("Andrea Cuiuli", 4 , date, "Una persona allergica alle noci", restaurantId);
        reservations.add(res1);
        reservations.add(res2);
        reservations.add(res3);*/

        try {
            return JSONUtil.readJSONTableResList(getActivity(), date, restaurantId);
        } catch (JSONException e) {
            return new ArrayList<>();
        }
    }

    public void changeData(Calendar date, String restaurantId){
        reservation_list = getDataJson(date, restaurantId);
        adapter.notifyDataSetChanged();
    }

    private String getMonthName(int month){
        return new DateFormatSymbols().getMonths()[month];
    }
}
