/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.polito.group2.restaurantowner.user.restaurant_list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import android.Manifest;
import android.content.Intent;

import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.firebasedata.RestaurantPreview;
import it.polito.group2.restaurantowner.user.restaurant_list.StreetViewActivity;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;

public class MapsActivity extends AppCompatActivity implements
        OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    protected static final String TAG = "location-updates-sample";
    protected Boolean mRequestingLocationUpdates; //Tracks the status of the location updates request if started or not
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000; //The desired interval for location updates. Inexact. Updates may be more or less frequent.
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS * 3; //The fastest rate for active location updates. Exact. Updates will never be more frequent than this value.
    protected Location mCurrentLocation;
    private Marker mLastSelectedMarker;
    private Marker mLastUserMarker;
    private String mLastUpdateTime;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    //UI
    protected TextView mLastUpdateTimeTextView;
    protected String mLastUpdateTimeLabel;

    private GoogleMap mMap;
    private ClusterManager<RestaurantPreview> mClusterManager;
    private Set<Marker> all_markers = new HashSet<Marker>();
    private final int REQUEST_CHECK_SETTINGS = 1;
    private final float DEFAULT_ZOOM2 = 14.0f;
    private final int DEFAULT_PADDING = 100;
    private double DEFAULT_RADIUS = 0; ///1000 m is 1 km

    private final Random mRandom = new Random();
    PermissionListener dialogPermissionListener_gps;
    PermissionListener dialogPermissionListener_wifi;
    private ProgressDialog progressDialog;
    private int d_height;
    private int d_width;
    private List<RestaurantPreview> near_restaurants_previews_list = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);

        updateValuesFromBundle(savedInstanceState);

        // Locate the UI widgets.
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.last_update_time_text);
        // Set labels.
        mLastUpdateTimeLabel = getResources().getString(R.string.last_update_time_label);
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        buildGoogleApiClient();

        Dexter.initialize(this);
        Dexter.continuePendingRequestIfPossible(dialogPermissionListener_gps);
        Dexter.continuePendingRequestIfPossible(dialogPermissionListener_wifi);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setUpMap();
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Bundle b = getIntent().getExtras();
        if(b!=null && b.getDouble("range")!=0){
            DEFAULT_RADIUS = b.getDouble("range");
        }
        if(b!=null && b.getParcelableArrayList("restaurants_previews_list")!=null){ //it must be always this way, because otherwise this activity is not started
            near_restaurants_previews_list = b.getParcelableArrayList("restaurants_previews_list");
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
                //setButtonsEnabledState();
            }
            // Update the value of mCurrentLocation
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            //updateUI();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /*
    public void startUpdatesButtonHandler() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            //setButtonsEnabledState();
            startLocationUpdates();
        }
    }

    public void stopUpdatesButtonHandler() {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            //setButtonsEnabledState();
            stopLocationUpdates();
        }
    }
    */

    @Override
    public void onBackPressed() {
        OnBackUtil.clean_stack_and_go_to_user_restaurant_list(this);
    }

    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
        catch(SecurityException e){
            Log.d("aaa", "Exception in onConnected");
        }
    }

    private void updateUI() {
        if(mMap!=null) {
            //mMap.clear();
            if (mCurrentLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), DEFAULT_ZOOM2));
                mLastUpdateTimeTextView.setText(String.format("%s: %s", mLastUpdateTimeLabel, mLastUpdateTime));

                if(mLastUserMarker==null) {
                    mLastUserMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                                    .title("Your position")
                                            //.snippet("Population: 2,074,200")
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_navigation_arrow))
                                    .visible(false)
                    );
                }
                mLastUserMarker.setPosition(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));

                prepare_clustering();
                if(DEFAULT_RADIUS!=0 && mLastUserMarker!=null){
                    //add circle
                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(mLastUserMarker.getPosition().latitude, mLastUserMarker.getPosition().longitude))
                            .radius(DEFAULT_RADIUS)
                            .strokeColor(Color.BLACK));
                    //.fillColor(Color.BLUE)
                }
            }
        }
    }

    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
            grantPermissions();
        }

        @Override
        public void onResume() {
            super.onResume();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) { //Runs when a GoogleApiClient object successfully connects.
        Log.i(TAG, "Connected to GoogleApiClient");
        mRequestingLocationUpdates = true;
        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            try {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            catch(SecurityException e){
                Log.d("aaa", "Exception in onConnected");
            }
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        updateUI();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
        /*
        Toast.makeText(this, getResources().getString(R.string.location_updated_message),
                Toast.LENGTH_SHORT).show();
        */
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void grantPermissions() {
        if (android.os.Build.VERSION.RELEASE.startsWith("6.")){
            // only for Marshmallow and newer versions
            //I want that first I request GPS, but if rejected, request WIFI
            /*
            dialogPermissionListener_gps =
    /*        dialogPermissionListener_gps =
                    DialogOnDeniedPermissionListener.Builder
                            .withContext(this)
                            .withTitle("GPS permission")
                            .withMessage("GPS permission is needed to take your accurate position")
                            .withButtonText(android.R.string.ok)
                            .withIcon(R.mipmap.ic_launcher)
                            .build();
            Dexter.checkPermissionOnSameThread(dialogPermissionListener_gps, Manifest.permission.ACCESS_FINE_LOCATION);
            */
            if(!Dexter.isRequestOngoing()) {
                Dexter.checkPermission(dialogPermissionListener_gps, Manifest.permission.ACCESS_FINE_LOCATION);

                Dexter.checkPermission(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        permission_granted();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        gps_denied();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }, Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
        else{
            mGoogleApiClient.connect();

            settingsrequest();
        }
    }

    public void settingsrequest(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        //builder.setAlwaysShow(true); //this is the key ingredient if I want to force this permission

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        //settingsrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }


    public void gps_denied(){
        //does nothing
    }

    public void permission_granted(){
        //get actual/last position
        mGoogleApiClient.connect();
    }

    public void permission_denied(){
        //does nothing
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (mMap != null) {
            return;
        }

        mMap = map;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        try {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (mMap.getUiSettings().isMyLocationButtonEnabled()) {
                        stopLocationUpdates();
                        Toast.makeText(MapsActivity.this, "Location updates stopped", Toast.LENGTH_SHORT).show();
                    } else {
                        startLocationUpdates();
                        Toast.makeText(MapsActivity.this, "Location updates started", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
        }
        catch(SecurityException e){
            Log.d("aaa", "Exception in onMapReady");
        }
        //focus camera on Turin
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.063911, 7.658844), DEFAULT_ZOOM2));
        //mMap.getUiSettings().setZoomControlsEnabled(false); hide controls

        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        //mMap.setOnMarkerDragListener(this);

        // Override the default content description on the view
        map.setContentDescription(" 'Map Description' ");
    }

    public void enlarge_camera(){
        // Pan to see all markers in view (cannot zoom to bounds until the map has a size)
        final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation") // We use the new method when supported
                @SuppressLint("NewApi") // We check which build version we are using.
                @Override
                public void onGlobalLayout() {
                    LatLngBounds bounds = null;

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    if(mLastUserMarker!=null)
                        builder.include(mLastUserMarker.getPosition());
                    for (Marker marker : all_markers) {
                        builder.include(marker.getPosition());
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    if(bounds != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, DEFAULT_PADDING));
                    }
                }
            });
        }
    }

    /*
    public void add_restaurant_preview_if_near(){
        for(RestaurantPreview r : restaurants_previews_list){
            String distance_string_formatted = calculate_distance(r.getPosition(), mLastUserMarker);
            String[] two_strings = distance_string_formatted.split("\\s+");
            double distance = Double.valueOf(two_strings[0]);
            String unit = two_strings[1];
            //all restaurants in 1 km
            if(!unit.equals("km"))
                near_restaurants_previews_list.add(r);
        }
    }
    */

    public void prepare_clustering(){
        mClusterManager = new ClusterManager<RestaurantPreview>(this, mMap);
        mMap.setOnCameraChangeListener(mClusterManager);
        mClusterManager.setRenderer(new MyClusterRenderer(this, mMap, mClusterManager));
        mClusterManager.addItems(near_restaurants_previews_list);
    }

    public void onStreetView(View view){
        if(mCurrentLocation != null) {
            Bundle b = new Bundle();
            Intent intent = new Intent(
                    getApplicationContext(),
                    StreetViewActivity.class);
            b.putParcelable("STREET_VIEW_POSITION", new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // This causes the marker to change color
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        // or random color: marker.setIcon(BitmapDescriptorFactory.defaultMarker(mRandom.nextFloat() * 360));

        show_restaurant_popup(marker);

        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).

        //return true or false?
        return false;
    }

    public void show_restaurant_popup(Marker marker){
        mLastSelectedMarker = marker;

        //fill marker window
        final RestaurantPreview restaurant_preview = take_right_restaurant_preview_by_position(marker);
        if(restaurant_preview!=null) {
            final Dialog d = new Dialog(MapsActivity.this);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //d.setTitle("Select");
            //d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            d.setContentView(R.layout.restaurant_preview_map);
            TextView resName;
            RatingBar rating;
            TextView tablesNumber;
            TextView address;
            TextView distance;
            ImageButton button_get_directions;
            ImageButton button_get_info;
            ImageButton button_get_street_view;
            TextView open_now;

            resName = (TextView) d.findViewById(R.id.textViewName);
            rating = (RatingBar) d.findViewById(R.id.ratingBar);
            tablesNumber = (TextView) d.findViewById(R.id.textViewTablesNumber);
            distance = (TextView) d.findViewById(R.id.textViewDistance);
            address = (TextView) d.findViewById(R.id.textViewAddress);
            button_get_directions = (ImageButton) d.findViewById(R.id.button_get_directions);
            button_get_info = (ImageButton) d.findViewById(R.id.button_get_info);
            button_get_street_view = (ImageButton) d.findViewById(R.id.button_get_street_view);
            open_now = (TextView) d.findViewById(R.id.restaurant_today_time);

            d_height = getWindow().getDecorView().getHeight();
            d_width = getWindow().getDecorView().getWidth();
            Glide.with(getApplicationContext()).load(restaurant_preview.getRestaurant_cover_firebase_URL()).asBitmap().into(new SimpleTarget<Bitmap>(
                    d_width, d_height) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable drawable = new BitmapDrawable(resource);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        d.findViewById(R.id.father_linear_layout).setBackground(drawable);
                        d.findViewById(R.id.father_linear_layout).setAlpha(0.88f);
                    }
                }
            });

            //fill_restaurant_preview
            resName.setText(restaurant_preview.getRestaurant_name());
            rating.setRating(restaurant_preview.getRestaurant_rating());
            if(restaurant_preview.getTables_number()!=0)
                tablesNumber.setText(String.valueOf(restaurant_preview.getTables_number()));
            address.setText(retrieve_address_by_geocoding(mLastSelectedMarker.getPosition(), mLastSelectedMarker.getPosition())); //or with one query
            String string_to_round = calculate_distance(mLastSelectedMarker.getPosition(), mLastUserMarker);
            String[] parts_to_round = string_to_round.split("\\s+");
            int rounded_value = Math.round(Float.valueOf(parts_to_round[0].replace(",",".")));
            String string_result = String.valueOf(rounded_value) + " " + parts_to_round[1];
            distance.setText(string_result);
            if(restaurant_preview.isOpenNow()) {
                open_now.setText("Open now");
            }
            else{
                open_now.setText("Closed now");
            }
            button_get_info.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(
                                    getApplicationContext(),
                                    UserRestaurantActivity.class);
                            Bundle b = new Bundle();
                            b.putString("restaurant_id", restaurant_preview.getRestaurant_id());
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    }
            );
            button_get_street_view.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle b = new Bundle();
                            Intent intent = new Intent(
                                    getApplicationContext(),
                                    StreetViewActivity.class);
                            b.putParcelable("STREET_VIEW_POSITION", new LatLng(restaurant_preview.getLat(), restaurant_preview.getLon()));
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    }
            );
            button_get_directions.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?daddr=" + restaurant_preview.getLat() + "," + restaurant_preview.getLon()));
                            startActivity(intent);
                        }
                    }
            );

            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    //if (marker.equals(mPerth)) {
                    // This causes the marker at Perth to bounce into position when it is clicked.
                    final Handler handler = new Handler();
                    final long start = SystemClock.uptimeMillis();
                    final long duration = 1500;
                    final Interpolator interpolator = new BounceInterpolator();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            long elapsed = SystemClock.uptimeMillis() - start;
                            float t = Math.max(
                                    1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                            mLastSelectedMarker.setAnchor(0.5f, 1.0f + 2 * t);
                            if (t > 0.0) {
                                // Post again 16ms later.
                                handler.postDelayed(this, 16);
                            }
                        }
                    });
                }
            });

            d.show();
        }
    }

    public String retrieve_address_by_geocoding(LatLng latitude, LatLng longitude){
        Geocoder geoCoder = new Geocoder(this);
        try {
            List<Address> matches = geoCoder.getFromLocation(latitude.latitude, longitude.longitude, 1);
            Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
            return bestMatch.getAddressLine(0); // + bestMatch.getAddressLine(1)
        }
        catch(IOException e) {
            Log.d("aaa", "Exception in retrieve_address_by_geocoding");
        }

        return null;
    }

    public String calculate_distance(LatLng a, Marker b) {
        double distance = SphericalUtil.computeDistanceBetween(a, b.getPosition());
        return formatNumber(distance);
    }

    private String formatNumber(double distance) {
        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        //return String.format("%4.3f%s", distance, unit);
        //trying to add space to split later
        return String.format("%4.3f %s", distance, unit);
    }

    public RestaurantPreview take_right_restaurant_preview_by_position(Marker marker){
        LatLng actual_marker_position = marker.getPosition();
        for(RestaurantPreview r : near_restaurants_previews_list){
            if(r.getPosition().equals(actual_marker_position))
                return r;
        }

        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show();
    }

    /*Customizing the info window and/or its contents.
    class CustomInfoWindowAdapter implements InfoWindowAdapter {

        private final View mWindow;
        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            /*
            if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_window) {
                // This means that getInfoContents will be called.
                return null;
            }

            render(marker, mWindow);
            return mWindow;
        }
        @Override
        public View getInfoContents(Marker marker) {
            /*
            if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_contents) {
                // This means that the default info contents will be used.
                return null;
            }

            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            /*
            int badge;
            // Use the equals() method on a Marker to check for equals.  Do not use ==.
            if (marker.equals(mBrisbane)) {
                badge = R.mipmap.ic_place_marker;
            } else if (marker.equals(mAdelaide)) {
                badge = R.mipmap.ic_place_marker;
            } else if (marker.equals(mSydney)) {
                badge = R.mipmap.ic_place_marker;
            } else if (marker.equals(mMelbourne)) {
                badge = R.mipmap.ic_place_marker;
            } else if (marker.equals(mPerth)) {
                badge = R.mipmap.ic_place_marker;
            } else {
                // Passing 0 to setImageResource will clear the image view.
                badge = 0;
            }
            ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);

            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }

        }
    }*/

    public class MyClusterRenderer extends DefaultClusterRenderer<RestaurantPreview> {

        //private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());

        public MyClusterRenderer(Context context, GoogleMap map, ClusterManager<RestaurantPreview> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected  void onClusterRendered(Cluster<RestaurantPreview> cluster, Marker marker){
            enlarge_camera();
        }

        @Override
        protected void onBeforeClusterItemRendered(RestaurantPreview item, MarkerOptions markerOptions) {
            BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(mRandom.nextFloat() * 360);
            markerOptions.icon(markerDescriptor);
        }

        @Override
        protected void onClusterItemRendered(RestaurantPreview clusterItem, Marker marker) {
            all_markers.add(marker);
            super.onClusterItemRendered(clusterItem, marker);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<RestaurantPreview> cluster, MarkerOptions markerOptions){
            super.onBeforeClusterRendered(cluster, markerOptions);
            /*
            final Drawable clusterIcon = getResources().getDrawable(R.drawable.ic_lens_black_24dp);
            clusterIcon.setColorFilter(getResources().getColor(android.R.color.holo_orange_light), PorterDuff.Mode.SRC_ATOP);

            mClusterIconGenerator.setBackground(clusterIcon);

            //modify padding for one or two digit numbers
            if (cluster.getSize() < 10) {
                mClusterIconGenerator.setContentPadding(40, 20, 0, 0);
            }
            else {
                mClusterIconGenerator.setContentPadding(30, 20, 0, 0);
            }

            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            */
        }
    }

    private void progress_dialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Its loading....");
        progressDialog.setTitle("");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }


}
