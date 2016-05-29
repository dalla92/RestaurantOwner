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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.JSONUtil;
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


    public FragmentExtras() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentExtras newInstance(Restaurant res,Context mContext) {
        FragmentExtras fragment = new FragmentExtras();
        Bundle args = new Bundle();
        if(res.getRestaurant_name()!=null) {
            try {
                ArrayList<RestaurantService> serList = JSONUtil.readJSONServicesList(mContext);
                for(RestaurantService ser : serList){
                    if(ser.getRestaurantId().equals(res.getRestaurant_id())) {
                        args.putBoolean(ser.getName(), true);
                        if (ser.getName().equals("Squared Meters"))
                            args.putString("Squared Meters", ser.getAttribute());
                        if (ser.getName().equals("Closest Metro"))
                            args.putString("Closest Metro", ser.getAttribute());
                        if (ser.getName().equals("Closest Bus"))
                            args.putString("Closest Bus", ser.getAttribute());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            if (animals.isChecked()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Animals");
                list.add(rs);
            }
            if (vegan.isChecked()) {
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
            if (tv.isChecked()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("TV");
                list.add(rs);
            }
            if (patio.isChecked()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Patio");
                list.add(rs);
            }
            if (wifi.isChecked()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Wi-Fi");
                list.add(rs);
            }
            if (creditcard.isChecked()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Credit Card");
                list.add(rs);
            }
            if (babyspace.isChecked()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Baby Space");
                list.add(rs);
            }
            if (ac.isChecked()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("AC");
                list.add(rs);
            }
            if(!squaredMeters.getText().toString().equals("")) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Squared Meters");
                rs.setAttribute(squaredMeters.getText().toString());
                list.add(rs);
            }
            if(!closestMetro.getText().toString().equals("")) {
                RestaurantService rs1 = new RestaurantService();
                rs1.setName("Closest Metro");
                rs1.setAttribute(closestMetro.getText().toString());
                list.add(rs1);
            }
            if(!closestBus.getText().toString().equals("")) {
                RestaurantService rs2 = new RestaurantService();
                rs2.setName("Closest bus");
                rs2.setAttribute(closestBus.getText().toString());
                list.add(rs2);
            }
            dataPasser.onExtrasPass(list);
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
        public void onExtrasPass(List<RestaurantService> list);
    }
}
