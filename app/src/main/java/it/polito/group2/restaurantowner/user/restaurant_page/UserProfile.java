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
import android.os.AsyncTask;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.ImageUtils;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
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
    private Toolbar toolbar;
    private FirebaseDatabase firebase;
    private User current_user;
    private Uri imageUri;
    private StorageReference storageRef;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get the right user
        user_id = FirebaseUtil.getCurrentUserId();
        if(user_id == null){
            Toast.makeText(UserProfile.this, "You need to be logged in.", Toast.LENGTH_SHORT).show();
            finish();
        }

        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://have-break-9713d.appspot.com");
        firebase = FirebaseDatabase.getInstance();
        mProgressDialog = FirebaseUtil.initProgressDialog(this);

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
        return DrawerUtil.drawer_user_not_restaurant_page(this, item);
    }

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

        //take a photo
        Button button1 = (Button) findViewById(R.id.button_take_photo);
        assert button1 != null;
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        //choose a photo
        Button button2 = (Button) findViewById(R.id.button_choose_photo);
        assert button2 != null;
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            OnBackUtil.clean_stack_and_go_to_user_restaurant_list(this);
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
        ImageView image = (ImageView) findViewById(R.id.imageView);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if(photouri!=null) {
                Glide.with(this)
                        .load(photouri)
                        .placeholder(R.drawable.blank_profile)
                        .into(image);
            }
        }
        //choose a photo result
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
                Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.blank_profile)
                        .into(image);
        }
        /*SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.putString("photouri", photouri);
        edit.commit();*/
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
        FirebaseUtil.showProgressDialog(mProgressDialog);
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

        if(photouri == null && imageUri == null){
            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/users/" + user_id);
            ref2.setValue(current_user);
            FirebaseUtil.hideProgressDialog(mProgressDialog);
            Toast.makeText(UserProfile.this, "Information saved", Toast.LENGTH_SHORT).show();
            end();
        }

        if(photouri != null){
            ImageCompressionAsyncTask imageCompression = new ImageCompressionAsyncTask() {
                @Override
                protected void onPostExecute(Uri uri) {
                    // image here is compressed & ready to be saved
                    galleryAddPic();

                    final StorageReference userPictureRef = storageRef.child("users/" + user_id + "/profile.jpg");
                    final StorageReference userThumbnailRef = storageRef.child("users/" + user_id + "/profile_thumbnail.jpg");

                    //upload
                    UploadTask uploadTask = userPictureRef.putFile(uri);
                    uploadTask
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NotNull Exception e) {
                                    e.printStackTrace();
                                    FirebaseUtil.hideProgressDialog(mProgressDialog);
                                    Toast.makeText(UserProfile.this, "The upload is failed", Toast.LENGTH_LONG).show();
                                    end();
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
                                    Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(photouri), THUMBSIZE, THUMBSIZE);

                                    UploadTask uploadTask = userThumbnailRef.putFile(getImageUri(UserProfile.this, ThumbImage));
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        //public void onFailure(@NonNull Throwable throwable) {
                                        public void onFailure(@NotNull Exception e) {
                                            e.printStackTrace();
                                            FirebaseUtil.hideProgressDialog(mProgressDialog);
                                            Toast.makeText(UserProfile.this, "The thumbnail upload is failed", Toast.LENGTH_LONG).show();
                                            end();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                            current_user.setUser_thumbnail(downloadUrl.toString());

                                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/users/" + user_id);
                                            ref2.setValue(current_user);
                                            FirebaseUtil.hideProgressDialog(mProgressDialog);
                                            Toast.makeText(UserProfile.this, "Information saved", Toast.LENGTH_SHORT).show();
                                            end();
                                        }
                                    });
                                }
                            });
                }
            };
            imageCompression.execute(photouri);// imagePath as a string
        }

        if(imageUri != null){
            final StorageReference userPictureRef = storageRef.child("users/" + user_id + "/profile.jpg");
            final StorageReference userThumbnailRef = storageRef.child("users/" + user_id + "/profile_thumbnail.jpg");

            //upload
            UploadTask uploadTask = userPictureRef.putFile(imageUri);
            uploadTask
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NotNull Exception e) {
                            e.printStackTrace();
                            FirebaseUtil.hideProgressDialog(mProgressDialog);
                            Toast.makeText(UserProfile.this, "The upload is failed", Toast.LENGTH_LONG).show();
                            end();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            current_user.setUser_photo_firebase_URL(downloadUrl.toString());

                            //onSuccess try to save also its thumbnail
                            try {
                                final int THUMBSIZE = 64;
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(UserProfile.this.getContentResolver(),imageUri);
                                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(bitmap, THUMBSIZE, THUMBSIZE);
                                UploadTask uploadTask = userThumbnailRef.putFile(getImageUri(UserProfile.this, ThumbImage));
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    //public void onFailure(@NonNull Throwable throwable) {
                                    public void onFailure(@NotNull Exception e) {
                                        e.printStackTrace();
                                        FirebaseUtil.hideProgressDialog(mProgressDialog);
                                        Toast.makeText(UserProfile.this, "The thumbnail upload is failed", Toast.LENGTH_LONG).show();
                                        end();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        current_user.setUser_thumbnail(downloadUrl.toString());
                                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/users/" + user_id);
                                        ref2.setValue(current_user);

                                        FirebaseUtil.hideProgressDialog(mProgressDialog);
                                        Toast.makeText(UserProfile.this, "Information saved", Toast.LENGTH_SHORT).show();
                                        end();
                                    }
                                });
                            } catch (IOException e) {
                                FirebaseUtil.hideProgressDialog(mProgressDialog);
                                Toast.makeText(UserProfile.this, "The thumbnail creation failed", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                end();
                            }

                        }
                    });
        }

    }

    private void end(){
        Intent intent = new Intent(getApplicationContext(), UserRestaurantList.class);
        startActivity(intent);
    }

    public abstract class ImageCompressionAsyncTask extends AsyncTask<String, Void, Uri> {

        @Override
        protected Uri doInBackground(String... strings) {
            if(strings.length == 0 || strings[0] == null)
                return null;
            return ImageUtils.compressImage(strings[0]);
        }

        protected abstract void onPostExecute(Uri imageUri) ;
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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photouri);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
