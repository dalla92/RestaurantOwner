package it.polito.group2.restaurantowner.user.restaurant_list;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;

public class SearchActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private boolean isFirstChar = true;
    private CardView placesCard, restaurantsCard, tagCard;
    private FirebaseDatabase firebase;
    private ProgressDialog mProgressDialog;
    private ArrayList<String> names;
    private RecyclerView restaurantRecyclerView, placesRecyclerView, tagsRecyclerView;
    private RestaurantSearchAdapter restaurantAdapter;

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
                .enableAutoManage(this, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        names = new ArrayList<>();
        placesCard = (CardView) findViewById(R.id.places_card);
        restaurantsCard = (CardView) findViewById(R.id.restaurant_card);
        tagCard = (CardView) findViewById(R.id.tag_card);
        restaurantRecyclerView = (RecyclerView) findViewById(R.id.search_restaurant_list);
        placesRecyclerView = (RecyclerView) findViewById(R.id.search_place_list);
        tagsRecyclerView = (RecyclerView) findViewById(R.id.search_tag_list);


        restaurantAdapter = new RestaurantSearchAdapter(names);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantRecyclerView.setAdapter(restaurantAdapter);


        showProgressDialog();

        firebase.getReference("restaurant_names").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Boolean> namesMap = (Map<String, Boolean>) dataSnapshot.getValue();
                if(namesMap != null)
                    names.addAll(namesMap.keySet());

                for(String name: namesMap.keySet())
                    Log.d("prova", name);
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        EditText textView = (EditText) findViewById(R.id.search_text);
        assert textView != null;
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() < 1){
                    restaurantsCard.setVisibility(View.GONE);
                    placesCard.setVisibility(View.GONE);
                    tagCard.setVisibility(View.GONE);
                }
                if (s.length() > 1 && s.charAt(0) != '#') {
                    tagCard.setVisibility(View.GONE);

                    if (names.size() > 0) {
                        ArrayList<String> matches = new ArrayList<>();
                        for (int i = 0; i < names.size(); i++) {
                            String name = names.get(i);
                            if (name.toLowerCase().startsWith(s.toString().toLowerCase())) {
                                matches.add(name);
                            }
                        }
                        if (matches.size() > 0) {
                            restaurantAdapter.setData(matches);
                            restaurantsCard.setVisibility(View.VISIBLE);
                        } else
                            restaurantsCard.setVisibility(View.GONE);
                    }
                    //placesCard.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ImageView icon_location = (ImageView) findViewById(R.id.icon_location);
        assert icon_location != null;
        icon_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SearchActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                    Log.d("prova", "no permission");
                }

                PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);

                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                    @Override
                    public void onResult(@NotNull PlaceLikelihoodBuffer likelyPlaces) {

                        Log.d("prova", likelyPlaces.getStatus().toString());

                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            Log.i("prova", String.format("Place '%s' has likelihood: %g",
                                    placeLikelihood.getPlace().getName(),
                                    placeLikelihood.getLikelihood()));
                        }
                        likelyPlaces.release();
                    }
                });
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

    @Override
    protected void onStart() {
        super.onStart();
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

}

