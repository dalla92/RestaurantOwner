package it.polito.group2.restaurantowner;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

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
    public static FragmentExtras newInstance(int sectionNumber) {
        FragmentExtras fragment = new FragmentExtras();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
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
        return rootView;
    }

    public void passData() {
        if(dataPasser!=null) {
            ArrayList<RestaurantService> list = new ArrayList<RestaurantService>();
            if (animals.isSelected()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Animals");
                list.add(rs);
            }
            if (vegan.isSelected()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Vegan");
                list.add(rs);
            }
            if (vegetarian.isSelected()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Vegetarian");
                list.add(rs);
            }
            if (glutenFree.isSelected()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Gluten Free");
                list.add(rs);
            }
            if (tv.isSelected()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("TV");
                list.add(rs);
            }
            if (patio.isSelected()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Patio");
                list.add(rs);
            }
            if (wifi.isSelected()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Wi-Fi");
                list.add(rs);
            }
            if (creditcard.isSelected()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Credit Card");
                list.add(rs);
            }
            if (babyspace.isSelected()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("Baby Space");
                list.add(rs);
            }
            if (ac.isSelected()) {
                RestaurantService rs = new RestaurantService();
                rs.setName("AC");
                list.add(rs);
            }
            RestaurantService rs = new RestaurantService();
            rs.setName("Squared Meters");
            rs.setAttribute(squaredMeters.getText().toString());
            list.add(rs);
            RestaurantService rs1 = new RestaurantService();
            rs.setName("Closest Metro");
            rs.setAttribute(closestMetro.getText().toString());
            list.add(rs1);
            RestaurantService rs2 = new RestaurantService();
            rs.setName("Closest bus");
            rs.setAttribute(closestBus.getText().toString());
            list.add(rs2);
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
