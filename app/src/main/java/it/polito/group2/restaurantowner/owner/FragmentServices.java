package it.polito.group2.restaurantowner.owner;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.RestaurantTimeSlot;

/**
 * Created by Daniele on 07/04/2016.
 */
public class FragmentServices extends Fragment implements TimePickerDialog.OnTimeSetListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    Button buttonLunchOpen;
    Button buttonLunchClose;
    Button buttonDinnerOpen;
    Button buttonDinnerClose;
    ArrayList<String> lunchOpenTime = new ArrayList<String>();
    ArrayList<String> lunchCloseTime = new ArrayList<String>();
    ArrayList<String> dinnerOpenTime = new ArrayList<String>();
    ArrayList<String> dinnerCloseTime = new ArrayList<String>();
    boolean[] listLunchClose = new boolean[]{true,true,true,true,true,true,true,true};
    boolean[] listDinnerClose = new boolean[]{true,true,true,true,true,true,true,true};
    Boolean allDays=false;
    int selectedDay = 0;
    CheckBox fidelity;
    CheckBox tableRes;
    EditText tableResEdit;
    CheckBox takeAway;
    EditText takeAwayEdit;

    OnServicesPass dataPasser;

    //boolean times_changed;

    public FragmentServices() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentServices newInstance(Restaurant res, Context mContext) {
        FragmentServices fragment = new FragmentServices();
        Bundle args = new Bundle();
        if(res.getRestaurant_id()!=null) {
            args.putBoolean("Fidelity Program", res.getFidelityProgramAccepted());
            args.putBoolean("Table Reservation", res.getTableReservationAllowed());
            args.putString("Table Number", String.valueOf(res.getRestaurant_total_tables_number()));
            args.putBoolean("Take Away", res.getTakeAwayAllowed());
            args.putString("Orders Hour", String.valueOf(res.getRestaurant_orders_per_hour()));
            if (res.getRestaurant_time_slot() != null) {
                for (RestaurantTimeSlot ot : res.getRestaurant_time_slot()) {
                    int day = ot.getDay_of_week();
                    if (ot.getLunch()) {
                        args.putString("lunchOpenTime" + day, ot.getOpen_lunch_time());
                        args.putString("lunchCloseTime" + day, ot.getClose_lunch_time());
                    } else
                        args.putBoolean("ClosedLunch" + day, true);
                    if (ot.getDinner()) {
                        args.putString("dinnerOpenTime" + day, ot.getOpen_dinner_time());
                        args.putString("dinnerCloseTime" + day, ot.getClose_dinner_time());
                    } else
                        args.putBoolean("ClosedDinner" + day, true);
                }
            }
        }
        fragment.setArguments(args);
        return fragment;
    }

    public void passData() {
        if(tableResEdit.getText().toString().length()!=0)
            tableRes.setChecked(true);
        else
            tableRes.setChecked(false);
        if(takeAwayEdit.getText().toString().length()!=0)
            takeAway.setChecked(true);
        else
            takeAway.setChecked(false);

        /*if(!times_changed){
            lunchOpenTime = new ArrayList<>();
            lunchCloseTime = new ArrayList<>();
            dinnerOpenTime = new ArrayList<>();
            dinnerCloseTime = new ArrayList<>();
            listLunchClose = new boolean[]{true,true,true,true,true,true,true,true};
            listDinnerClose = new boolean[]{true,true,true,true,true,true,true,true};
        }*/

        dataPasser.onServicesPass(fidelity.isChecked(), tableRes.isChecked(), tableResEdit.getText().toString(), takeAway.isChecked(), takeAwayEdit.getText().toString(),
                lunchOpenTime, lunchCloseTime, dinnerOpenTime, dinnerCloseTime, listLunchClose, listDinnerClose);
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            dataPasser = (OnServicesPass) a;
        } catch (ClassCastException e) {
            throw new ClassCastException(a.toString()
                    + " must implement OnServicesPass");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_restaurant_services, container, false);

        fidelity = (CheckBox) rootView.findViewById(R.id.checkBoxFidelity);
        tableRes = (CheckBox) rootView.findViewById(R.id.checkBoxTable);
        takeAway = (CheckBox) rootView.findViewById(R.id.checkBoxTakeAway);
        tableResEdit = (EditText) rootView.findViewById(R.id.editTextTable);
        takeAwayEdit = (EditText) rootView.findViewById(R.id.editTextTakeAway);

        final CheckBox cbLunchClosed = (CheckBox) rootView.findViewById(R.id.checkBoxLunchClose);
        final CheckBox cbDinnerClosed = (CheckBox) rootView.findViewById(R.id.checkBoxDinnerClose);

        if(getArguments().isEmpty()) {
            if (savedInstanceState != null) {
                // Restore value of members from saved state
                lunchOpenTime = savedInstanceState.getStringArrayList("lunchOpenTime");
                lunchCloseTime = savedInstanceState.getStringArrayList("lunchCloseTime");
                dinnerOpenTime = savedInstanceState.getStringArrayList("dinnerOpenTime");
                dinnerCloseTime = savedInstanceState.getStringArrayList("dinnerCloseTime");
                listLunchClose = savedInstanceState.getBooleanArray("listLunchClose");
                listDinnerClose = savedInstanceState.getBooleanArray("listDinnerClose");
            } else {
                for (int i = 0; i < 8; i++) {
                    lunchOpenTime.add(i, "CLOSED");
                    lunchCloseTime.add(i, "CLOSED");
                    dinnerOpenTime.add(i, "CLOSED");
                    dinnerCloseTime.add(i, "CLOSED");
                }
            }
        }
        else{
           for(int i=0;i<8;i++) {
               lunchOpenTime.add(getArguments().getString("lunchOpenTime" + i, ""));
               lunchCloseTime.add(getArguments().getString("lunchCloseTime" + i, ""));
               dinnerOpenTime.add(getArguments().getString("dinnerOpenTime" + i, ""));
               dinnerCloseTime.add(getArguments().getString("dinnerCloseTime" + i, ""));
               listLunchClose[i] = getArguments().getBoolean("ClosedLunch" + i, false);
               listDinnerClose[i] = getArguments().getBoolean("ClosedDinner" + i, false);
           }
            fidelity.setChecked(getArguments().getBoolean("Fidelity Program",false));
            tableRes.setChecked(getArguments().getBoolean("Table Reservation",false));
            takeAway.setChecked(getArguments().getBoolean("Take Away", false));
            tableResEdit.setText(getArguments().getString("Table Number", ""));
            takeAwayEdit.setText(getArguments().getString("Orders Hour", ""));
        }


        buttonLunchOpen = (Button) rootView.findViewById(R.id.buttonLunchOpen);
        buttonLunchOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState){
                        /*
                        //Use the current time as the default values for the time picker
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        */

                        //Create and return a new instance of TimePickerDialog
                        return new TimePickerDialog(getActivity(),this, 10, 00,
                                DateFormat.is24HourFormat(getActivity()));
                    }

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = (hourOfDay<10?"0":"")+hourOfDay + ":" + new DecimalFormat("00").format(minute);
                        if(allDays)
                            for(int i=0;i<8;i++)
                                lunchOpenTime.set(i,time);
                        else
                            lunchOpenTime.set(selectedDay,time);
                        buttonLunchOpen.setText(time);

                        //times_changed = true;
                    }
                };
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });

        buttonLunchClose = (Button) rootView.findViewById(R.id.buttonLunchClose);
        buttonLunchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState){
                        /*
                        //Use the current time as the default values for the time picker
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        */

                        //Create and return a new instance of TimePickerDialog
                        return new TimePickerDialog(getActivity(),this, 13, 00,
                                DateFormat.is24HourFormat(getActivity()));
                    }
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = (hourOfDay<10?"0":"")+hourOfDay + ":" + new DecimalFormat("00").format(minute);
                        if(allDays)
                            for(int i=0;i<8;i++)
                                lunchCloseTime.set(i,time);
                        else
                            lunchCloseTime.set(selectedDay,time);
                        buttonLunchClose.setText(time);

                        //times_changed = true;
                    }
                };
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });

        buttonDinnerOpen = (Button) rootView.findViewById(R.id.buttonDinnerOpen);
        buttonDinnerOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState){
                        /*
                        //Use the current time as the default values for the time picker
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        */

                        //Create and return a new instance of TimePickerDialog
                        return new TimePickerDialog(getActivity(),this, 19, 00,
                                DateFormat.is24HourFormat(getActivity()));
                    }
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String time = (hourOfDay<10?"0":"")+hourOfDay + ":" + new DecimalFormat("00").format(minute);
                        if(allDays)
                            for(int i=0;i<8;i++)
                                dinnerOpenTime.set(i,time);
                        else
                            dinnerOpenTime.set(selectedDay,time);
                        buttonDinnerOpen.setText(time);

                        //times_changed = true;
                    }
                };
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });

        buttonDinnerClose = (Button) rootView.findViewById(R.id.buttonDinnerClose);
        buttonDinnerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState){
                        /*
                        //Use the current time as the default values for the time picker
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        */

                        //Create and return a new instance of TimePickerDialog
                        return new TimePickerDialog(getActivity(),this, 23, 00,
                                DateFormat.is24HourFormat(getActivity()));
                    }
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = (hourOfDay<10?"0":"")+hourOfDay + ":" + new DecimalFormat("00").format(minute);
                        if(allDays)
                            for(int i=0;i<8;i++)
                                dinnerCloseTime.set(i,time);
                        else
                            dinnerCloseTime.set(selectedDay,time);
                        buttonDinnerClose.setText(time);

                        //times_changed = true;
                    }
                };
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });

        cbLunchClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    lunchOpenTime.set(selectedDay,"CLOSED");
                    lunchCloseTime.set(selectedDay,"CLOSED");
                    buttonLunchOpen.setText("CLOSED");
                    buttonLunchOpen.setEnabled(false);
                    buttonLunchClose.setText("CLOSED");
                    buttonLunchClose.setEnabled(false);
                    listLunchClose[selectedDay]= true;
                }
                else{
                    lunchOpenTime.set(selectedDay,"10:00");
                    lunchCloseTime.set(selectedDay,"13:00");
                    buttonLunchOpen.setText("10:00");
                    buttonLunchOpen.setEnabled(true);
                    buttonLunchClose.setText("13:00");
                    buttonLunchClose.setEnabled(true);
                    listLunchClose[selectedDay]= false;
                }
            }
        });

        cbDinnerClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    dinnerOpenTime.set(selectedDay,"CLOSED");
                    dinnerCloseTime.set(selectedDay, "CLOSED");
                    buttonDinnerOpen.setText("CLOSED");
                    buttonDinnerOpen.setEnabled(false);
                    buttonDinnerClose.setText("CLOSED");
                    buttonDinnerClose.setEnabled(false);
                    listDinnerClose[selectedDay] = true;
                }
                else{
                    dinnerOpenTime.set(selectedDay,"19:00");
                    dinnerCloseTime.set(selectedDay, "23:00");
                    buttonDinnerOpen.setText("19:00");
                    buttonDinnerOpen.setEnabled(true);
                    buttonDinnerClose.setText("23:00");
                    buttonDinnerClose.setEnabled(true);
                    listDinnerClose[selectedDay] = false;
                }
            }
        });


        final Spinner day = (Spinner) rootView.findViewById(R.id.spinnerDays);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.week_days_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        day.setAdapter(adapter);
        selectedDay = day.getSelectedItemPosition();

        day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==7){
                    allDays=true;
                    /*cbLunchClosed.setVisibility(CheckBox.INVISIBLE);
                    cbDinnerClosed.setVisibility(CheckBox.INVISIBLE);*/
                }
                else {
                    allDays = false;
                    cbLunchClosed.setVisibility(CheckBox.VISIBLE);
                    cbLunchClosed.setChecked(listLunchClose[position]);
                    cbDinnerClosed.setVisibility(CheckBox.VISIBLE);
                    cbDinnerClosed.setChecked(listDinnerClose[position]);

                }
                selectedDay = position;
                buttonLunchOpen.setEnabled(!listLunchClose[position]);
                buttonLunchClose.setEnabled(!listLunchClose[position]);
                buttonDinnerOpen.setEnabled(!listDinnerClose[position]);
                buttonDinnerClose.setEnabled(!listDinnerClose[position]);
                buttonLunchOpen.setText(lunchOpenTime.get(selectedDay));
                buttonLunchClose.setText(lunchCloseTime.get(selectedDay));
                buttonDinnerOpen.setText(dinnerOpenTime.get(selectedDay));
                buttonDinnerClose.setText(dinnerCloseTime.get(selectedDay));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putStringArrayList("lunchOpenTime",lunchOpenTime);
        savedInstanceState.putStringArrayList("lunchCloseTime",lunchCloseTime);
        savedInstanceState.putStringArrayList("dinnerOpenTime",dinnerOpenTime);
        savedInstanceState.putStringArrayList("dinnerCloseTime",dinnerCloseTime);
        savedInstanceState.putBooleanArray("listLunchClose", listLunchClose);
        savedInstanceState.putBooleanArray("listDinnerClose",listDinnerClose);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        buttonLunchOpen.setText(hourOfDay + minute);
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    public interface OnServicesPass {
        public void onServicesPass(Boolean fidelity, Boolean tableRes, String numTables, Boolean takeAway, String orderPerHour, List<String> lunchOpenTime,List<String> lunchCloseTime,
                                   List<String> dinnerOpenTime, List<String> dinnerCloseTime, boolean[] lunchClosure, boolean[] dinnerClosure);
    }
}
