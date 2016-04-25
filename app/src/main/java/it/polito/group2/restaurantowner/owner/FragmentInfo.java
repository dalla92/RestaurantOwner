package it.polito.group2.restaurantowner.owner;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import it.polito.group2.restaurantowner.R;

/**
 * Created by Daniele on 07/04/2016.
 */
public class FragmentInfo extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    String selectedCategory = null;
    OnInfoPass dataPasser;
    private EditText name;
    private EditText address;
    private EditText phone;
    private Spinner category;

    public FragmentInfo() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentInfo newInstance(Restaurant res) {
        FragmentInfo fragment = new FragmentInfo();
        Bundle args = new Bundle();
        if(res.getName()!=null) {
            args.putString("Name", res.getName());
            args.putString("Address", res.getAddress());
            args.putString("Phone", res.getPhoneNum());
            args.putString("Category", res.getCategory());
        }
        fragment.setArguments(args);
        return fragment;
    }



    public void passData() {
        dataPasser.onInfoPass(name.getText().toString(), address.getText().toString(), phone.getText().toString(), String.valueOf(category.getSelectedItem()));
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Log.d("aaa", "passed in fragment info");
            dataPasser = (OnInfoPass) a;
        } catch (ClassCastException e) {
            throw new ClassCastException(a.toString()
                    + " must implement OnInfoPass");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_restaurant_info, container, false);
        name = (EditText) rootView.findViewById(R.id.resadd_info_name);
        address = (EditText) rootView.findViewById(R.id.resadd_info_address);
        phone = (EditText) rootView.findViewById(R.id.resadd_info_phone);
        category = (Spinner) rootView.findViewById(R.id.resadd_info_category);


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        category.setAdapter(adapter);
        if(!getArguments().isEmpty()){
            name.setText(getArguments().getString("Name"));
            address.setText(getArguments().getString("Address"));
            phone.setText(getArguments().getString("Phone"));
            for(int i = 0;i<adapter.getCount();i++) {
                CharSequence item = adapter.getItem(i);
                if (item.toString().equals(getArguments().getString("Category")))
                    category.setSelection(i);
            }
        }
        selectedCategory = String.valueOf(category.getSelectedItem());

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return rootView;
    }

    public interface OnInfoPass {
        public void onInfoPass(String data,String address, String phone, String category);
    }
}