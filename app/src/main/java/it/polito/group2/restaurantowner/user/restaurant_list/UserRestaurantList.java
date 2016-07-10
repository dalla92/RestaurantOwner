package it.polito.group2.restaurantowner.user.restaurant_list;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import it.polito.group2.restaurantowner.R;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.firebasedata.RestaurantPreview;
import it.polito.group2.restaurantowner.firebasedata.RestaurantTimeSlot;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.owner.RecyclerItemClickListener;
import it.polito.group2.restaurantowner.user.restaurant_page.Filter;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;
import it.polito.group2.restaurantowner.user.search.SearchActivity;

public class UserRestaurantList extends AppCompatActivity
        implements
        OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener
{

    private final static int ACTION_FILTER = 10;
    private final static int ACTION_SEARCH = 2;
    private final static int REQUEST_CHECK_SETTINGS = 1;

    private UserRestaurantPreviewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    ArrayList<RestaurantPreview> restaurants_previews_list = new ArrayList<>();

    private double DEFAULT_RANGE = 5000; //dafault range is 2 km
    private double range = DEFAULT_RANGE;
    private GoogleMap mMap;
    private final float DEFAULT_ZOOM1 = 11.0f;
    private ClusterManager<MyItem> mClusterManager;
    private Marker mLastUserMarker;
    private LatLng searchedPosition;
    protected static final String TAG = "location-updates-sample";
    protected boolean mRequestingLocationUpdates = false; //Tracks the status of the location updates request if started or not
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000; //The desired interval for location updates. Inexact. Updates may be more or less frequent.
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS * 3; //The fastest rate for active location updates. Exact. Updates will never be more frequent than this value.
    protected Location mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    PermissionListener dialogPermissionListener_gps;
    private FirebaseDatabase firebase;
    private ProgressDialog mProgressDialog;
    private Toolbar toolbar;
    private TextView search;
    private FloatingActionButton fab;
    public static int index;
    private boolean isRequestedPermission = false;
    private boolean position_already_active = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_restaurant_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.gps_fab);
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        fab.setLayoutParams(p);
        fab.setVisibility(View.GONE);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        position_already_active = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); // Return a boolean
        Log.d("prova", "" + position_already_active);
        fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantList.this, R.drawable.ic_my_location_24dp));
        if(!position_already_active) {
            fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantList.this, R.drawable.ic_my_location_24dp));
            position_already_active = true;
        }
        else {
            fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantList.this, R.drawable.ic_my_location_on));
            position_already_active = false;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mProgressDialog = FirebaseUtil.initProgressDialog(this);

        firebase = FirebaseDatabase.getInstance();

        if (!haveNetworkConnection()) {
            create_dialog(this);
        }
        else {
            // Create an instance of GoogleAPIClient.
            createLocationRequest();

            ImageView filter_icon = (ImageView) findViewById(R.id.icon_filter);
            assert filter_icon != null;
            filter_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserRestaurantList.this, Filter.class);
                    startActivityForResult(intent, ACTION_FILTER);
                }
            });

            search = (TextView) findViewById(R.id.search_text);
            assert search != null;
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserRestaurantList.this, SearchActivity.class);
                    startActivityForResult(intent, ACTION_SEARCH);
                }
            });

            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);
            // use a linear layout manager
            GridLayoutManager mLayoutManager = null;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mLayoutManager = new GridLayoutManager(this, 1);
                mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, 5, true));
            }
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mLayoutManager = new GridLayoutManager(this, 2);
                mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 5, true));
            }

            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent mIntent = new Intent(UserRestaurantList.this, UserRestaurantActivity.class);
                            RestaurantPreview resPrev = mAdapter.getItem(position);
                            if (resPrev != null)
                                mIntent.putExtra("restaurant_id", resPrev.getRestaurant_id());
                            startActivity(mIntent);
                        }
                    })
            );

            mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        Log.d("prova", "scrolling up");
                        fab.setVisibility(View.INVISIBLE);
                    } else {
                        Log.d("prova", "scrolling down");
                        fab.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                        // Do something
                    } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        // Do something
                    } else {
                        // Do something
                    }
                }
            });

            mAdapter = new UserRestaurantPreviewAdapter(restaurants_previews_list, this, mLastUserMarker);
            mRecyclerView.setAdapter(mAdapter);

            setDrawer();

            setUpMap();
        }
    }

    public void create_dialog(Context context){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        // set title
        alertDialogBuilder.setTitle("Attention");
        // set dialog message
        alertDialogBuilder
                .setMessage("You need to provide internet access")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        UserRestaurantList.this.finish();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public void onConnected(Bundle connectionHint) { //Runs when a GoogleApiClient object successfully connects.
        Log.i(TAG, "Connected to GoogleApiClient");
        //fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantList.this, R.drawable.ic_my_location_on));
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
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        /*if (mRequestingLocationUpdates) {
            startLocationUpdates();
            read_restaurants_from_firebase(mRequestingLocationUpdates);
        }*/

        updateUI();
    }

    @Override
    public void onLocationChanged(Location location) {
        mRequestingLocationUpdates = true;

        mCurrentLocation = location;
        updateUI();

        if(isRequestedPermission) {
            read_restaurants_from_firebase(true);
            isRequestedPermission = false;
        }
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

    public void grantPermissions() {
        if(!position_already_active) {
            fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantList.this, R.drawable.ic_my_location_24dp));
            position_already_active = true;
        }
        else {
            fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantList.this, R.drawable.ic_my_location_on));
            position_already_active = false;
        }
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
//            Dexter.checkPermission(dialogPermissionListener_gps, Manifest.permission.ACCESS_FINE_LOCATION);

            Dexter.checkPermission(new PermissionListener() {
                @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                    permission_granted();
                }
                @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                    gps_denied();
                }
                @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }, Manifest.permission.ACCESS_FINE_LOCATION);
        }
        else{
            mGoogleApiClient.connect();

            settingsrequest();
        }
    }

    public void permission_granted(){
        //get actual/last position
        mGoogleApiClient.connect();
    }

    public void permission_denied(){
        //does nothing
    }

    public void gps_denied(){
        //does nothing
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (mMap != null) {
            return;
        }

        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grantPermissions();
            }
        });

        read_restaurants_from_firebase(false);

        mMap = map;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        catch(SecurityException e){
            Log.d("aaa", "Exception in onMapReady");
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                call_maps_activity();

            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                call_maps_activity();

            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                call_maps_activity();
                return false;
            }
        });

        maps_stuff();
        /*if(mRequestingLocationUpdates) {
            Log.d("prova", "ciao");
            read_restaurants_from_firebase(mRequestingLocationUpdates);
        }*/
    }

    public void read_restaurants_from_firebase(final boolean isPositionUp){
        mAdapter.clear();
        if(mClusterManager!=null)
            mClusterManager.clearItems();

        FirebaseUtil.showProgressDialog(mProgressDialog);
        //I want to get all restaurants within 2Km: I get all the ids of the restaurants, for each id I get its latitude and longitude, and if distance<2km I add it to restaurant_preview_list and cluster_manager

        DatabaseReference resPreviewRef = firebase.getReference("restaurants_previews");
        Query recentPostsQuery;
        if(!isPositionUp){
            recentPostsQuery = resPreviewRef.limitToFirst(15);
        }
        else{
            recentPostsQuery = resPreviewRef;
        }
        recentPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    RestaurantPreview resPreview = data.getValue(RestaurantPreview.class);
                    Double lat = resPreview.getLat();
                    Double lon = resPreview.getLon();

                    if (isPositionUp) {
                        //if i have the position of the user
                        if (lat != null && lon != null) {
                            if (is_restaurant_near_with_position(new LatLng(lat, lon), range)) {
                                restaurants_previews_list.add(resPreview);
                                mAdapter.addItem(resPreview);
                                mClusterManager.addItem(new MyItem(lat, lon));
                                mClusterManager.cluster();
                            }
                        }
                    } else {
                        //if I have the location searched by the user
                        if (lat != null && lon != null) {
                            if (is_restaurant_near_without_position(new LatLng(lat, lon), range)) {
                                restaurants_previews_list.add(resPreview);
                                mAdapter.addItem(resPreview);
                                mClusterManager.addItem(new MyItem(lat, lon));
                                mClusterManager.cluster();
                            }
                        }
                    }
                }
                FirebaseUtil.hideProgressDialog(mProgressDialog);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseUtil.hideProgressDialog(mProgressDialog);
            }
        });


        /*DatabaseReference restaurantDatabaseReference = firebase.getReference("restaurant_names");
        restaurantDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final String restaurant_id = (String) dataSnapshot.getValue();

                    DatabaseReference ref = firebase.getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants/" + restaurant_id + "/restaurant_latitude_position");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Double snap_lat = snapshot.getValue(Double.class);
                            final Double lat = snap_lat;
                            //get only longitude
                            DatabaseReference ref2 = firebase.getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants/" + restaurant_id + "/restaurant_longitude_position");
                            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    Double snap_long = snapshot.getValue(Double.class);
                                    final Double lon = snap_long;
                                    if (isPositionUp) {
                                        //if i have the position of the user
                                        if (lat != null && lon != null) {
                                            if (is_restaurant_near_with_position(new LatLng(lat, lon), range)) {
                                                DatabaseReference ref2 = firebase.getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants_previews/" + restaurant_id + "/");
                                                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot snapshot) {
                                                        RestaurantPreview snap_r_p = snapshot.getValue(RestaurantPreview.class);
                                                        //restaurants_previews_list.add(snap_r_p);
                                                        Log.d("prova", "added");

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError firebaseError) {
                                                        System.out.println("The read failed: " + firebaseError.getMessage());
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    else {
                                        //if I have the location searched by the user
                                        if (lat != null && lon != null) {
                                            if (is_restaurant_near_without_position(new LatLng(lat, lon), range)) {
                                                DatabaseReference ref2 = firebase.getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants_previews/" + restaurant_id + "");
                                                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot snapshot) {
                                                        RestaurantPreview snap_r_p = snapshot.getValue(RestaurantPreview.class);
                                                        //restaurants_previews_list.add(snap_r_p);
                                                        Log.d("prova", "added no search");
                                                        mAdapter.addItem(snap_r_p);
                                                        mClusterManager.addItem(new MyItem(lat, lon));
                                                        mClusterManager.cluster();
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError firebaseError) {
                                                        System.out.println("The read failed: " + firebaseError.getMessage());
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError firebaseError) {
                                    System.out.println("The read failed: " + firebaseError.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    });
                }

                FirebaseUtil.hideProgressDialog(mProgressDialog);

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });*/
    }

    public boolean is_restaurant_near_with_position(LatLng res_position, double range){
        double distance = calculate_distance2(res_position, mLastUserMarker.getPosition());
        if(distance<range){
            return true;
        }
        else
        return false;
    }

    public boolean is_restaurant_near_without_position(LatLng res_position, double range){
        if(searchedPosition == null)
            return true;
        double distance = calculate_distance2(res_position, searchedPosition);
        if(distance<range){
            return true;
        }
        else
            return false;
    }

    public double calculate_distance2(LatLng a, LatLng b) {
        double distance = SphericalUtil.computeDistanceBetween(a, b);
        return distance;
    }

    private void call_maps_activity(){
        Intent intent = new Intent(
                getApplicationContext(),
                MapsActivity.class);
        Bundle b = new Bundle();
        if(range == 0.0){
            range = DEFAULT_RANGE;
        }
        b.putDouble("range", range);
        b.putParcelableArrayList("restaurants_previews_list", mAdapter.getPreviews());
        intent.putExtras(b);
        if(mAdapter.getPreviews()!=null && mAdapter.getPreviews().size() > 0)
            startActivity(intent);
    }

    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    public void maps_stuff(){
        //Camera in Turin
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.063911, 7.658844), DEFAULT_ZOOM1));

        mClusterManager = new ClusterManager<MyItem>(this, mMap);
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem myItem) {
                call_maps_activity();
                return false;
            }
        });
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                call_maps_activity();
                return false;
            }
        });
        mMap.setOnCameraChangeListener(mClusterManager);
    }

    private void setDrawer() {
        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        final MenuItem ownerItem = menu.findItem(R.id.nav_owner);
        MenuItem loginItem = menu.findItem(R.id.nav_login);
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);
        MenuItem myProfileItem = menu.findItem(R.id.nav_my_profile);
        MenuItem myOrdersItem = menu.findItem(R.id.nav_my_orders);
        MenuItem mrResItem =  menu.findItem(R.id.nav_my_reservations);
        MenuItem myReviewsItem = menu.findItem(R.id.nav_my_reviews);
        MenuItem myFavItem = menu.findItem(R.id.nav_my_favourites);

        ownerItem.setVisible(false);
        String userID = FirebaseUtil.getCurrentUserId();
        if (userID != null) {
            loginItem.setVisible(false);
            logoutItem.setVisible(true);
            myProfileItem.setVisible(true);
            myOrdersItem.setVisible(true);
            mrResItem.setVisible(true);
            myReviewsItem.setVisible(true);
            myFavItem.setVisible(true);
            //navigationView.inflateHeaderView(R.layout.nav_header_login);

            DatabaseReference userRef = firebase.getReference("users/" + userID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
                    TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
                    ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPicture);
                    User target = dataSnapshot.getValue(it.polito.group2.restaurantowner.firebasedata.User.class);

                    if (target.getOwnerUser())
                        ownerItem.setVisible(true);

                    nav_username.setText(target.getUser_full_name());
                    nav_email.setText(target.getUser_email());

                    String photoUri = target.getUser_photo_firebase_URL();
                    if(photoUri == null || photoUri.equals("")) {
                        Glide
                                .with(getApplicationContext())
                                .load(R.drawable.blank_profile_nav)
                                .centerCrop()
                                .into(nav_picture);
                    }
                    else{
                        Glide
                                .with(getApplicationContext())
                                .load(photoUri)
                                .centerCrop()
                                .into(nav_picture);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("prova", "cancelled");
                }
            });

        }
        else{
            loginItem.setVisible(true);
            logoutItem.setVisible(false);
            myProfileItem.setVisible(false);
            myOrdersItem.setVisible(false);
            mrResItem.setVisible(false);
            myReviewsItem.setVisible(false);
            myFavItem.setVisible(false);

        }

        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return DrawerUtil.drawer_user_not_restaurant_page(this, item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            OnBackUtil.clean_stack_and_exit_application(this, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        if(!haveNetworkConnection())
            create_dialog(this);
    }

    public void settingsrequest(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        isRequestedPermission = true;
                        //I have already the GPS activated
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //GPS permission is requested
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            isRequestedPermission = true;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(UserRestaurantList.this, REQUEST_CHECK_SETTINGS);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ACTION_FILTER) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String cat = data.getExtras().getString("Category");
                boolean lunch = data.getExtras().getBoolean("Lunch");
                boolean dinner = data.getExtras().getBoolean("Dinner");
                boolean price1 = data.getExtras().getBoolean("OneEuro");
                boolean price2 = data.getExtras().getBoolean("TwoEuro");
                boolean price3 = data.getExtras().getBoolean("ThreeEuro");
                boolean price4 = data.getExtras().getBoolean("FourEuro");
                double range = data.getExtras().getDouble("range", DEFAULT_RANGE);
                this.range = range;
                mClusterManager.clearItems();
                filter(cat, lunch, dinner, price1, price2, price3, price4, mLastUserMarker, range);
            }
        }
        if(requestCode == ACTION_SEARCH){
            if(resultCode == RESULT_OK){
                String searchedText = data.getExtras().getString("searchedText");
                search.setText(searchedText);

                final ArrayList<String> restaurantIDs = data.getExtras().getStringArrayList("restaurant_list");
                LatLng coordinates = (LatLng) data.getExtras().get("coordinates");

                if(restaurantIDs != null && restaurantIDs.size() > 0) {
                    restaurants_previews_list = new ArrayList<>();
                    mAdapter.clear();

                    FirebaseUtil.showProgressDialog(mProgressDialog);
                    for(int i = 0; i < restaurantIDs.size(); i++) {
                        Log.d("prova", restaurantIDs.get(i));
                        final int index = i;
                        DatabaseReference restaurantRef = firebase.getReference("restaurants_previews/" + restaurantIDs.get(i));
                        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                RestaurantPreview restaurantPreview = dataSnapshot.getValue(RestaurantPreview.class);
                                if(restaurantPreview != null) {
                                    mAdapter.addItem(restaurantPreview);
                                    restaurants_previews_list.add(restaurantPreview);
                                }

                                if(index == restaurantIDs.size() - 1)
                                    FirebaseUtil.hideProgressDialog(mProgressDialog);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                FirebaseUtil.hideProgressDialog(mProgressDialog);
                            }
                        });
                    }
                }

                if(coordinates != null){
                    Log.d("prova", "coordinates");
                    searchedPosition = coordinates;
                    read_restaurants_from_firebase(false);
                }

            }
        }

        if(requestCode == REQUEST_CHECK_SETTINGS){
            if(resultCode == RESULT_OK){
                mRequestingLocationUpdates = true;
                fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantList.this, R.drawable.ic_my_location_on));
                startLocationUpdates();
            }
            else {
                //disable gps
                mRequestingLocationUpdates = false;
                stopLocationUpdates();
                fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantList.this, R.drawable.ic_my_location_24dp));
                read_restaurants_from_firebase(false);
            }
        }
    }

    protected void filter(final String category, final boolean lunch, final boolean dinner, final boolean price1, final boolean price2, final boolean price3, final boolean price4, final Marker marker, final double range) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        //filter by time
        final Calendar today = Calendar.getInstance();
        //restaurants_previews_list = mAdapter.getPreviews();
        mAdapter.clear();
        mClusterManager.clearItems();
        for (index = 0; index < restaurants_previews_list.size(); index++) {
            DatabaseReference resPreviewRef = firebase.getReference("restaurants/" + restaurants_previews_list.get(index).getRestaurant_id() + "/restaurant_time_slot");
            resPreviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String resId = snapshot.getRef().getParent().getKey();
                    RestaurantPreview rp = getRestaurantPreviewFromId(resId);
                    for (DataSnapshot snap_slot : snapshot.getChildren()) {
                        RestaurantTimeSlot timeSlot = snap_slot.getValue(RestaurantTimeSlot.class);
                        if (timeSlot.getDay_of_week() == today.get(Calendar.DAY_OF_WEEK) - 2) {
                            boolean addRes = false;
                            if (lunch && timeSlot.getLunch())
                                addRes = true;
                            if (dinner && timeSlot.getDinner())
                                addRes = true;
                            if (addRes) {
                                if (category.equals("0") || rp.getRestaurant_category().equals(category)){
                                    if (price1 && rp.getRestaurant_price_range() == 1 ||
                                            price2 && rp.getRestaurant_price_range() == 2 ||
                                            price3 && rp.getRestaurant_price_range() == 3 ||
                                            price4 && rp.getRestaurant_price_range() == 4 )
                                        if (is_restaurant_near(new LatLng(rp.getPosition().latitude, rp.getPosition().longitude), mLastUserMarker, range)) {
                                            mClusterManager.addItem(new MyItem(rp.getLat(), rp.getLon()));
                                            mClusterManager.cluster();
                                            mAdapter.addItem(rp);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                            }
                        }
                    }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });
        }
    }

    public boolean is_restaurant_near(LatLng res_position, Marker mLastUserMarker, double range) {
        double distance = calculate_distance2(res_position, mLastUserMarker.getPosition());
        if (distance < range) {
            return true;
        } else
            return false;
    }

    public RestaurantPreview getRestaurantPreviewFromId(String resId) {
        for(RestaurantPreview rp : restaurants_previews_list){
            if(rp.getRestaurant_id().equals(resId))
                return rp;
        }
        return null;
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), DEFAULT_ZOOM1));

                if(mLastUserMarker == null) {
                    mLastUserMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                                    .title("Your position")
                                            //.snippet("Population: 2,074,200")
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_navigation_arrow))
                                    .visible(false)
                    );
                    mAdapter.setLastUserMarker(mLastUserMarker);
                }
                else {
                    mLastUserMarker.setPosition(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                    mAdapter.setLastUserMarker(mLastUserMarker);
                }
            }
            if(range!=0 && mLastUserMarker!=null){
                //add circle
                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(mLastUserMarker.getPosition().latitude, mLastUserMarker.getPosition().longitude))
                        .radius(range)
                        .strokeWidth(5)
                        .strokeColor(Color.BLACK));
                //.fillColor(Color.BLUE)

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!haveNetworkConnection()) {
            create_dialog(this);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
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

    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

}
