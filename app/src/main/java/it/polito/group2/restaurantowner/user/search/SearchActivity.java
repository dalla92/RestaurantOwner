package it.polito.group2.restaurantowner.user.search;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;

public class SearchActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {


    private GoogleApiClient mGoogleApiClient;
    private CardView restaurantsCard, placesCard, tagCard;
    private FirebaseDatabase firebase;
    private ProgressDialog mProgressDialog;
    private HashMap<String, HashMap<String, Boolean>> restaurantNamesIDMap;
    private HashMap<String, HashMap<String, Boolean>> tagNamesIDMap;
    private List<AutocompletePrediction> predictionList;
    private RecyclerView restaurantRecyclerView, tagRecyclerView, placeRecyclerView;
    private RestaurantSuggestionAdapter restaurantAdapter;
    private TagSuggestionAdapter tagAdapter;
    private PlaceSuggestionAdapter placeAdapter;
    private ProgressBar progressBar;
    private LatLngBounds bounds;
    private boolean empty = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        firebase = FirebaseDatabase.getInstance();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();

        //bounds = new LatLngBounds(new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
        mProgressDialog = FirebaseUtil.initProgressDialog(this);

        FirebaseUtil.showProgressDialog(mProgressDialog);
        Geocoder geocoder = new Geocoder(SearchActivity.this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocationName(Locale.ITALY.getCountry(), 1);
            FirebaseUtil.hideProgressDialog(mProgressDialog);
            if(!list.isEmpty())
                bounds = new LatLngBounds(new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude()), new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude()));
        } catch (IOException e) {
            e.printStackTrace();
            FirebaseUtil.hideProgressDialog(mProgressDialog);
        }

        /*mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();*/

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        restaurantNamesIDMap = new HashMap<>();
        tagNamesIDMap = new HashMap<>();
        predictionList = new ArrayList<>();

        placesCard = (CardView) findViewById(R.id.places_card);
        restaurantsCard = (CardView) findViewById(R.id.restaurant_card);
        tagCard = (CardView) findViewById(R.id.tag_card);
        restaurantRecyclerView = (RecyclerView) findViewById(R.id.search_restaurant_list);
        placeRecyclerView = (RecyclerView) findViewById(R.id.search_place_list);
        tagRecyclerView = (RecyclerView) findViewById(R.id.search_tag_list);


        restaurantAdapter = new RestaurantSuggestionAdapter(restaurantNamesIDMap, this);
        restaurantRecyclerView.setHasFixedSize(true);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantRecyclerView.setAdapter(restaurantAdapter);

        tagAdapter = new TagSuggestionAdapter(tagNamesIDMap, this);
        tagRecyclerView.setHasFixedSize(true);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tagRecyclerView.setAdapter(tagAdapter);

        placeAdapter = new PlaceSuggestionAdapter(predictionList, this);
        placeRecyclerView.setHasFixedSize(true);
        placeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeRecyclerView.setAdapter(placeAdapter);

        EditText textView = (EditText) findViewById(R.id.search_text);
        assert textView != null;
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() < 1){
                    empty = true;
                    restaurantsCard.setVisibility(View.GONE);
                    placesCard.setVisibility(View.GONE);
                    tagCard.setVisibility(View.GONE);
                }
                if (s.length() > 0 && s.charAt(0) != '#') {
                    empty = false;
                    progressBar.setVisibility(View.VISIBLE);
                    tagCard.setVisibility(View.GONE);

                    Query restaurantQuery = firebase.getReference("restaurant_names").orderByKey().startAt(s.toString().toLowerCase()).endAt(s.toString().toLowerCase()+"\uf8ff");
                    restaurantQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!empty) {
                                HashMap<String, HashMap<String, Boolean>> matches = (HashMap<String, HashMap<String, Boolean>>) dataSnapshot.getValue();
                                if (matches != null && matches.size() > 0) {
                                    restaurantAdapter.setData(matches);
                                    restaurantsCard.setVisibility(View.VISIBLE);
                                } else {
                                    restaurantsCard.setVisibility(View.GONE);
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                            else
                                progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.d("prova", "cancelled");
                        }
                    });

                    if(s.length() > 2){
                        PlaceSuggestionAsyncTask suggestions = new PlaceSuggestionAsyncTask();
                        suggestions.execute(s.toString());
                    }
                }

                if(s.length() > 1 && s.charAt(0) == '#'){
                    progressBar.setVisibility(View.VISIBLE);
                    restaurantsCard.setVisibility(View.GONE);
                    placesCard.setVisibility(View.GONE);

                    Query tagsQuery = firebase.getReference("/tag_names").orderByKey().startAt(s.toString().substring(1)).endAt(s.toString().substring(1)+"\uf8ff");
                    tagsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, HashMap<String, Boolean>> matches = (HashMap<String, HashMap<String, Boolean>>)dataSnapshot.getValue();

                            if(matches != null && matches.size() > 0) {
                                tagAdapter.setData(matches);
                                tagCard.setVisibility(View.VISIBLE);
                            }
                            else{
                                tagCard.setVisibility(View.GONE);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.d("prova", "cancelled");
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ImageView iconBack = (ImageView) findViewById(R.id.icon_back);
        assert iconBack != null;
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private class PlaceSuggestionAsyncTask extends AsyncTask<String, Void, List<AutocompletePrediction>>{

        @Override
        protected List<AutocompletePrediction> doInBackground(String... params) {
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).build();
            return getAutocomplete(params[0], bounds, autocompleteFilter);
        }

        @Override
        protected void onPostExecute(List<AutocompletePrediction> prediction_list){
            progressBar.setVisibility(View.INVISIBLE);
            if(prediction_list.size() > 0){
                placeAdapter.setData(prediction_list);
                placesCard.setVisibility(View.VISIBLE);
            }
            else
                placesCard.setVisibility(View.GONE);

        }
    }

    private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint, LatLngBounds mBounds,  AutocompleteFilter mPlaceFilter) {
        if (mGoogleApiClient.isConnected()) {
            Log.i("prova", "Starting autocomplete query for: " + constraint);

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, constraint.toString(), mBounds, mPlaceFilter);

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            AutocompletePredictionBuffer autocompletePredictions = results.await(60, TimeUnit.SECONDS);

            // Confirm that the query completed successfully, otherwise return null
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Toast.makeText(this, getResources().getString(R.string.technical_problem), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();
        if( mGoogleApiClient != null )
            mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( mGoogleApiClient != null )
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NotNull ConnectionResult connectionResult) {
        Toast.makeText(SearchActivity.this, getResources().getString(R.string.technical_problem), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}

