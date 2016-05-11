package it.polito.group2.restaurantowner.owner;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.OrderMeal;
import it.polito.group2.restaurantowner.data.Order;

public class TakeAwayFragment extends Fragment {

    private ArrayList<Order> reservation_list;
    private BaseAdapter adapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_table_reservation, container, false);

        Bundle bundle = getArguments();
        long date_millis = bundle.getLong("date");
        String restaurantId = bundle.getString("id");
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(date_millis);
        createFakeData(date, restaurantId);
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
                    convertView = inflater.inflate(R.layout.takeaway_reservation_item, parent, false);
                }


                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                TextView text_client_name = (TextView) convertView.findViewById(R.id.reservation_client);
                TextView text_time = (TextView) convertView.findViewById(R.id.reservation_time);
                TextView text_notes = (TextView) convertView.findViewById(R.id.reservation_notes);

                Order reservation = reservation_list.get(position);
                text_client_name.setText(reservation.getUsername());
                text_time.setText(timeFormat.format(reservation.getDate().getTime()));
                text_notes.setText(reservation.getNotes());

                LinearLayout list = (LinearLayout) convertView.findViewById(R.id.takeaway_reservation_list);
                list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<OrderMeal> ordered_meals = reservation_list.get(position).getOrdered_meals();
                        MealListDialog dialog = MealListDialog.newInstance(ordered_meals);
                        dialog.show(getActivity().getFragmentManager(), null);
                    }
                });

                ImageView delete = (ImageView) convertView.findViewById(R.id.table_reservation_delete);
                Calendar today = Calendar.getInstance();
                Calendar target = reservation.getDate();
                if(target.after(today.getTime()) ||
                        (target.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                target.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                                target.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH))) {
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

    private void createFakeData(Calendar date, String restaurantId) {
        ArrayList<Order> reservations = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 1);
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        String id3 = UUID.randomUUID().toString();
        ArrayList<OrderMeal> mealOrdered1 = new ArrayList<>();
        mealOrdered1.add(new OrderMeal("Spaghetti alla carbonara", 2, id1));
        mealOrdered1.add(new OrderMeal("Pizza Margerita", 3, id1));
        mealOrdered1.add(new OrderMeal("Antipasto di mare", 2, id1));
        mealOrdered1.add(new OrderMeal("Heineken", 5, id1));
        Order res1 = new Order("Andrea Cuiuli", mealOrdered1, date, "Una persona allergica alle noci", restaurantId, id1);

        ArrayList<OrderMeal> mealOrdered2 = new ArrayList<>();
        mealOrdered2.add(new OrderMeal("Linguine allo scoglio", 2, id2));
        mealOrdered2.add(new OrderMeal("Pizza Napoletana", 3, id2));
        Order res2 = new Order("Andrea Cuiuli", mealOrdered2, date, "Una persona allergica alle noci", restaurantId, id2);

        ArrayList<OrderMeal> mealOrdered3 = new ArrayList<>();
        mealOrdered3.add(new OrderMeal("Antipasto Rustico", 1, id3));
        mealOrdered3.add(new OrderMeal("Vino della casa", 1, id3));
        mealOrdered3.add(new OrderMeal("Carpaccio di manzo", 1, id3));
        Order res3 = new Order("Andrea Cuiuli", mealOrdered3 , date, "Una persona allergica alle noci", restaurantId, id3);

        reservations.add(res1);
        reservations.add(res2);
        reservations.add(res3);

        try {
            JSONUtil.saveJSONTakeAwayResList(getActivity(), reservations);
        } catch (JSONException e) {
            Log.d("failed", "problema nel createFakeData delle takeAwayReservations");
        }
    }

    private void saveDataToJson(ArrayList<Order> reservations) {
        try {
            JSONUtil.saveJSONTakeAwayResList(getActivity(), reservations);
        } catch (JSONException e) {
            Log.d("failed", "problema nel saveDataJson delle takeAwayReservations");
        }
    }

    private ArrayList<Order> getDataJson(Calendar date, String restaurantId) {

        try {
            return JSONUtil.readJSONTakeAwayResList(getActivity(), date, restaurantId);
        } catch (JSONException e) {
            return new ArrayList<>();
        }
    }

    public void changeData(Calendar date, String restaurantId){
        reservation_list = getDataJson(date, restaurantId);
        TextView reservation_title = (TextView) getActivity().findViewById(R.id.reservation_list_title);
        if(reservation_list.isEmpty()) {
            reservation_title.setVisibility(View.VISIBLE);
            reservation_title.setText(getString(R.string.no_reservation));
        }
        else
            reservation_title.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    private String getMonthName(int month){
        return new DateFormatSymbols().getMonths()[month];
    }

}
