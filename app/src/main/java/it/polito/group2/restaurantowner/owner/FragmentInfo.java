package it.polito.group2.restaurantowner.owner;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.user.search.PlaceSuggestionAdapter;

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
    private AutoCompleteTextView address;
    private EditText phone;
    private Spinner category;
    private static GoogleApiClient mGoogleApiClient;
    private static Context context;
    private PlaceSuggestionAsyncTask asyncTask = new PlaceSuggestionAsyncTask();
    private String searchedText;

    public FragmentInfo() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static  FragmentInfo newInstance(Restaurant res, GoogleApiClient mClient, AddRestaurantActivity addRestaurantActivity) {
        mGoogleApiClient = mClient;
        context = addRestaurantActivity;
        FragmentInfo fragment = new FragmentInfo();
        Bundle args = new Bundle();
        if(res.getRestaurant_id()!=null) {
            args.putString("Name", res.getRestaurant_name());
            args.putString("Address", res.getRestaurant_address());
            args.putString("Phone", res.getRestaurant_telephone_number());
            args.putString("Category", res.getRestaurant_category());
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
        address = (AutoCompleteTextView) rootView.findViewById(R.id.resadd_info_address);
        address.addTextChangedListener(new AddressTextWatcher());
        address.setDropDownAnchor(R.id.resadd_info_address);
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

    public class AddressTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            searchedText = s.toString();
            if(asyncTask.getStatus().equals(AsyncTask.Status.RUNNING))
                asyncTask.cancel(true);
            else{
                asyncTask = new PlaceSuggestionAsyncTask();
                asyncTask.execute();
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private class PlaceSuggestionAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).build();
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            if(isCancelled())
                return null;
            try {
                List<Address> list = geocoder.getFromLocationName(Locale.ITALY.getCountry(), 1);
                if(!list.isEmpty()) {
                    LatLngBounds bounds = new LatLngBounds(new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude()), new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude()));
                    ArrayList<String> addressList = new ArrayList<>();
                    ArrayList<AutocompletePrediction> predictions = getAutocomplete(searchedText, bounds, autocompleteFilter, this);
                    if(predictions != null)
                        for(AutocompletePrediction prediction: predictions) {
                            if(isCancelled())
                                return null;
                            String address = getFormattedAddress(prediction);
                            addressList.add(address);
                        }
                    return addressList;
                }
                return new ArrayList<>();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private String getFormattedAddress(AutocompletePrediction prediction) {
            String secondaryText = prediction.getSecondaryText(null).toString();
            int firstComma = secondaryText.indexOf(',');
            int lastComma =secondaryText.lastIndexOf(',');

            String address = prediction.getPrimaryText(null).toString().trim();
            String city = secondaryText.substring(0, firstComma);
            String country = secondaryText.substring(lastComma + 1).trim();
            Log.d("prova", address);
            Log.d("prova", city);
            Log.d("prova", country);
            Log.d("prova", "--------------------------------------");
            return address + ", " + city + ", " + country;
        }

        @Override
        protected void onCancelled(){
            asyncTask = new PlaceSuggestionAsyncTask();
            asyncTask.execute();
        }

        @Override
        protected void onPostExecute(ArrayList<String> prediction_list){
            if(prediction_list != null && prediction_list.size() > 0){
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_dropdown_item_1line, prediction_list);
                address.setAdapter(adapter);
                address.setThreshold(1);
            }
        }
    }

    private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint, LatLngBounds mBounds, AutocompleteFilter mPlaceFilter, PlaceSuggestionAsyncTask asyncTask) {
        if (mGoogleApiClient.isConnected()) {
            Log.i("prova", "Starting autocomplete query for: " + constraint);

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, constraint.toString(), mBounds, mPlaceFilter);

            if(asyncTask.isCancelled())
                return null;
            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            AutocompletePredictionBuffer autocompletePredictions = results.await(1, TimeUnit.SECONDS);


            if(asyncTask.isCancelled())
                return null;

            // Confirm that the query completed successfully, otherwise return null
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                //Toast.makeText(this, "Error contacting API: " + status.toString(), Toast.LENGTH_SHORT).show();
                Log.e("prova", "Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i("prova", "Query completed. Received " + autocompletePredictions.getCount() + " predictions.");

            // Freeze the results immutable representation that can be stored safely.
            return DataBufferUtils.freezeAndClose(autocompletePredictions);
        }
        Log.e("rpova", "Google API client is not connected for autocomplete query.");
        return null;
    }

    public interface OnInfoPass {
        public void onInfoPass(String data,String address, String phone, String category);
    }
}
