package it.polito.group2.restaurantowner.user.restaurant_page;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.login.LoginManagerActivity;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.user.my_orders.MyOrdersActivity;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewsActivity;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import junit.framework.TestResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String user_id;
    static final int PICK_IMAGE = 2;
    static final int REQUEST_TAKE_PHOTO = 3;
    public String photouri=null;
    private ProgressDialog progressDialog;
    private StorageReference user_storage_reference;
    private StorageReference user_photo_reference;
    private StorageReference user_photo_reference_thumbnail;  //mStorageRef was previously used to transfer data
    private Toolbar toolbar;
    private FirebaseDatabase firebase;
    private User current_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       firebase = FirebaseDatabase.getInstance();
        //get the right user
        user_id = FirebaseUtil.getCurrentUserId();
        if(user_id == null){
            Toast.makeText(UserProfile.this, "You need to be logged in.", Toast.LENGTH_SHORT).show();
            finish();
        }

        setDrawerAndGetUser();
		
    }

    private void setDrawerAndGetUser() {
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
                    User target = dataSnapshot.getValue(User.class);

                    current_user = target;

                    load_saved_data();

                    activate_buttons();

                    if (target.getOwnerUser())
                        ownerItem.setVisible(true);

                    nav_username.setText(target.getUser_full_name());
                    nav_email.setText(target.getUser_email());

                    String photoUri = target.getUser_photo_firebase_URL();
                    if(photoUri == null || photoUri.equals("")) {
                        Glide
                                .with(UserProfile.this)
                                .load(R.drawable.blank_profile_nav)
                                .centerCrop()
                                .into(nav_picture);
                    }
                    else{
                        Glide
                                .with(UserProfile.this)
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


    @Override
    public void onResume(){
        super.onResume();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(id==R.id.nav_owner){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        else if(id==R.id.nav_home){
            Intent intent = new Intent(this, UserRestaurantList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        else if(id==R.id.nav_login){
            Intent intent = new Intent(this, LoginManagerActivity.class);
            intent.putExtra("login", true);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_logout){
            Intent intent = new Intent(this, LoginManagerActivity.class);
            intent.putExtra("login", false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if(id==R.id.nav_my_profile) {
            Intent intent = new Intent(this, UserProfile.class);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_my_orders) {
            Intent intent = new Intent(this, MyOrdersActivity.class);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_my_reservations){
            Intent intent = new Intent(this, UserMyReservations.class);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_my_reviews){
            Intent intent = new Intent(this, MyReviewsActivity.class);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_my_favourites){
            Intent intent = new Intent(this, UserMyFavourites.class);
            startActivity(intent);
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*
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
    */

    public void load_saved_data(){
        //load photo
        ImageView image = (ImageView) findViewById(R.id.imageView);

        String photoUri = current_user.getUser_photo_firebase_URL();
        if(photoUri == null || photoUri.equals("")) {
            Glide
                    .with(UserProfile.this)
                    .load(R.drawable.blank_profile)
                    .centerCrop()
                    .into(image);
        }
        else{
            Glide
                    .with(UserProfile.this)
                    .load(photoUri)
                    .centerCrop()
                    .into(image);
        }

        //load other fields
        TextView tv = (TextView) findViewById(R.id.email);
        if (current_user.getUser_email() != null)
            tv.setText(current_user.getUser_email());
        TextView tv3 = (TextView) findViewById(R.id.profile_full_name);
        if (current_user.getUser_full_name() != null)
            tv3.setText(current_user.getUser_full_name());
        TextView tv5 = (TextView) findViewById(R.id.phone_number);
        if (current_user.getUser_telephone_number() != null)
            tv5.setText(current_user.getUser_telephone_number());
        TextView tv6 = (TextView) findViewById(R.id.vat_number);
        if (current_user.getOwner_vat_number() != null)
            tv6.setText(current_user.getOwner_vat_number());
    }

    public void activate_buttons() {
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
    public void onSaveInstanceState(Bundle savedInstanceState) { //not called in case of finish()
        Log.d("tag1", "saveinstancestate");
        //save the state here
        saveinstancestate(savedInstanceState);
    }

    public void saveinstancestate(Bundle savedInstanceState){
        // If there's an upload in progress, save the reference so you can query it later
        if (user_photo_reference != null) {
            savedInstanceState.putString("reference", user_photo_reference.toString());
        }
        if (user_photo_reference_thumbnail != null) {
            savedInstanceState.putString("reference", user_photo_reference_thumbnail.toString());
        }

        TextView tv = (TextView) findViewById(R.id.email);
        TextView tv3 = (TextView) findViewById(R.id.profile_full_name);
        TextView tv5 = (TextView) findViewById(R.id.phone_number);
        TextView tv6 = (TextView) findViewById(R.id.vat_number);

        String email = tv.getText().toString();
        String full_name = tv3.getText().toString();
        String phone_number = tv5.getText().toString();
        String vat_number = tv6.getText().toString();

        savedInstanceState.putString("email", email);
        savedInstanceState.putString("full_name", full_name);
        savedInstanceState.putString("phone_number", phone_number);
        savedInstanceState.putString("vat_number", vat_number);
        //I can not save the photo, but i could save its URI
        savedInstanceState.putString("photouri", photouri);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d("tag1", "restoreinstancestate");
        restoreinstancestate(savedInstanceState);
    }

    public void restoreinstancestate(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            if(savedInstanceState.getString("photouri", null)!=null) {
                Uri photouri2 = Uri.parse(savedInstanceState.getString("photouri", null));
                if(photouri2!=null) {
                    ImageView image = (ImageView) findViewById(R.id.imageView);
                    Glide.with(this)
                            .load(photouri2) //"http://nuuneoi.com/uploads/source/playstore/cover.jpg"
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.blank_profile)
                            .into(image);
                }
            }
            TextView tv = (TextView) findViewById(R.id.email);
            TextView tv3 = (TextView) findViewById(R.id.profile_full_name);
            TextView tv5 = (TextView) findViewById(R.id.phone_number);
            TextView tv6 = (TextView) findViewById(R.id.vat_number);
            if (savedInstanceState.getString("email") != null)
                tv.setText(savedInstanceState.getString("email", null));
            if (savedInstanceState.getString("full_name") != null)
                tv3.setText(savedInstanceState.getString("full_name", null));
            if (savedInstanceState.getString("phone_number") != null)
                tv5.setText(savedInstanceState.getString("phone_number", null));
            if (savedInstanceState.getString("vat_number") != null)
                tv6.setText(savedInstanceState.getString("vat_number", null));
        }

        // If there was an upload in progress, get its reference and create a new StorageReference
        final String stringRef = savedInstanceState.getString("reference");
        if (stringRef == null) {
            return;
        }
        user_storage_reference = FirebaseStorage.getInstance().getReferenceFromUrl(stringRef);
        // Find all UploadTasks under this StorageReference (in this example, there should be one)
        List<UploadTask> tasks = user_storage_reference.getActiveUploadTasks();
        if (tasks.size() > 0) {
            // Get the task monitoring the upload
            UploadTask task = tasks.get(0);
            // Add new listeners to the task using an Activity scope
            task.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
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
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        //this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            save_user();
            Intent intent = new Intent(getApplicationContext(), UserRestaurantList.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_cancel) {
            Intent intent = new Intent(getApplicationContext(), UserRestaurantList.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //take a photo result
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //view photo
            Log.d("aaa", "BREAK2");
            ImageView image = (ImageView) findViewById(R.id.imageView);
            //setPic();
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
                Glide.with(this)
                        .load(photouri) //"http://nuuneoi.com/uploads/source/playstore/cover.jpg"
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.blank_profile)
                        .into(image);
            }
        }
        //choose a photo result
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            //photouri = imageUri.toString();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                ImageView image_to_enlarge = (ImageView) findViewById(R.id.imageView);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                Glide.with(this)
                        .load(imageUri) //"http://nuuneoi.com/uploads/source/playstore/cover.jpg"
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.blank_profile)
                        .into(image_to_enlarge);
                photouri = saveToInternalStorage(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (imageStream != null) {
                    try {
                        imageStream.close();
                    } catch (IOException e) {
                        // Ignore the exception
                    }
                }
            }
        }
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.putString("photouri", photouri);
        edit.commit();
        //I can not save the photo, but i could save its URI
        //hide();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void save_user(){
        //new user
        TextView tv = (TextView) findViewById(R.id.email);
        TextView tv3 = (TextView) findViewById(R.id.profile_full_name);
        TextView tv5 = (TextView) findViewById(R.id.phone_number);
        TextView tv6 = (TextView) findViewById(R.id.vat_number);
        String email = tv.getText().toString();
        String full_name = tv3.getText().toString();
        String phone_number = tv5.getText().toString();
        String vat_number = tv6.getText().toString();

        current_user.setUser_email(email);
        current_user.setUser_full_name(full_name);
        current_user.setUser_telephone_number(phone_number);
        current_user.setOwner_vat_number(vat_number);

        //save photo
        ImageView image = (ImageView) findViewById(R.id.imageView);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        user_storage_reference = storage.getReferenceFromUrl("gs://have-break-9713d.appspot.com");

        if(photouri == null){
            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/users/" + user_id);
            ref2.setValue(current_user);
            return;
        }

        File f = new File(photouri);
        Uri imageUri = Uri.fromFile(f);
        // Create a child reference
        // imagesRef now points to "images"
        Calendar c = Calendar.getInstance();
        user_photo_reference = user_storage_reference.child("users/" + user_id + "/" + c.getTimeInMillis() + ".jpg");
        //upload
        UploadTask uploadTask = user_storage_reference.putFile(imageUri);
        uploadTask
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        Log.d("my_ex", e.getMessage());
                        Toast failure_message = Toast.makeText(UserProfile.this, "The upload is failed", Toast.LENGTH_LONG);
                        failure_message.show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        current_user.setUser_photo_firebase_URL(downloadUrl.toString());

                        //onSuccess try to save also its thumbnail
                        final int THUMBSIZE = 64;
                        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(photouri),
                                THUMBSIZE, THUMBSIZE);
                        File f = new File((getImageUri(UserProfile.this, ThumbImage).toString()));
                        Uri imageUri = Uri.fromFile(f);
                        user_photo_reference_thumbnail = user_storage_reference.child("users/" + user_id + "_thumbnail");
                        UploadTask uploadTask = user_photo_reference_thumbnail.putFile(imageUri);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            //public void onFailure(@NonNull Throwable throwable) {
                            public void onFailure(Exception e) {
                                e.printStackTrace();
                                Log.d("my_ex", e.getMessage());
                                Toast failure_message = Toast.makeText(UserProfile.this, "The upload is failed", Toast.LENGTH_LONG);
                                failure_message.show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                current_user.setUser_thumbnail(downloadUrl.toString());

                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/users/" + user_id);
                                ref2.setValue(current_user);
                            }
                        });
                    }
                });

            }


    private String saveToInternalStorage(Bitmap bitmapImage) throws IOException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(directory, "profile.png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        return myPath.getAbsolutePath();
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
        photouri = image.getAbsolutePath();
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

}
