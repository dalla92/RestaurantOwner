package it.polito.group2.restaurantowner.owner;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;


import java.util.ArrayList;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;

/**
 * Created by Daniele on 07/04/2016.
 */
public class FragmentExtras extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    OnExtrasPass dataPasser;
    CheckBox animals;
    CheckBox vegan;
    CheckBox vegetarian;
    CheckBox glutenFree;
    CheckBox tv;
    CheckBox patio;
    CheckBox wifi;
    CheckBox creditcard;
    CheckBox babyspace;
    CheckBox ac;
    EditText squaredMeters;
    EditText closestMetro;
    EditText closestBus;
    static Restaurant myRes;


    public FragmentExtras() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentExtras newInstance(Restaurant res,Context mContext) {
        myRes = res;
        FragmentExtras fragment = new FragmentExtras();
        Bundle args = new Bundle();
        if(res.getRestaurant_id()!=null) {
            if(res.getAirConditionedPresent())
                args.putBoolean("AC", true);
            if(res.getAnimalAllowed())
                args.putBoolean("Animals", true);
            if(res.getTvPresent())
                args.putBoolean("TV", true);
            if(res.getCreditCardAccepted())
                args.putBoolean("Credit Card", true);
//            if(res.getCeliacFriendly())
//                args.putBoolean("Celiac", true);
            if(res.getWifiPresent())
                args.putBoolean("Wi-Fi", true);
            if(res.getRestaurant_squared_meters()!=0)
                args.putInt("Squared Meters", 0);
            if(res.getRestaurant_closest_metro()!="")
                args.putString("Closest Metro", "");
            if(res.getRestaurant_closest_bus()!="")
                args.putString("Closest Bus", "");
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_restaurant_extras, container, false);
        animals = (CheckBox) rootView.findViewById(R.id.cbAnimals);
        vegan = (CheckBox) rootView.findViewById(R.id.cbVegan);
        vegetarian = (CheckBox) rootView.findViewById(R.id.cbVegetarian);
        glutenFree = (CheckBox) rootView.findViewById(R.id.cbGlutenFree);
        tv = (CheckBox) rootView.findViewById(R.id.cbTV);
        patio = (CheckBox) rootView.findViewById(R.id.cbPatio);
        wifi = (CheckBox) rootView.findViewById(R.id.cbWifi);
        creditcard = (CheckBox) rootView.findViewById(R.id.cbCreditCard);
        babyspace = (CheckBox) rootView.findViewById(R.id.cbBabySpace);
        ac = (CheckBox) rootView.findViewById(R.id.cbAC);
        squaredMeters = (EditText) rootView.findViewById(R.id.etSquaredMeters);
        closestMetro = (EditText) rootView.findViewById(R.id.etClosestMetro);
        closestBus = (EditText) rootView.findViewById(R.id.etClosestBus);

        if(!getArguments().isEmpty()){
            animals.setChecked(getArguments().getBoolean("Animals",false));
            vegan.setChecked(getArguments().getBoolean("Vegan",false));
            vegetarian.setChecked(getArguments().getBoolean("Vegetarian",false));
            glutenFree.setChecked(getArguments().getBoolean("GlutenFree",false));
            tv.setChecked(getArguments().getBoolean("TV",false));
            patio.setChecked(getArguments().getBoolean("Patio",false));
            wifi.setChecked(getArguments().getBoolean("Wi-Fi",false));
            creditcard.setChecked(getArguments().getBoolean("Credit Card",false));
            babyspace.setChecked(getArguments().getBoolean("Baby Space",false));
            ac.setChecked(getArguments().getBoolean("AC",false));
            squaredMeters.setText(getArguments().getString("Squared Meters", ""));
            closestMetro.setText(getArguments().getString("Closest Metro", ""));
            closestBus.setText(getArguments().getString("Closest Bus", ""));
        }

        return rootView;
    }

    public void passData() {
        if(dataPasser!=null) {
            ArrayList<RestaurantService> list = new ArrayList<RestaurantService>();
            if (animals.isChecked())
                myRes.setAnimalAllowed(true);
            else
                myRes.setAnimalAllowed(false);
/*            if (vegan.isChecked()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Vegan");
                list.add(rs);
            }
            if (vegetarian.isChecked()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Vegetarian");
                list.add(rs);
            }
            if (glutenFree.isChecked()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Gluten Free");
                list.add(rs);
            }
*/        if (tv.isChecked())
            myRes.setTvPresent(true);
        else
            myRes.setTvPresent(false);
/*            if (patio.isChecked()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Patio");
                list.add(rs);
            }
*/          if (wifi.isChecked())
            myRes.setWifiPresent(true);
            else
            myRes.setWifiPresent(false);
            if (creditcard.isChecked())
                myRes.setCreditCardAccepted(true);
            else
                myRes.setCreditCardAccepted(false);
/*            if (babyspace.isChecked()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Baby Space");
                list.add(rs);
            }
*/          if (ac.isChecked())
                myRes.setAirConditionedPresent(true);
            else
                myRes.setAirConditionedPresent(false);
            if(!squaredMeters.getText().toString().equals("")) {
                myRes.setRestaurant_squared_meters(Integer.parseInt(squaredMeters.getText().toString()));
            }
            if(!closestMetro.getText().toString().equals("")) {
                myRes.setRestaurant_closest_metro(closestMetro.getText().toString());
            }
            if(!closestBus.getText().toString().equals("")) {
                myRes.setRestaurant_closest_bus(closestBus.getText().toString());

            }
            dataPasser.onExtrasPass(myRes);
        }
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            dataPasser = (OnExtrasPass) a;
        } catch (ClassCastException e) {
            throw new ClassCastException(a.toString()
                    + " must implement OnExtrasPass");
        }
    }

    public interface OnExtrasPass {
        public void onExtrasPass(Restaurant res);
    }
}
