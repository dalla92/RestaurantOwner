package it.polito.group2.restaurantowner.user.search;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import it.polito.group2.restaurantowner.R;

public class SearchActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private GoogleApiClient mGoogleApiClient;
    private boolean isFirstChar = true;
    private CardView restaurantsCard, placesCard, tagCard;
    private FirebaseDatabase firebase;
    private ProgressDialog mProgressDialog;
    private HashMap<String, String> restaurantNamesIDMap;
    private HashMap<String, HashMap<String, Boolean>> tagNamesIDMap;
    private RecyclerView restaurantRecyclerView, placesRecyclerView, tagsRecyclerView;
    private RestaurantSuggestionAdapter restaurantAdapter;
    private TagSuggestionAdapter tagAdapter;
    private Location mLastLocation;
    private LocationSettingsRequest.Builder builder;
    private ProgressBar progressBar;
    private boolean empty = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        firebase = FirebaseDatabase.getInstance();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        restaurantNamesIDMap = new HashMap<>();
        tagNamesIDMap = new HashMap<>();
        placesCard = (CardView) findViewById(R.id.places_card);
        restaurantsCard = (CardView) findViewById(R.id.restaurant_card);
        tagCard = (CardView) findViewById(R.id.tag_card);
        restaurantRecyclerView = (RecyclerView) findViewById(R.id.search_restaurant_list);
        placesRecyclerView = (RecyclerView) findViewById(R.id.search_place_list);
        tagsRecyclerView = (RecyclerView) findViewById(R.id.search_tag_list);


        restaurantAdapter = new RestaurantSuggestionAdapter(restaurantNamesIDMap, this);
        restaurantRecyclerView.setHasFixedSize(true);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantRecyclerView.setAdapter(restaurantAdapter);

        tagAdapter = new TagSuggestionAdapter(tagNamesIDMap, this);
        tagsRecyclerView.setHasFixedSize(true);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tagsRecyclerView.setAdapter(tagAdapter);

        /*firebase.getReference("restaurant_names").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                restaurantNamesIDMap = (HashMap<String, String>) dataSnapshot.getValue();
                if(restaurantNamesIDMap != null)
                    names.addAll(restaurantNamesIDMap.keySet());

                for(String name: restaurantNamesIDMap.keySet())
                    Log.d("prova", name);
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

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

                    Query restaurantQuery = firebase.getReference("/restaurant_names").orderByKey().startAt(s.toString()).endAt(s.toString()+"\uf8ff");
                    restaurantQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!empty) {
                                HashMap<String, String> matches = (HashMap<String, String>) dataSnapshot.getValue();
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

        /*ImageView icon_location = (ImageView) findViewById(R.id.icon_location);
        assert icon_location != null;
        icon_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                PendingResult<LocationSettingsResult> result =
                        LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(@NotNull LocationSettingsResult locationSettingsResult) {

                        final Status status = locationSettingsResult.getStatus();
                        final LocationSettingsStates LS_state = locationSettingsResult.getLocationSettingsStates();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                Log.d("prova", "success");
                               getLocation();

                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the user
                                // a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    status.startResolutionForResult(SearchActivity.this, REQUEST_CHECK_SETTINGS);

                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way to fix the
                                // settings so we won't show the dialog.
                                hideProgressDialog();
                                Log.d("prova", "error with location");
                                break;
                        }
                    }
                });

            }
        });*/

        ImageView iconBack = (ImageView) findViewById(R.id.icon_back);
        assert iconBack != null;
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("prova", "no permission");
        }

        Geocoder geocoder = new Geocoder(SearchActivity.this, Locale.getDefault());
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 5);
                for(Address address: addresses)
                    Log.d("prova", address.toString());
            } catch (IOException e) {
                Log.e("prova", "Service Not Available", e);
                hideProgressDialog();
            }
        } else {
            hideProgressDialog();
            Log.d("prova", "location null");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d("prova", "resultOk");
                       getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d("prova", "result_canceled");
                        hideProgressDialog();
                        break;
                    default:
                        break;
                }
                break;
        }
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

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onConnectionFailed(@NotNull ConnectionResult connectionResult) {
        Toast.makeText(SearchActivity.this, "Connection to the google API failed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}

