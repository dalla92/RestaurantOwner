package it.polito.group2.restaurantowner.owner.offer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Offer;

public class OfferFragment extends Fragment {

    public static final String OFFER = "offer";
    public static final String LIST = "restaurantMealList";
    private Offer offer;
    private ArrayList<Meal> restaurantMealList;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);

    private OnActionListener mCallback;

    public OfferFragment() {}

    public static OfferFragment newInstance(Offer offer, ArrayList<Meal> meals) {
        OfferFragment fragment = new OfferFragment();
        Bundle args = new Bundle();
        args.putSerializable(OFFER, offer);
        args.putParcelableArrayList(LIST, meals);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            offer = (Offer)getArguments().getSerializable(OFFER);
            restaurantMealList = getArguments().getParcelableArrayList(LIST);
        }

        setHasOptionsMenu(true);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().
                    getResources().getString(R.string.owner_offer_fragment_offer_title));
        } catch (Exception e) {
            Log.d("FILIPPO", e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.owner_offer_fragment_offer, container, false);

        Switch enabled = (Switch) view.findViewById(R.id.enabled);
        enabled.setChecked(offer.getOfferEnabled());

        Switch lunch = (Switch) view.findViewById(R.id.lunch);
        lunch.setChecked(offer.getOfferAtLunch());

        Switch dinner = (Switch) view.findViewById(R.id.dinner);
        dinner.setChecked(offer.getOfferAtDinner());

        if(!offer.getOfferName().equals("")) {
            EditText offerName = (EditText) view.findViewById(R.id.offer_name);
            offerName.setText(offer.getOfferName());
        }

        if(!offer.getOfferDescription().equals("")) {
            EditText offerDescription = (EditText) view.findViewById(R.id.offer_description);
            offerDescription.setText(offer.getOfferDescription());
        }

        if(offer.getOfferStartDate() != null) {
            TextView start = (TextView) view.findViewById(R.id.text_from_date);
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            if(offer.getOfferStartDate().get(Calendar.YEAR) == year &&
                    offer.getOfferStartDate().get(Calendar.MONTH) == month &&
                    offer.getOfferStartDate().get(Calendar.DAY_OF_MONTH) == day) {
                start.setText(getString(R.string.owner_offer_fragment_offer_label_today));
            } else {
                start.setText(dateFormat.format(offer.getOfferStartDate().getTime()));
            }
        }

        if(offer.getOfferStopDate() != null) {
            TextView stop = (TextView) view.findViewById(R.id.text_to_date);
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            if(offer.getOfferStopDate().get(Calendar.YEAR) == year &&
                    offer.getOfferStopDate().get(Calendar.MONTH) == month &&
                    offer.getOfferStopDate().get(Calendar.DAY_OF_MONTH) == day) {
                stop.setText(getString(R.string.owner_offer_fragment_offer_label_today));
            } else {
                stop.setText(dateFormat.format(offer.getOfferStopDate().getTime()));
            }
        }

        ToggleButton mon = (ToggleButton) view.findViewById(R.id.mon_selected);
        mon.setChecked(offer.isWeekDayInOffer(Calendar.MONDAY));

        ToggleButton tue = (ToggleButton) view.findViewById(R.id.tue_selected);
        tue.setChecked(offer.isWeekDayInOffer(Calendar.TUESDAY));

        ToggleButton wed = (ToggleButton) view.findViewById(R.id.wed_selected);
        wed.setChecked(offer.isWeekDayInOffer(Calendar.WEDNESDAY));

        ToggleButton thu = (ToggleButton) view.findViewById(R.id.thu_selected);
        thu.setChecked(offer.isWeekDayInOffer(Calendar.THURSDAY));

        ToggleButton fri = (ToggleButton) view.findViewById(R.id.fri_selected);
        fri.setChecked(offer.isWeekDayInOffer(Calendar.FRIDAY));

        ToggleButton sat = (ToggleButton) view.findViewById(R.id.sat_selected);
        sat.setChecked(offer.isWeekDayInOffer(Calendar.SATURDAY));

        ToggleButton sun = (ToggleButton) view.findViewById(R.id.sun_selected);
        sun.setChecked(offer.isWeekDayInOffer(Calendar.SUNDAY));

        EditText discount = (EditText) view.findViewById(R.id.discount);
        discount.setText(offer.getOfferPercentage().toString());

        if(offer.getOfferForTotal()) {
            offer.setOfferForCategory(false);
            offer.setOfferForMeal(false);
            hideCategoryList();
            hideMealList();
        }

        if(offer.getOfferForCategory()) {
            offer.setOfferForTotal(false);
            offer.setOfferForMeal(false);
            showCategoryList();
            hideMealList();
        }

        if(offer.getOfferForMeal()) {
            offer.setOfferForTotal(false);
            offer.setOfferForCategory(false);
            showMealList();
            hideCategoryList();
        }

        RadioButton applyAll = (RadioButton) view.findViewById(R.id.apply_all);
        applyAll.setChecked(offer.getOfferForTotal());

        RadioButton applyCategories = (RadioButton) view.findViewById(R.id.apply_categories);
        applyCategories.setChecked(offer.getOfferForCategory());

        RadioButton applyMeals = (RadioButton) view.findViewById(R.id.apply_meals);
        applyMeals.setChecked(offer.getOfferForMeal());

        RadioGroup rdGroup = (RadioGroup) view.findViewById(R.id.apply_option);
        rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton applyAll = (RadioButton) view.findViewById(R.id.apply_all);
                RadioButton applyCategories = (RadioButton) view.findViewById(R.id.apply_categories);
                RadioButton applyMeals = (RadioButton) view.findViewById(R.id.apply_meals);
                switch (checkedId) {
                    case R.id.apply_all:
                        if (applyAll.isChecked()) {
                            applyCategories.setChecked(false);
                            applyMeals.setChecked(false);
                            hideCategoryList();
                            hideMealList();
                            saveData(view);
                        }
                        break;
                    case R.id.apply_categories:
                        if (applyCategories.isChecked()) {
                            applyAll.setChecked(false);
                            applyMeals.setChecked(false);
                            hideMealList();
                            showCategoryList();
                            saveData(view);
                            if(offer.getOfferOnCategories().size() <= 0) {
                                mCallback.onCategoryListRq(offer);
                            }
                        }
                        break;
                    case R.id.apply_meals:
                        if (applyMeals.isChecked()) {
                            applyAll.setChecked(false);
                            applyCategories.setChecked(false);
                            hideCategoryList();
                            showMealList();
                            saveData(view);
                            if(offer.getOfferOnMeals().size() <= 0) {
                                mCallback.onMealListRq(offer);
                            }
                        }
                        break;
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnActionListener) {
            mCallback = (OnActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface OnActionListener {
        public void onSaveClicked(Offer offer);
        public void onCategoryListRq(Offer offer);
        public void onMealListRq(Offer offer);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.owner_offer_fragment_offer_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_save){
            saveData(getView());
            boolean alert = false;
            if(offer.getOfferEnabled()) {
                if(!offer.getOfferAtDinner() && !offer.getOfferAtLunch())
                    alert = true;
                if(!offer.getOfferForTotal() && !offer.getOfferForCategory() && !offer.getOfferForMeal())
                    alert = true;
                if(offer.getOfferForCategory() && offer.getOfferOnCategories().size() <= 0)
                    alert = true;
                if(offer.getOfferForMeal() && offer.getOfferOnMeals().size() <= 0)
                    alert = true;
            }

            if(alert) {
                final View view = getView();
                if(view != null) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getContext().getResources().getString(R.string.owner_offer_fragment_offer_title_alert))
                            .setMessage(getContext().getResources().getString(R.string.owner_offer_fragment_offer_message_alert))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mCallback.onSaveClicked(offer);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void editCategoryList(View v) {
        saveData(v);
        mCallback.onCategoryListRq(offer);
    }

    public void editMealList(View v) {
        saveData(v);
        mCallback.onMealListRq(offer);
    }

    public void showFromDatePickerDialog(View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog =  new DatePickerDialog(this.getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                TextView date = (TextView) view.findViewById(R.id.text_from_date);
                Calendar c = Calendar.getInstance();
                if(c.get(Calendar.YEAR) == year &&  c.get(Calendar.MONTH) == monthOfYear &&  c.get(Calendar.DAY_OF_MONTH) == dayOfMonth)
                    date.setText(getString(R.string.owner_offer_fragment_offer_label_today));
                else {
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, monthOfYear);
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    date.setText(dateFormat.format(c.getTime()));
                }
            }
        }, year, month, day);
        dialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        dialog.show();
    }

    public void showToDatePickerDialog(View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog =  new DatePickerDialog(this.getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                TextView date = (TextView) view.findViewById(R.id.text_to_date);
                Calendar c = Calendar.getInstance();
                if(c.get(Calendar.YEAR) == year &&  c.get(Calendar.MONTH) == monthOfYear &&  c.get(Calendar.DAY_OF_MONTH) == dayOfMonth)
                    date.setText(getString(R.string.owner_offer_fragment_offer_label_today));
                else {
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, monthOfYear);
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    date.setText(dateFormat.format(c.getTime()));
                }
            }
        }, year, month, day);

        dialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        dialog.show();
    }

    private void saveData(View view) {

        Switch enabled = (Switch) view.findViewById(R.id.enabled);
        this.offer.setOfferEnabled(enabled.isEnabled());

        Switch lunch = (Switch) view.findViewById(R.id.lunch);
        this.offer.setOfferAtLunch(lunch.isEnabled());

        Switch dinner = (Switch) view.findViewById(R.id.dinner);
        this.offer.setOfferAtDinner(dinner.isEnabled());

        EditText offerName = (EditText) view.findViewById(R.id.offer_name);
        this.offer.setOfferName(offerName.getText().toString());

        EditText offerDescription = (EditText) view.findViewById(R.id.offer_description);
        this.offer.setOfferDescription(offerDescription.getText().toString());

        TextView start = (TextView) view.findViewById(R.id.text_from_date);
        if(start.getText().toString().equals(getString(R.string.owner_offer_fragment_offer_label_today))) {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            this.offer.setOfferStartDate(today);
        } else {
            Calendar d = Calendar.getInstance();
            try {
                d.setTime(dateFormat.parse(start.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            d.set(Calendar.HOUR_OF_DAY, 0);
            d.set(Calendar.MINUTE, 0);
            d.set(Calendar.SECOND, 0);
            this.offer.setOfferStartDate(d);
        }

        TextView stop = (TextView) view.findViewById(R.id.text_to_date);
        if(stop.getText().toString().equals(getString(R.string.owner_offer_fragment_offer_label_today))) {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            this.offer.setOfferStopDate(today);
        } else {
            Calendar d = Calendar.getInstance();
            try {
                d.setTime(dateFormat.parse(stop.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            d.set(Calendar.HOUR_OF_DAY, 0);
            d.set(Calendar.MINUTE, 0);
            d.set(Calendar.SECOND, 0);
            this.offer.setOfferStopDate(d);
        }

        ToggleButton mon = (ToggleButton) view.findViewById(R.id.mon_selected);
        if(mon.isChecked())
            this.offer.addWeekDayInOffer(Calendar.MONDAY);
        else
            this.offer.delWeekDayInOffer(Calendar.MONDAY);

        ToggleButton tue = (ToggleButton) view.findViewById(R.id.tue_selected);
        if(tue.isChecked())
            this.offer.addWeekDayInOffer(Calendar.TUESDAY);
        else
            this.offer.delWeekDayInOffer(Calendar.TUESDAY);

        ToggleButton wed = (ToggleButton) view.findViewById(R.id.wed_selected);
        if(wed.isChecked())
            this.offer.addWeekDayInOffer(Calendar.WEDNESDAY);
        else
            this.offer.delWeekDayInOffer(Calendar.WEDNESDAY);

        ToggleButton thu = (ToggleButton) view.findViewById(R.id.thu_selected);
        if(thu.isChecked())
            this.offer.addWeekDayInOffer(Calendar.THURSDAY);
        else
            this.offer.delWeekDayInOffer(Calendar.THURSDAY);

        ToggleButton fri = (ToggleButton) view.findViewById(R.id.fri_selected);
        if(fri.isChecked())
            this.offer.addWeekDayInOffer(Calendar.FRIDAY);
        else
            this.offer.delWeekDayInOffer(Calendar.FRIDAY);

        ToggleButton sat = (ToggleButton) view.findViewById(R.id.sat_selected);
        if(sat.isChecked())
            this.offer.addWeekDayInOffer(Calendar.SATURDAY);
        else
            this.offer.delWeekDayInOffer(Calendar.SATURDAY);

        ToggleButton sun = (ToggleButton) view.findViewById(R.id.sun_selected);
        if(sun.isChecked())
            this.offer.addWeekDayInOffer(Calendar.SUNDAY);
        else
            this.offer.delWeekDayInOffer(Calendar.SUNDAY);

        EditText discount = (EditText) view.findViewById(R.id.discount);
        this.offer.setOfferPercentage(Integer.parseInt(discount.getText().toString()));

        RadioButton applyAll = (RadioButton) view.findViewById(R.id.apply_all);
        offer.setOfferForTotal(applyAll.isChecked());

        RadioButton applyCategories = (RadioButton) view.findViewById(R.id.apply_categories);
        offer.setOfferForCategory(applyCategories.isChecked());

        RadioButton applyMeals = (RadioButton) view.findViewById(R.id.apply_meals);
        offer.setOfferForMeal(applyMeals.isChecked());

        if(offer.getOfferForTotal()) {
            offer.setOfferForCategory(false);
            offer.setOfferForMeal(false);
        }

        if(offer.getOfferForCategory()) {
            offer.setOfferForTotal(false);
            offer.setOfferForMeal(false);
        }

        if(offer.getOfferForMeal()) {
            offer.setOfferForTotal(false);
            offer.setOfferForCategory(false);
        }
    }

    private void hideCategoryList() {
        View view = getView();
        if(view != null) {
            LinearLayout list = (LinearLayout) view.findViewById(R.id.category_block);
            list.setVisibility(View.GONE);
        } else {
            Log.d("FILIPPO", "Oggetto view risulta null");
        }
    }

    private void hideMealList() {
        View view = getView();
        if(view != null) {
            LinearLayout list = (LinearLayout) view.findViewById(R.id.meal_block);
            list.setVisibility(View.GONE);
        } else {
            Log.d("FILIPPO", "Oggetto view risulta null");
        }
    }

    private void showCategoryList() {
        View view = getView();
        if(view != null) {
            LinearLayout list = (LinearLayout) view.findViewById(R.id.category_block);
            list.setVisibility(View.VISIBLE);
            TextView label = (TextView) view.findViewById((R.id.nocategory_lable));
            if(offer.getOfferOnCategories().values().size() > 0) {
                label.setVisibility(View.GONE);
            } else {
                label.setVisibility(View.VISIBLE);
            }
            setCategoryList();
        } else {
            Log.d("FILIPPO", "Oggetto view risulta null");
        }
    }

    private void showMealList() {
        View view = getView();
        if(view != null) {
            LinearLayout list = (LinearLayout) view.findViewById(R.id.meal_block);
            list.setVisibility(View.VISIBLE);
            TextView label = (TextView) view.findViewById((R.id.nomeal_lable));
            if(offer.getOfferOnMeals().values().size() > 0) {
                label.setVisibility(View.GONE);
            } else {
                label.setVisibility(View.VISIBLE);
            }
            setMealList();
        } else {
            Log.d("FILIPPO", "Oggetto view risulta null");
        }
    }

    private void setCategoryList() {
        final RecyclerView list = (RecyclerView) getView().findViewById(R.id.category_list);
        assert list != null;
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setNestedScrollingEnabled(false);
        ArrayList<String> categoryList = new ArrayList<String>();
        categoryList.addAll(offer.getOfferOnCategories().keySet());
        OfferCategoryAdapter adapter = new OfferCategoryAdapter(categoryList);
        list.setAdapter(adapter);
    }

    private void setMealList() {
        final RecyclerView list = (RecyclerView) getView().findViewById(R.id.meal_list);
        assert list != null;
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setNestedScrollingEnabled(false);
        ArrayList<String> mealList = new ArrayList<String>();
        for(Meal m : restaurantMealList) {
            if(offer.getOfferOnMeals().containsKey(m.getMeal_id()))
                mealList.add(m.getMeal_name());
        }
        OfferMealAdapter adapter = new OfferMealAdapter(mealList);
        list.setAdapter(adapter);
    }

}
