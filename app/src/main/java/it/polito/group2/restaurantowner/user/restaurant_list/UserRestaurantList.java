package it.polito.group2.restaurantowner.user.restaurant_list;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import it.polito.group2.restaurantowner.R;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.User;
import it.polito.group2.restaurantowner.firebasedata.Order;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.owner.RecyclerItemClickListener;
import it.polito.group2.restaurantowner.user.my_orders.MyOrdersActivity;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewsActivity;
import it.polito.group2.restaurantowner.user.restaurant_page.Filter;
import it.polito.group2.restaurantowner.user.restaurant_page.UserMyFavourites;
import it.polito.group2.restaurantowner.user.restaurant_page.UserMyReservations;
import it.polito.group2.restaurantowner.user.restaurant_page.UserProfile;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;

public class UserRestaurantList extends AppCompatActivity
//      DatabaseReferenceLoginBaseActivity
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
    /*private GeoFire geoFire;
    private GeoQuery geoQuery;
    HashMap<String,GeoLocation> resNearby = new HashMap<>();*/
    private String user_id;
    private ArrayList<User> users = new ArrayList<User>();
    private Context context;
    public User current_user;
    private Drawable d;
    private FirebaseDatabase firebase;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_restaurant_list);

        //get data
        Intent intent = getIntent();
        if(intent.getExtras()!=null && intent.getExtras().get("user_id")!=null)
            user_id = (String) intent.getExtras().get("user_id");

        showProgressDialog();
        firebase = FirebaseDatabase.getInstance();
        //if location is not active, otherwise should call GeoFire
        Query resturantReference = firebase.getReference("restaurants").orderByChild("rating");

        resturantReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                resList.add(dataSnapshot.getValue(Restaurant.class));
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Restaurant changedRes = dataSnapshot.getValue(Restaurant.class);
                for (Restaurant r : resList) {
                    if (r.getRestaurant_id().equals(changedRes.getRestaurant_id())) {
                        resList.remove(r);
                        resList.add(changedRes);
                        break;
                    }
                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Restaurant removedRes = dataSnapshot.getValue(Restaurant.class);
                for (Restaurant r : resList) {
                    if (r.getRestaurant_id().equals(removedRes.getRestaurant_id())) {
                        resList.remove(r);
                        break;
                    }
                }
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //TODO capire quando si verifica
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO capire quando si verifica
            }
        });
        hideProgressDialog();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView filter_icon = (ImageView) findViewById(R.id.icon_filter);
        assert filter_icon != null;
        filter_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserRestaurantList.this, Filter.class);
                startActivityForResult(intent, ACTION_FILTER);
            }
        });

        TextView search = (TextView) findViewById(R.id.search_text);
        assert search != null;
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserRestaurantList.this, SearchActivity.class);
                startActivityForResult(intent, ACTION_FILTER);
            }
        });

/*        DatabaseReference.setAndroidContext(this);
        geoFire = new GeoFire(new DatabaseReference("https://flickering-fire-455.firebaseio.com/"));
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
                        String id = resList.get(position).getRestaurant_id();
                        mIntent.putExtra("restaurant_id", id);
                        mIntent.putExtra("user_id", user_id);
                        startActivity(mIntent);
                    }
                })
        );

        mAdapter = new UserRestaurantPreviewAdapter(resList, this);
        mRecyclerView.setAdapter(mAdapter);

        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
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
                users = JSONUtil.readJSONUsersList(this, null);
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
        /*
        BorderedTextView nav_username = (BorderedTextView) nav_email_textview;
        BorderedTextView nav_email = (BorderedTextView) nav_email_textview;
        */
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
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        Uri photouri = null;
        if(userDetails.getString("photouri", null)!=null) {
            photouri = Uri.parse(userDetails.getString("photouri", null));
            File f = new File(getRealPathFromURI(photouri));
            d = Drawable.createFromPath(f.getAbsolutePath());
            navigationView.getHeaderView(0).setBackground(d);
        }
        else
            nav_photo.setImageResource(R.drawable.blank_profile);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        d = null;
        System.gc();
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
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


    /*@Override
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
    }*/


    /*    @Override
        protected DatabaseReference getDatabaseReferenceRef() {
            DatabaseReference rootRef = new DatabaseReference("https://flickering-fire-455.firebaseio.com/my/data");
            // TODO: Return your DatabaseReference ref
            return rootRef;
        }

        @Override
        protected void onDatabaseReferenceLoginProviderError(DatabaseReferenceLoginError firebaseLoginError) {
            // TODO: Handle an error from the authentication provider
        }

        @Override
        protected void onDatabaseReferenceLoginUserError(DatabaseReferenceLoginError firebaseLoginError) {
            // TODO: Handle an error from the user
        }

        @Override
        public void onDatabaseReferenceLoggedIn(AuthData authData) {
            // TODO: Handle successful login displaying user info in the drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            TextView username = (TextView) drawer.findViewById(R.id.navHeaderUsername);
            TextView email = (TextView) drawer.findViewById(R.id.navHeaderEmail);
            username.setText(authData.getUid());

        }

        @Override
        public void onDatabaseReferenceLoggedOut() {
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
            //TODO query the DatabaseReference database with resNearby
        }

        @Override
        public void onGeoQueryError(DatabaseError error) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("There was an unexpected error querying GeoFire: " + error.getMessage())
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
    */

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
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
}
