package it.polito.group2.restaurantowner;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TableFragment extends Fragment {

    private ArrayList<TableReservation> reservation_list = getDataJson();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_table_reservation, container, false);

        ListView lv = (ListView) rootView.findViewById(R.id.table_list_view);
        BaseAdapter adapter = new BaseAdapter() {

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

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:MM");
                TextView text_client_name = (TextView) convertView.findViewById(R.id.reservation_client);
                TextView text_time = (TextView) convertView.findViewById(R.id.reservation_time);
                TextView text_people = (TextView) convertView.findViewById(R.id.reservation_people);
                TextView text_notes = (TextView) convertView.findViewById(R.id.reservation_notes);

                TableReservation reservation = reservation_list.get(position);
                text_client_name.setText(reservation.getClient_name());
                text_time.setText(timeFormat.format(reservation.getTime().getTime()));
                text_people.setText(String.format("%d", reservation.getN_people()));
                text_notes.setText(reservation.getNotes());

                ImageView delete = (ImageView) convertView.findViewById(R.id.table_reservation_delete);
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
                return convertView;
            }
        };

        lv.setAdapter(adapter);

        return rootView;
    }

    private ArrayList<TableReservation> getDataJson() {
        ArrayList<TableReservation> reservations = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        TableReservation res1 = new TableReservation("Andrea Cuiuli", 4 , today, "Una persona allergica alle noci");
        TableReservation res2 = new TableReservation("Andrea Cuiuli", 4 , today, "Una persona allergica alle noci");
        TableReservation res3 = new TableReservation("Andrea Cuiuli", 4 , today, "Una persona allergica alle noci");
        reservations.add(res1);
        reservations.add(res2);
        reservations.add(res3);

        return reservations;
    }
}
