package it.polito.group2.restaurantowner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Restaurant_page extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Menu menu;
    private RecyclerView rv;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean card_view_clicked=false;
    public ArrayList<Restaurant> resList = new ArrayList<>(); //to be consistent with Daniel's code
    public String restaurant_id = null; //if not passed by the previous activity
    public int PICK_IMAGE = 0;
    public int REQUEST_TAKE_PHOTO = 1;
    public int MODIFY_INFO = 4;
    public String photouri = null;
    public ArrayList<Comment> comments = new ArrayList<>();
    public Restaurant my_restaurant = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_page);

        //get the right restaurant
        Bundle b = getIntent().getExtras();
        if(b!=null)
            restaurant_id = b.getString("RestaurantId");
        //get data
        try {
            resList = readJSONResList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addComments(restaurant_id);
        try {
            saveJSONComList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            comments = readJSONComList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("ccc", "Given " + restaurant_id);
        for (Restaurant r : resList) {
            Log.d("ccc", "Found "+r.getRestaurantId());
            if (r.getRestaurantId().equals(restaurant_id)) {
                Log.d("ccc", "Corresponding "+r.getRestaurantId());
                my_restaurant = r;
                break;
            }
        }
        //fill data
        //setTitle(my_restaurant.getName());
        /*
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        if(userDetails != null) {
            ImageView image = (ImageView) findViewById(R.id.image_to_enlarge);
            if (userDetails.getString(restaurant_id, null) != null) {
                Uri photouri = Uri.parse(userDetails.getString(restaurant_id, null));
                if (photouri != null)
                    image.setImageURI(photouri);
            }
        }
        */
        ImageView image = (ImageView) findViewById(R.id.image_to_enlarge);
        image.setImageURI(Uri.parse(my_restaurant.getPhotoUri()));

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        EditText edit_restaurant_name = (EditText) findViewById(R.id.edit_restaurant_name);
        EditText edit_restaurant_address = (EditText) findViewById(R.id.edit_restaurant_address);
        EditText edit_restaurant_telephone_number = (EditText) findViewById(R.id.edit_restaurant_telephone_number);
        if(my_restaurant !=null && my_restaurant.getName()!=null)
            edit_restaurant_name.setText(my_restaurant.getName());
        if(my_restaurant !=null && my_restaurant.getAddress()!=null)
            edit_restaurant_address.setText(my_restaurant.getAddress());
        if(my_restaurant !=null && my_restaurant.getPhoneNum()!=null)
            edit_restaurant_telephone_number.setText(my_restaurant.getPhoneNum());

        //navigation drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //prepare enlarged image option
        ImageView imageview = (ImageView) findViewById(R.id.image_to_enlarge);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
                if(userDetails != null) {
                    String parameter = userDetails.getString(restaurant_id, null);
                    if(parameter!=null){
                        Intent intent = new Intent(
                                getApplicationContext(),
                                Enlarged_image.class);
                        Bundle b = new Bundle();
                        b.putString("photouri", parameter);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                }
            }
        });

        //cardview implementation
        rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        initializeAdapterComments();
        final AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        NestedScrollView scroll = (NestedScrollView) findViewById(R.id.parent_scroll);
        scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                appbar.setExpanded(false);
            }
        });

        //buttons listeners implementation
        //take a photo
        Button button1 = (Button) findViewById(R.id.button_take_photo);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        //choose a photo
        Button button2 = (Button) findViewById(R.id.button_choose_photo);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("photouri", photouri);
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if(state!=null) {
            if(state.getString("photouri")!=null) {
                ImageView image = (ImageView) findViewById(R.id.image_to_enlarge);
                image.setImageURI(Uri.parse(state.getString("photouri")));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        try {
            saveJSONResList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        try {
            readJSONResList();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    */

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
    public boolean onCreateOptionsMenu(Menu menu) {
    //    getMenuInflater().inflate(R.menu.main_restaurant, menu);
    //     this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();
        if(id == R.id.action_menu) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MenuRestaurant_page.class);
            Bundle b = new Bundle();
            b.putString("restaurant_id", restaurant_id);
            intent1.putExtras(b);
            startActivity(intent1);
            return true;
        } else if(id==R.id.action_offers) {
            Intent intent2 = new Intent(
                    getApplicationContext(),
                    OfferList.class);
            Bundle b2 = new Bundle();
            b2.putString("restaurant_id", restaurant_id);
            intent2.putExtras(b2);
            startActivity(intent2);
            return true;
        } else if(id==R.id.action_reservations){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    Reservation.class);
            Bundle b3 = new Bundle();
            b3.putString("restaurant_id", restaurant_id);
            intent3.putExtras(b3);
            startActivity(intent3);
            return true;
        } else if(id==R.id.action_reviews){
            Intent intent4 = new Intent(
                    getApplicationContext(),
                    ReviewsActivity.class); //here Filippo must insert his class name
            Bundle b4 = new Bundle();
            b4.putString("restaurant_id", restaurant_id);
            intent4.putExtras(b4);
            startActivity(intent4);
            return true;
        } else if(id==R.id.action_statistics){
            Intent intent5 = new Intent(
                    getApplicationContext(),
                    StatisticsActivity.class); //here Filippo must insert his class name
            Bundle b5 = new Bundle();
            b5.putString("restaurant_id", restaurant_id);
            intent5.putExtras(b5);
            startActivity(intent5);
            return true;
        } else if(id==R.id.action_edit){
            Intent intent6 = new Intent(
                    getApplicationContext(),
                    AddRestaurantActivity.class);
            intent6.putExtra("Restaurant", my_restaurant);
            final AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
            appbar.setExpanded(false);
            startActivityForResult(intent6, MODIFY_INFO);
            return true;
        } else if(id==R.id.action_edit_cover) {
            drawer.closeDrawer(GravityCompat.START);
            show();
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //take a photo result
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //view photo
            Log.d("aaa", "BREAK2");
            ImageView image = (ImageView) findViewById(R.id.image_to_enlarge);
            setPic();
            //add photo to gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            if(photouri!=null) {
                File f = new File(photouri); //here is passed the mCurrentPhotoPath
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
                //photouri = contentUri.toString();
                Log.d("aaa", "BREAK3"+contentUri.toString());
                Log.d("aaa", "BREAK4" + Uri.parse(photouri));
                image.setImageURI(Uri.parse(photouri));
                my_restaurant.setPhotoUri(photouri); // ***MAYBE***
            }
            try {
                saveJSONResList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //choose a photo result
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            photouri = imageUri.toString();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                ImageView image_to_enlarge = (ImageView) findViewById(R.id.image_to_enlarge);
                image_to_enlarge.setImageBitmap(BitmapFactory.decodeStream(imageStream));
            } catch (FileNotFoundException e) {
                // Handle the error
            } finally {
                if (imageStream != null) {
                    try {
                        imageStream.close();
                    } catch (IOException e) {
                        // Ignore the exception
                    }
                }
            }
            if(my_restaurant == null)
                Log.d("aaa", "MY RESTAURANT IS NULL");
            my_restaurant.setPhotoUri(photouri);
            try {
                saveJSONResList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*
            SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
            SharedPreferences.Editor edit = userDetails.edit();
            edit.putString(restaurant_id, photouri);
            //I can not save the photo, but i could save its URI
            edit.commit();
            */
        }
        if (requestCode == MODIFY_INFO) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Restaurant res = (Restaurant) data.getExtras().get("Restaurant");
                my_restaurant = res;
                try {
                    ArrayList<Restaurant> resList = JSONUtil.readJSONResList(this);
                    for(Restaurant restaurant : resList){
                        if(restaurant.getRestaurantId().equals(res.getRestaurantId())){
                            resList.set(resList.indexOf(restaurant),res);
                        }
                    }
                    JSONUtil.saveJSONResList(this, resList);
                    Intent intent = getIntent();
                    intent.putExtra("RestaurantId", res.getRestaurantId());
                    finish();
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        /*
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.putString(restaurant_id, photouri);
        //I can not save the photo, but i could save its URI
        edit.commit();
        */
        try{
            saveJSONResList();
        }
        catch(JSONException e){
        }
        hide();
    }

    /*
    private void initializeComments(){
        comments = new ArrayList<>();
        comments.add(new Comment("0", "Turi Lecce", "01/02/2003", 1, "@mipmap/ic_launcher", "Ah chi ni sacciu mba")); //or int photoId R.mipmap.ic_launcher
        comments.add(new Comment("0", "Iaffiu u cuttu", "11/06/2002", 2.7, "@mipmap/money_icon", "Cosa assai"));
        comments.add(new Comment("0", "Iano Papale", "21/12/2001", 5, "@mipmap/image_preview_black", "Fussi pi mia ci tunnassi, ma appi problemi cu me soggira ca ogni bota si lassa curriri de scali, iu no sacciu va."));
        comments.add(new Comment("0", "Tano Sghei", "22/05/2000", 3.4, "@mipmap/image_preview_black", "M'uccullassi n'autra vota. Turi ci emu?"));
    }
    */

    private void initializeAdapterComments(){
        Adapter_Comments adapter = new Adapter_Comments(comments, this.getApplicationContext());
        rv.setAdapter(adapter);
    }

    public void myClickHandler_expand_comment(View v){
        final View default_view = v;
        TextView comment = (TextView) v.findViewById(R.id.comment);
        final int original_comment_height=comment.getMeasuredHeight();
        int i;
        String comment_start = comment.getText().toString().substring(0, 7);
        for(i=0; i<comments.size(); i++){
            if(comment_start.equals(comments.get(i).comment.substring(0, 7)))
                break;
        }
        if(!card_view_clicked) {
            card_view_clicked=true;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 300, 1f);
            comment.setMaxLines(Integer.MAX_VALUE);
            comment.setText(comments.get(i).comment);
            comment.setLayoutParams(lp);
        }
        else {
            card_view_clicked=false;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, v.getLayoutParams().height, 1f);
            String comment_ell = comments.get(i).comment.substring(0, 7);
            comment_ell = comment_ell + getResources().getString(R.string.show_more);
            comment.setText(comment_ell);
            comment.setMaxLines(2);
            comment.setLayoutParams(lp);
        }
        v.requestLayout();
    }

    public void show(){
        //button present yes/not
        Button button_take_photo1 = (Button) findViewById(R.id.button_take_photo);
        button_take_photo1.setVisibility(View.VISIBLE);
        button_take_photo1.requestFocus();
        Button button_choose_photo1 = (Button) findViewById(R.id.button_choose_photo);
        button_choose_photo1.setVisibility(View.VISIBLE);
        button_choose_photo1.requestFocus();
    }

    public void hide(){
        //button present yes/not
        Button button_take_photo2 = (Button) findViewById(R.id.button_take_photo);
        button_take_photo2.setVisibility(View.GONE);
        Button button_choose_photo2 = (Button) findViewById(R.id.button_choose_photo);
        button_choose_photo2.setVisibility(View.GONE);
    }

    public ArrayList<Restaurant> readJSONResList() throws JSONException {
        resList = new ArrayList<>();
        String json = null;
        FileInputStream fis = null;
        String FILENAME = "restaurantList.json";
        try {
            fis = openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return resList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Restaurants");
        //Iterate the jsonArray and print the info of JSONObjects
        for(int i=0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Restaurant res = new Restaurant();
            res.setRestaurantId(jsonObject.optString("RestaurantId"));
            res.setPhotoUri(jsonObject.optString("Photo"));
            res.setAddress(jsonObject.optString("Address"));
            res.setCategory(jsonObject.optString("Category"));
            res.setClosestBus(jsonObject.optString("ClosestBus"));
            res.setClosestMetro(jsonObject.optString("ClosestMetro"));
            res.setFidelity(jsonObject.getBoolean("Fidelity"));
            res.setName(jsonObject.optString("Name"));
            res.setOrdersPerHour(jsonObject.optString("OrdersPerHour"));
            res.setPhoneNum(jsonObject.optString("PhoneNum"));
            res.setRating(jsonObject.optString("Rating"));
            res.setReservationNumber(jsonObject.optString("ReservationNumber"));
            res.setReservedPercentage(jsonObject.optString("ReservationPercentage"));
            res.setSquaredMeters(jsonObject.optString("SquaredMeters"));
            res.setTableReservation(jsonObject.getBoolean("TableReservation"));
            res.setTableNum(jsonObject.optString("TableNum"));
            res.setTakeAway(jsonObject.getBoolean("TakeAway"));
            res.setUserId(jsonObject.optString("UserId"));
            resList.add(res);
        }
        return resList;
    }

    public ArrayList<Comment> readJSONComList() throws JSONException {
        comments = new ArrayList<>();
        String json = null;
        FileInputStream fis = null;
        String FILENAME = "commentList.json";
        try {
            fis = openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return comments;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Comments"); //root tag?
        //Iterate the jsonArray and print the info of JSONObjects
        for(int i=0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Comment com = new Comment();
            if(jsonObject.optString("RestaurantId").equals(restaurant_id)){ //I read only the comments of my restaurant
                com.setRestaurantId(jsonObject.optString("RestaurantId")); //optInt or optString?
                com.setDate(jsonObject.optString("Date"));
                com.setStars_number(jsonObject.optInt("StarsNumber")); //optInt or optString?
                com.setComment(jsonObject.optString("Comment"));
                com.setPhotoId(jsonObject.optString("UserPhoto"));
                com.setUsername(jsonObject.optString("Username"));
                comments.add(com);
            }
        }
        return comments;
    }

    public void saveJSONResList() throws JSONException {
        String FILENAME = "restaurantList.json";
        JSONArray jarray = new JSONArray();
        for(Restaurant res : resList){
            JSONObject jres = new JSONObject();
            jres.put("RestaurantId",res.getRestaurantId());
            jres.put("Photo",res.getPhotoUri());
            jres.put("Address",res.getAddress());
            jres.put("Category",res.getCategory());
            jres.put("ClosestBus",res.getClosestBus());
            jres.put("ClosestMetro",res.getClosestMetro());
            jres.put("Fidelity",res.isFidelity());
            jres.put("Name",res.getName());
            jres.put("OrdersPerHour",res.getOrdersPerHour());
            jres.put("PhoneNum",res.getPhoneNum());
            jres.put("Rating",res.getRating());
            jres.put("ReservationNumber",res.getReservationNumber());
            jres.put("ReservationPercentage",res.getReservedPercentage());
            jres.put("SquaredMeters",res.getSquaredMeters());
            jres.put("TableReservation",res.isTableReservation());
            jres.put("TableNum",res.getTableNum());
            jres.put("TakeAway",res.isTakeAway());
            jres.put("UserId",res.getUserId());
            jarray.put(jres);
        }
        JSONObject resObj = new JSONObject();
        resObj.put("Restaurants", jarray);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(resObj.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveJSONComList() throws JSONException {
        String FILENAME = "commentList.json";
        JSONArray jarray = new JSONArray();
        for(Comment com : comments){
            JSONObject jres = new JSONObject();
            jres.put("RestaurantId",com.getRestaurantId());
            jres.put("Date",com.getDate());
            jres.put("StarsNumber",com.getStars_number());
            jres.put("Comment",com.getComment());
            jres.put("UserPhoto",com.userphoto());
            jres.put("Username",com.getUsername());
            jarray.put(jres);
        }
        JSONObject resObj = new JSONObject();
        resObj.put("Comments", jarray);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(resObj.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addComments(String restaurant_id){
        Comment c1 = new Comment();
        c1.setUsername("Salvatore Grasso");
        c1.setRestaurantId(restaurant_id);
        c1.setComment("Mi è piaciuto tanto");
        c1.setPhotoId(getResources().getResourceName(R.mipmap.ic_launcher));
        c1.setStars_number(4);
        comments.add(c1);

        Comment c2 = new Comment();
        c2.setUsername("Karl");
        c2.setRestaurantId(restaurant_id);
        c2.setComment("Non ho visto di meglio.............................................");
        c2.setPhotoId(getResources().getResourceName(R.mipmap.ic_launcher));
        c2.setStars_number(5);
        comments.add(c2);

        Comment c3 = new Comment();
        c3.setUsername("Angelo Spada");
        c3.setRestaurantId(restaurant_id);
        c3.setComment("Non siti cosa");
        c3.setPhotoId(getResources().getResourceName(R.mipmap.ic_launcher));
        c3.setStars_number(1);
        comments.add(c3);

        Comment c4 = new Comment();
        c4.setUsername("Pina");
        c4.setRestaurantId(restaurant_id);
        c4.setComment("Pessimo");
        c4.setPhotoId(getResources().getResourceName(R.mipmap.ic_launcher));
        c4.setStars_number(0);
        comments.add(c4);
    }

    public void addRestaurants(){
        Restaurant r1 = new Restaurant();
        r1.setName("TRATTORIA NDI IAFFIU");
        r1.setAddress(("Sciara curia 7"));
        r1.setPhoneNum("095393930");
        r1.setRestaurantId(restaurant_id);
        resList.add(r1);

        Restaurant r2 = new Restaurant();
        r2.setName("BELLA ITALIA");
        r2.setAddress(("Magellano 38"));
        r2.setPhoneNum("+44099495930");
        r2.setRestaurantId("1");
        resList.add(r2);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); //import util or sql?
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        photouri = "file:" + image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("aaa", "BREAK1");
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void setPic() {
        ImageView mImageView = (ImageView) findViewById(R.id.image_to_enlarge);
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photouri, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = 1;
        if(targetW!=0 && targetH!=0) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photouri, bmOptions);
        if(bitmap!=null)
            mImageView.setImageBitmap(bitmap);
        /*
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.putString("photouri", photouri);
        //I can not save the photo, but i could save its URI
        edit.commit();
        */
    }


}
