package it.polito.group2.restaurantowner.user.restaurant_page;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import it.polito.group2.restaurantowner.R;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.Restaurant;
import it.polito.group2.restaurantowner.data.User;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.owner.RecyclerItemClickListener;
import it.polito.group2.restaurantowner.user.my_orders.MyOrdersActivity;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewsActivity;

public class UserRestaurantList extends AppCompatActivity
//      FirebaseLoginBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
/*        , OnMapReadyCallback, GeoQueryEventListener
*/{

    GoogleMap map;
    final static int ACTION_FILTER = 1;
    private UserRestaurantPreviewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    ArrayList<Restaurant> resList = new ArrayList<>();
    private static final int VERTICAL_ITEM_SPACE = 5;
    private GoogleApiClient mGoogleApiClient;
    final static int LOCATION_REQUEST = 4;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    HashMap<String,GeoLocation> resNearby = new HashMap<>();
    private String user_id;
    private ArrayList<User> users = new ArrayList<User>();
    private Context context;
    public User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_restaurant_list);

        //get data
        Intent intent = getIntent();
        if(intent.getExtras()!=null && intent.getExtras().get("user_id")!=null)
            user_id = (String) intent.getExtras().get("user_id");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*        Firebase.setAndroidContext(this);
        geoFire = new GeoFire(new Firebase("https://flickering-fire-455.firebaseio.com/"));
*/
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // GoogleMapOptions options = new GoogleMapOptions().liteMode(true);
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        ////SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        // mapFragment.getMapAsync(this);

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
                        String id = resList.get(position).getRestaurantId();
                        mIntent.putExtra("restaurant_id", id);
                        mIntent.putExtra("user_id", id);
                        startActivity(mIntent);
                    }
                })
        );

        mAdapter = new UserRestaurantPreviewAdapter(getData(), this);
        mRecyclerView.setAdapter(mAdapter);

        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //TODO decomment handle logged/not logged user
        /*
        if(user_id==null){ //not logged
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_my_profile).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_orders).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_reservations).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_reviews).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_favorites).setVisible(false);
        }
        else{ //logged
            //if user is logged does not need to logout for any reason; he could authenticate with another user so Login is still maintained
        }
        */
        //TODO Rearrange the following code
        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("user_id")!=null) {
            user_id = getIntent().getExtras().getString("user_id");
            try {
                users = JSONUtil.readJSONUsersList(context, null);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            for(User u : users){
                if(u.getId().equals(user_id)){
                    current_user = u;
                    break;
                }
            }
        }
        else{
            current_user = new User();
            current_user.setEmail("jkjs@dskj");
            //current_user.setFidelity_points(110);
            current_user.setFirst_name("Alex");
            current_user.setIsOwner(true);
            current_user.setPassword("tipiacerebbe");
            current_user.setPhone_number("0989897879789");
            current_user.setVat_number("sw8d9wd8w9d8w9d9");
        }
        TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
        TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
        ImageView nav_photo = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        if(current_user!=null) {
            if (current_user.getFirst_name() != null && current_user.getLast_name() == null)
                nav_username.setText(current_user.getFirst_name());
            else if (current_user.getFirst_name() == null && current_user.getLast_name() != null)
                nav_username.setText(current_user.getLast_name());
            else if (current_user.getFirst_name() != null && current_user.getLast_name() != null)
                nav_username.setText(current_user.getFirst_name() + " " + current_user.getLast_name());
            if (current_user.getEmail() != null)
                nav_email.setText(current_user.getEmail());
            if (current_user.getPhoto() != null)
                nav_photo.setImageBitmap(current_user.getPhoto());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        // All providers are optional! Remove any you don't want.
/*        setEnabledAuthProvider(AuthProviderType.FACEBOOK);
        setEnabledAuthProvider(AuthProviderType.GOOGLE);
        setEnabledAuthProvider(AuthProviderType.PASSWORD);
*/    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
/*        if(geoQuery!=null)
            geoQuery.removeAllListeners();
*/    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_restaurant_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {

            case R.id.action_filter:
                Intent intent = new Intent(this, Filter.class);
                startActivityForResult(intent, ACTION_FILTER);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    /*    @Override
        protected Firebase getFirebaseRef() {
            Firebase rootRef = new Firebase("https://flickering-fire-455.firebaseio.com/my/data");
            // TODO: Return your Firebase ref
            return rootRef;
        }

        @Override
        protected void onFirebaseLoginProviderError(FirebaseLoginError firebaseLoginError) {
            // TODO: Handle an error from the authentication provider
        }

        @Override
        protected void onFirebaseLoginUserError(FirebaseLoginError firebaseLoginError) {
            // TODO: Handle an error from the user
        }

        @Override
        public void onFirebaseLoggedIn(AuthData authData) {
            // TODO: Handle successful login displaying user info in the drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            TextView username = (TextView) drawer.findViewById(R.id.navHeaderUsername);
            TextView email = (TextView) drawer.findViewById(R.id.navHeaderEmail);
            username.setText(authData.getUid());

        }

        @Override
        public void onFirebaseLoggedOut() {
            // TODO: Handle logout
        }

    */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ACTION_FILTER) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                mAdapter = new UserRestaurantPreviewAdapter(resList, this);
                mRecyclerView.setAdapter(mAdapter);
                String cat = (String) data.getExtras().get("Category");
                String time = (String) data.getExtras().get("Time");
                boolean price1 = (boolean) data.getExtras().get("OneEuro");
                boolean price2 = (boolean) data.getExtras().get("TwoEuro");
                boolean price3 = (boolean) data.getExtras().get("ThreeEuro");
                boolean price4 = (boolean) data.getExtras().get("FourEuro");
                mAdapter.filter(cat,time, price1, price2, price3, price4);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(id==R.id.nav_owner){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MainActivity.class);
            Bundle b1 = new Bundle();
            b1.putString("user_id", user_id);
            intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        }
        else if(id==R.id.nav_home){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantList.class);
                Bundle b1 = new Bundle();
                b1.putString("user_id", user_id);
                intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        }
        else if(id==R.id.nav_login){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantList.class);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_profile) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserProfile.class);
                Bundle b1 = new Bundle();
                b1.putString("user_id", user_id);
                intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_orders) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MyOrdersActivity.class);
                Bundle b1 = new Bundle();
                b1.putString("user_id", user_id);
                intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_reservations){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    UserMyReservations.class);
            Bundle b3 = new Bundle();
                b3.putString("user_id", user_id);
                intent3.putExtras(b3);
                startActivity(intent3);
            return true;
        } else if(id==R.id.nav_my_reviews){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    MyReviewsActivity.class);
            Bundle b3 = new Bundle();
                b3.putString("user_id", user_id);
                intent3.putExtras(b3);
                startActivity(intent3);
            return true;
        } else if(id==R.id.nav_my_favourites){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    UserMyFavourites.class);
            Bundle b3 = new Bundle();
                b3.putString("user_id", user_id);
                intent3.putExtras(b3);
                startActivity(intent3);
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public ArrayList<Restaurant> getData() {

        try {
            resList = JSONUtil.readJSONResList(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resList;
    }

    /*    @Override
        public void onMapReady(GoogleMap googleMap) {
            Set<String> keySet = resNearby.keySet();
            for(String key : keySet){
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(resNearby.get(key).latitude, resNearby.get(key).longitude))
                        .title(key));
            }
        }
    */
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUEST);
            return;
        }
        else {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                //TODO order list based on current position or make query to firebase
/*                geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 0.6);
                geoQuery.addGeoQueryEventListener(this);
*/            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //TODO
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Connection failed, retry again.")
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /*    @Override
        public void onKeyEntered(String key, GeoLocation location) {
            //TODO search for the restaurant
            resNearby.put(key,location);

        }

        @Override
        public void onKeyExited(String key) {
            //TODO remove restaurant from the adapter
            resNearby.remove(key);
        }

        @Override
        public void onKeyMoved(String key, GeoLocation location) {

        }

        @Override
        public void onGeoQueryReady() {
            //TODO query the Firebase database with resNearby
        }

        @Override
        public void onGeoQueryError(FirebaseError error) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("There was an unexpected error querying GeoFire: " + error.getMessage())
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
    */
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
}
