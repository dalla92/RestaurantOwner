package it.polito.group2.restaurantowner;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Daniele on 07/04/2016.
 */
public class FragmentServices extends Fragment implements TimePickerDialog.OnTimeSetListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    Button buttonLunchOpen;
    Button buttonLunchClose;
    Button buttonDinnerOpen;
    Button buttonDinnerClose;
    ArrayList<String> lunchOpenTime = new ArrayList<String>();
    ArrayList<String> lunchCloseTime = new ArrayList<String>();
    ArrayList<String> dinnerOpenTime = new ArrayList<String>();
    ArrayList<String> dinnerCloseTime = new ArrayList<String>();
    ArrayList<Boolean> listLunchClose = new ArrayList<Boolean>();
    ArrayList<Boolean> listDinnerClose = new ArrayList<Boolean>();
    Boolean allDays=false;
    int selectedDay = 0;
    CheckBox fidelity;
    CheckBox tableRes;
    EditText tableResEdit;
    CheckBox takeAway;
    EditText takeAwayEdit;

    OnServicesPass dataPasser;


    public FragmentServices() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentServices newInstance(int sectionNumber) {
        FragmentServices fragment = new FragmentServices();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public void passData() {
        dataPasser.onServicesPass(fidelity.isSelected(), tableRes.isSelected(), tableResEdit.getText().toString(), takeAway.isSelected(), takeAwayEdit.getText().toString(),
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

        for(int i=0;i<8;i++){
            lunchOpenTime.add(i,"SET");
            lunchCloseTime.add(i,"SET");
            dinnerOpenTime.add(i,"SET");
            dinnerCloseTime.add(i,"SET");
            listLunchClose.add(i, false);
            listDinnerClose.add(i, false);
        }

        fidelity = (CheckBox) rootView.findViewById(R.id.checkBoxFidelity);
        tableRes = (CheckBox) rootView.findViewById(R.id.checkBoxTable);
        takeAway = (CheckBox) rootView.findViewById(R.id.checkBoxTakeAway);
        tableResEdit = (EditText) rootView.findViewById(R.id.editTextTable);
        takeAwayEdit = (EditText) rootView.findViewById(R.id.editTextTakeAway);

        final CheckBox cbLunchClosed = (CheckBox) rootView.findViewById(R.id.checkBoxLunchClose);
        final CheckBox cbDinnerClosed = (CheckBox) rootView.findViewById(R.id.checkBoxDinnerClose);

        buttonLunchOpen = (Button) rootView.findViewById(R.id.buttonLunchOpen);
        buttonLunchOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = hourOfDay + ":" + minute;
                        if(allDays)
                            for(int i=0;i<8;i++)
                                lunchOpenTime.set(i,time);
                        else
                            lunchOpenTime.set(selectedDay,time);
                        buttonLunchOpen.setText(time);
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
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = hourOfDay + ":" + minute;
                        if(allDays)
                            for(int i=0;i<8;i++)
                                lunchCloseTime.set(i,time);
                        else
                            lunchCloseTime.set(selectedDay,time);
                        buttonLunchClose.setText(time);
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
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String time = hourOfDay + ":" + minute;
                        if(allDays)
                            for(int i=0;i<8;i++)
                                dinnerOpenTime.set(i,time);
                        else
                            dinnerOpenTime.set(selectedDay,time);
                        buttonDinnerOpen.setText(time);

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
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = hourOfDay + ":" + minute;
                        if(allDays)
                            for(int i=0;i<8;i++)
                                dinnerCloseTime.set(i,time);
                        else
                            dinnerCloseTime.set(selectedDay,time);
                        buttonDinnerClose.setText(time);
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
                    listLunchClose.set(selectedDay, true);
                }
                else{
                    lunchOpenTime.set(selectedDay,"SET");
                    lunchCloseTime.set(selectedDay,"SET");
                    buttonLunchOpen.setText("SET");
                    buttonLunchOpen.setEnabled(true);
                    buttonLunchClose.setText("SET");
                    buttonLunchClose.setEnabled(true);
                    listLunchClose.set(selectedDay, false);
                }
            }
        });

        cbDinnerClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    dinnerOpenTime.set(selectedDay,"CLOSED");
                    dinnerCloseTime.set(selectedDay,"CLOSED");
                    buttonDinnerOpen.setText("CLOSED");
                    buttonDinnerOpen.setEnabled(false);
                    buttonDinnerClose.setText("CLOSED");
                    buttonDinnerClose.setEnabled(false);
                    listDinnerClose.set(selectedDay, true);
                }
                else{
                    dinnerOpenTime.set(selectedDay,"SET");
                    dinnerCloseTime.set(selectedDay,"SET");
                    buttonDinnerOpen.setText("SET");
                    buttonDinnerOpen.setEnabled(true);
                    buttonDinnerClose.setText("SET");
                    buttonDinnerClose.setEnabled(true);
                    listDinnerClose.set(selectedDay, false);
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
                    cbLunchClosed.setVisibility(CheckBox.INVISIBLE);
                    cbDinnerClosed.setVisibility(CheckBox.INVISIBLE);
                }
                else {
                    allDays = false;
                    cbLunchClosed.setVisibility(CheckBox.VISIBLE);
                    cbLunchClosed.setChecked(listLunchClose.get(position));
                    cbDinnerClosed.setVisibility(CheckBox.VISIBLE);
                    cbDinnerClosed.setChecked(listDinnerClose.get(position));

                }
                selectedDay = position;
                buttonLunchOpen.setEnabled(!listLunchClose.get(position));
                buttonLunchClose.setEnabled(!listLunchClose.get(position));
                buttonDinnerOpen.setEnabled(!listDinnerClose.get(position));
                buttonDinnerClose.setEnabled(!listDinnerClose.get(position));
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
                                   List<String> dinnerOpenTime, List<String> dinnerCloseTime, List<Boolean> lunchClosure, List<Boolean> dinnerClosure);
    }
}
