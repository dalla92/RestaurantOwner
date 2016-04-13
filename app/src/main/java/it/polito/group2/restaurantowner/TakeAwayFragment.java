package it.polito.group2.restaurantowner;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TakeAwayFragment extends Fragment {

    private ArrayList<TakeAwayReservation> reservation_list;
    private ArrayList<OrderedMeal> ordered_meals;

    public TakeAwayFragment(){
        super();
        Bundle bundle = getArguments();
        long date_millis = bundle.getLong("date");
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(date_millis);
        reservation_list = getDataJson(date);
    }
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
                    convertView = inflater.inflate(R.layout.takeaway_reservation_item, parent, false);
                }

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:MM");
                TextView text_client_name = (TextView) convertView.findViewById(R.id.reservation_client);
                TextView text_time = (TextView) convertView.findViewById(R.id.reservation_time);
                TextView text_notes = (TextView) convertView.findViewById(R.id.reservation_notes);

                TakeAwayReservation reservation = reservation_list.get(position);
                text_client_name.setText(reservation.getClient_name());
                text_time.setText(timeFormat.format(reservation.getDate().getTime()));
                text_notes.setText(reservation.getNotes());

                LinearLayout list = (LinearLayout) convertView.findViewById(R.id.takeaway_reservation_list);
                list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PopupWindow popup = new PopupWindow(getActivity());
                        View layout = inflater.inflate(R.layout.takeaway_reservation_popup, null);
                        ordered_meals = reservation_list.get(position).getOrdered_meals();
                        ListView lv = (ListView) layout.findViewById(R.id.meal_list_view);
                        BaseAdapter adapter = new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return ordered_meals.size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return ordered_meals.get(position);
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                if(convertView == null){
                                    convertView = inflater.inflate(R.layout.meal_item, parent, false);
                                }

                                TextView meal_name = (TextView) convertView.findViewById(R.id.meal_name);
                                TextView meal_quantity = (TextView) convertView.findViewById(R.id.meal_quantity);

                                OrderedMeal meal = ordered_meals.get(position);
                                meal_name.setText(meal.getMeal_name());
                                meal_quantity.setText(String.format("%d", meal.getQuantity()));
                                return convertView;
                            }
                        };

                        lv.setAdapter(adapter);
                        popup.setContentView(layout);
                        // Set content width and height
                        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                        // Closes the popup window when touch outside of it - when looses focus
                        popup.setOutsideTouchable(true);
                        popup.setFocusable(true);
                        popup.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                        popup.showAtLocation(v, Gravity.CENTER, 0, 100);

                        View container =  (View) popup.getContentView().getParent();
                        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        p.dimAmount = 0.5f;
                        wm.updateViewLayout(container, p);

                        // Show anchored to button
                        //popup.showAsDropDown(v  );

                        /*ArrayList<String> sortList = new ArrayList<>();
                        sortList.add("A to Z");
                        sortList.add("Z to A");
                        sortList.add("Low to high price");

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, sortList);
                        PopupWindow popup = new PopupWindow();*/
                        //popup.setContentView(R.layout.fragment_table_reservation, popup, false);
                    }
                });

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

    private ArrayList<TakeAwayReservation> getDataJson(Calendar date) {
        ArrayList<TakeAwayReservation> reservations = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        ArrayList<OrderedMeal> mealOrdered = new ArrayList<>();
        mealOrdered.add(new OrderedMeal("Spaghetti alla carbonara", 2));
        mealOrdered.add(new OrderedMeal("Pizza Margerita", 3));
        mealOrdered.add(new OrderedMeal("Antipasto di mare", 2));
        mealOrdered.add(new OrderedMeal("Heineken", 5));
        TakeAwayReservation res1 = new TakeAwayReservation("Andrea Cuiuli", mealOrdered , today, "Una persona allergica alle noci");
        TakeAwayReservation res2 = new TakeAwayReservation("Andrea Cuiuli", mealOrdered , today, "Una persona allergica alle noci");
        TakeAwayReservation res3 = new TakeAwayReservation("Andrea Cuiuli", mealOrdered , today, "Una persona allergica alle noci");
        reservations.add(res1);
        reservations.add(res2);
        reservations.add(res3);

        return reservations;
    }
}
