package it.polito.group2.restaurantowner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
    public int restaurant_id = 0; //if not passed by the previous activity
    public int PICK_IMAGE = 0;
    public int REQUEST_TAKE_PHOTO = 1;
    public String photouri;
    public ArrayList<Comment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_page);

        //read all data and fill resList
        try {
            resList = readJSONResList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            comments = readJSONComList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //or initializeComments();
        //get the right restaurant
        Bundle b = getIntent().getExtras();
        if(b!=null)
            restaurant_id = b.getInt("restaurant_id");
        //fill its data
        Restaurant my_restaurant = null;
        try { //if there are data to read
            my_restaurant = resList.get(restaurant_id);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        EditText edit_restaurant_name = (EditText) findViewById(R.id.edit_restaurant_name);
        ImageView image = (ImageView) findViewById(R.id.image_to_enlarge);
        EditText edit_restaurant_address = (EditText) findViewById(R.id.edit_restaurant_address);
        EditText edit_restaurant_telephone_number = (EditText) findViewById(R.id.edit_restaurant_telephone_number);
        if(my_restaurant !=null && my_restaurant.getName()!=null)
            edit_restaurant_name.setText(my_restaurant.getName());
        if(my_restaurant !=null && my_restaurant.getPhotoUri()!=null)
            image.setImageURI(Uri.parse(my_restaurant.getPhotoUri()));
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
                try {
                    resList = readJSONResList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent fullScreenIntent = new Intent(getApplicationContext(), Enlarged_image.class);
                fullScreenIntent.putExtra(Enlarged_image.class.getName(), resList.get(restaurant_id).getPhotoUri());
                startActivity(fullScreenIntent);
            }
        });

        //cardview implementation
        rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        initializeAdapterComments();
        final AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        ScrollView scroll = (ScrollView) findViewById(R.id.parent_scroll);
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
        EditText edit_restaurant_name = (EditText) findViewById(R.id.edit_restaurant_name);
        EditText edit_restaurant_address = (EditText) findViewById(R.id.edit_restaurant_address);
        EditText edit_restaurant_telephone_number = (EditText) findViewById(R.id.edit_restaurant_telephone_number);
        state.putString("restaurant_name", edit_restaurant_name.getText().toString());
        state.putString("restaurant_address", edit_restaurant_address.getText().toString());
        state.putString("restaurant_telephone_number", edit_restaurant_telephone_number.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if(state!=null) {
            EditText edit_restaurant_name = (EditText) findViewById(R.id.edit_restaurant_name);
            EditText edit_restaurant_address = (EditText) findViewById(R.id.edit_restaurant_address);
            EditText edit_restaurant_telephone_number = (EditText) findViewById(R.id.edit_restaurant_telephone_number);
            edit_restaurant_name.setText(state.getString("restaurant_name"));
            edit_restaurant_address.setText(state.getString("restaurant_address"));
            edit_restaurant_telephone_number.setText(state.getString("restaurant_telephone_number"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        try {
            saveJSONResList(resList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        try {
            readJSONResList();
        } catch (JSONException e) {
            e.printStackTrace();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_restaurant, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_menu:
                Intent intent1 = new Intent(
                        getApplicationContext(),
                        MenuRestaurant_page.class);
                Bundle b = new Bundle();
                b.putInt("restaurant_id", restaurant_id);
                intent1.putExtras(b);
                startActivity(intent1);
                return true;

            case R.id.action_more:
                Intent intent2 = new Intent(
                        getApplicationContext(),
                        MoreRestaurantInfo.class); //actually this should redirect to Daniel's activity
                Bundle b2 = new Bundle();
                b2.putInt("restaurant_id", restaurant_id);
                intent2.putExtras(b2);
                startActivity(intent2);
                return true;

            case R.id.action_edit:
                show();
                return true;

            case R.id.action_confirm:
                Restaurant modified_restaurant = resList.get(restaurant_id);
                EditText edit_restaurant_name2 = (EditText) findViewById(R.id.edit_restaurant_name);
                EditText edit_restaurant_address2 = (EditText) findViewById(R.id.edit_restaurant_address);
                EditText edit_restaurant_telephone_number2 = (EditText) findViewById(R.id.edit_restaurant_telephone_number);
                modified_restaurant.setName(edit_restaurant_name2.getText().toString());
                modified_restaurant.setAddress(edit_restaurant_address2.getText().toString());
                modified_restaurant.setPhoneNum(edit_restaurant_telephone_number2.getText().toString());
                hide();
                try {
                    saveJSONResList(resList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            // TODO Handle the logout action
        } else if (id == R.id.nav_manage) {
            // TODO Handle the manage action
        } else if (id == R.id.one_restaurant) {
            Intent intent = new Intent(
                    getApplicationContext(),
                    Restaurant_page.class);
            Bundle b = new Bundle();
            b.putInt("restaurant_id", restaurant_id);
            intent.putExtras(b);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //take a photo result
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //view photo
            ImageView image = (ImageView) findViewById(R.id.image_to_enlarge);
            setPic(image);
            //add photo to gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(photouri); //here is passed the mCurrentPhotoPath
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            Restaurant modified_restaurant = resList.get(restaurant_id);
            modified_restaurant.setPhotoUri(contentUri.toString()); // ***MAYBE***
            try {
                saveJSONResList(resList);
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
            Restaurant modified_restaurant = resList.get(restaurant_id);
            modified_restaurant.setPhotoUri(photouri);
            try {
                saveJSONResList(resList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // This method creates an ArrayList that has three Comment objects
    // Checkout the project associated with this tutorial on Github if
    // you want to use the same images.
    private void initializeComments(){
        comments = new ArrayList<>();
        comments.add(new Comment("0", "Turi Lecce", "01/02/2003", 1, "@mipmap/ic_launcher", "Ah chi ni sacciu mba")); //or int photoId R.mipmap.ic_launcher
        comments.add(new Comment("0", "Iaffiu u cuttu", "11/06/2002", 2.7, "@mipmap/money_icon", "Cosa assai"));
        comments.add(new Comment("0", "Iano Papale", "21/12/2001", 5, "@mipmap/image_preview_black", "Fussi pi mia ci tunnassi, ma appi problemi cu me soggira ca ogni bota si lassa curriri de scali, iu no sacciu va."));
        comments.add(new Comment("0", "Tano Sghei", "22/05/2000", 3.4, "@mipmap/image_preview_black", "M'uccullassi n'autra vota. Turi ci emu?"));
    }

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
        //menu item show/hide
        MenuItem action_confirm_item1 = menu.findItem(R.id.action_confirm);
        action_confirm_item1.setVisible(true);
        MenuItem action_edit_item1 = menu.findItem(R.id.action_edit);
        action_edit_item1.setVisible(false);
        //edit text clickable yes/not
        EditText edit_restaurant_name1 = (EditText) findViewById(R.id.edit_restaurant_name);
        EditText edit_restaurant_address1 = (EditText) findViewById(R.id.edit_restaurant_address);
        EditText edit_restaurant_telephone_number1 = (EditText) findViewById(R.id.edit_restaurant_telephone_number);
        edit_restaurant_name1.setFocusableInTouchMode(true);
        edit_restaurant_name1.setFocusable(true);
        edit_restaurant_name1.setAlpha(1);
        edit_restaurant_address1.setFocusableInTouchMode(true);
        edit_restaurant_address1.setFocusable(true);
        edit_restaurant_address1.setAlpha(1);
        edit_restaurant_telephone_number1.setFocusableInTouchMode(true);
        edit_restaurant_telephone_number1.setFocusable(true);
        edit_restaurant_telephone_number1.setAlpha(1);
        //button present yes/not
        Button button_take_photo1 = (Button) findViewById(R.id.button_take_photo);
        button_take_photo1.setVisibility(View.VISIBLE);
        Button button_choose_photo1 = (Button) findViewById(R.id.button_choose_photo);
        button_choose_photo1.setVisibility(View.VISIBLE);
    }

    public void hide(){
        //menu item show/hide
        MenuItem action_confirm_item2 = menu.findItem(R.id.action_confirm);
        action_confirm_item2.setVisible(false);
        MenuItem action_edit_item2 = menu.findItem(R.id.action_edit);
        action_edit_item2.setVisible(true);
        //edit text clickable yes/not
        EditText edit_restaurant_name2 = (EditText) findViewById(R.id.edit_restaurant_name);
        EditText edit_restaurant_address2 = (EditText) findViewById(R.id.edit_restaurant_address);
        EditText edit_restaurant_telephone_number2 = (EditText) findViewById(R.id.edit_restaurant_telephone_number);
        edit_restaurant_name2.setFocusableInTouchMode(false);
        edit_restaurant_name2.setFocusable(false);
        edit_restaurant_name2.setAlpha(0);
        edit_restaurant_address2.setFocusableInTouchMode(false);
        edit_restaurant_address2.setFocusable(false);
        edit_restaurant_address2.setAlpha(0);
        edit_restaurant_telephone_number2.setFocusableInTouchMode(false);
        edit_restaurant_telephone_number2.setFocusable(false);
        edit_restaurant_telephone_number2.setAlpha(0);
        //button present yes/not
        Button button_take_photo2 = (Button) findViewById(R.id.button_take_photo);
        button_take_photo2.setVisibility(View.GONE);
        Button button_choose_photo2 = (Button) findViewById(R.id.button_choose_photo);
        button_choose_photo2.setVisibility(View.GONE);
    }

    public ArrayList<Restaurant> readJSONResList() throws JSONException {
        String json = null;
        resList = new ArrayList<>();
        addRestaurants(resList);
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
        String json = null;
        comments = new ArrayList<>();
        addComments(comments);
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
            if(jsonObject.optInt("RestaurantId")==restaurant_id){ //I read only the comments of my restaurant
                com.setRestaurantId(jsonObject.optString("RestaurantId")); //optInt or optString?
                com.setDate(jsonObject.optString("Date"));
                com.setStars_number(jsonObject.optInt("StarsNumber")); //optInt or optString?
                com.setComment(jsonObject.optString("Comment"));
                com.setPhotoId(jsonObject.optString("UserPhoto"));
                comments.add(com);
            }
        }
        return comments;
    }

    public void saveJSONResList(ArrayList<Restaurant> resList) throws JSONException {
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

    public void addComments(ArrayList<Comment> comments){
        Comment c = new Comment();
        c.setUsername("Turiddu");
        c.setRestaurantId("0");
        c.setComment("Bonu è sicunnu mia");
        c.setPhotoId(getResources().getResourceName(R.mipmap.ic_launcher));
        c.setStars_number(2);
        comments.add(c);
    }

    public void addRestaurants(ArrayList<Restaurant> resList){
        Restaurant r = new Restaurant();
        r.setName("TRATTORIA NDI IAFFIU");
        r.setAddress(("Sciara curia 7"));
        r.setPhoneNum("095393930");
        resList.add(r);
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
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                //nothing
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void setPic(ImageView mImageView) {
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
        mImageView.setImageBitmap(bitmap);
    }


}
