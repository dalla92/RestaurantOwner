package it.polito.group2.restaurantowner.user.restaurant_list;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import org.jetbrains.annotations.NotNull;

import it.polito.group2.restaurantowner.R;

public class SearchActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private boolean isFirstChar = true;
    private CardView placesCard, restaurantsCard, tagCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        placesCard = (CardView) findViewById(R.id.places_card);
        restaurantsCard = (CardView) findViewById(R.id.restaurant_card);
        tagCard = (CardView) findViewById(R.id.tag_card);

        String[] COUNTRIES = new String[] {
                "Belgium", "France", "Italy", "Germany", "Spain"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.search_text);
        assert textView != null;
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 1 && s.charAt(0) == '#'){

                    tagCard.setVisibility(View.VISIBLE);
                    placesCard.setVisibility(View.GONE);
                    restaurantsCard.setVisibility(View.GONE);
                }
                else{
                    tagCard.setVisibility(View.GONE);
                    placesCard.setVisibility(View.VISIBLE);
                    restaurantsCard.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        textView.setAdapter(adapter);

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
                       /*PlaceLikelihood placeLikelihood = likelyPlaces.get( 0 );
                        String content = "";
                        if( placeLikelihood != null && placeLikelihood.getPlace() != null && !TextUtils.isEmpty( placeLikelihood.getPlace().getName() ) )
                            content = "Most likely place: " + placeLikelihood.getPlace().getName() + "\n";
                        if( placeLikelihood != null )
                            content += "Percent change of being there: " + (int) ( placeLikelihood.getLikelihood() * 100 ) + "%";
                        Log.d("prova", content + "content");
                        likelyPlaces.release();*/

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

    @Override
    public void onConnectionFailed(@NotNull ConnectionResult connectionResult) {
        Toast.makeText(SearchActivity.this, "Connection to the google API failed!", Toast.LENGTH_SHORT).show();
    }

}

