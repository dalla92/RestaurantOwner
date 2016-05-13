package it.polito.group2.restaurantowner.user.restaurant_page;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Review;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.Restaurant;
import it.polito.group2.restaurantowner.data.Offer;
import it.polito.group2.restaurantowner.data.User;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.user.my_orders.MyOrdersActivity;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewsActivity;
import it.polito.group2.restaurantowner.user.restaurant_page.gallery.GalleryViewActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private String user_id;
    static final int PICK_IMAGE = 2;
    static final int REQUEST_TAKE_PHOTO = 3;
    boolean isImageFitToScreen=false;
    public String photouri=null;
    public User current_user;
    private Context context;
    private ArrayList<User> users = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        context = this;

        //TODO Rearrange the following code
        try {
            users = JSONUtil.readJSONUsersList(context, null);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        if(users==null){
            current_user = new User();
            current_user.setEmail("jkjs@dskj");
            current_user.setId("d48jd48d48j");
            //current_user.setFidelity_points(110);
            current_user.setFirst_name("Alex");
            current_user.setIsOwner(true);
            current_user.setPassword("tipiacerebbe");
            current_user.setPhone_number("0989897879789");
            current_user.setVat_number("sw8d9wd8w9d8w9d9");
            users.add(current_user);
            try{
                JSONUtil.saveJSONUsersList(users, context);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            try {
                users = JSONUtil.readJSONUsersList(context, null);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("user_id")!=null) {
            user_id = getIntent().getExtras().getString("user_id");
            for(User u : users){
                if(u.getId().equals(user_id)){
                    current_user = u;
                    break;
                }
            }
            if(current_user==null){
                for(User u : users){
                    if(u.getId()!=null)
                    if(u.getId().equals("d48jd48d48j")){
                        current_user = u;
                        break;
                    }
                }
            }
        }
        if(current_user==null){
            for(User u : users){
                if(u.getId()!=null)
                    if(u.getId().equals("d48jd48d48j")){
                        current_user = u;
                        break;
                    }
            }
        }
        if(current_user==null){
            current_user = new User();
            current_user.setEmail("jkjs@dskj");
            current_user.setId("d48jd48d48j");
            //current_user.setFidelity_points(110);
            current_user.setFirst_name("Alex");
            current_user.setIsOwner(true);
            current_user.setPassword("tipiacerebbe");
            current_user.setPhone_number("0989897879789");
            current_user.setVat_number("sw8d9wd8w9d8w9d9");
            users.add(current_user);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        load_saved_data();

        activate_buttons();


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
        if (current_user.getPhoto()!=null){
            image.setImageBitmap(current_user.getPhoto());
        }
        else {
            SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
            if (userDetails != null) {
                if (userDetails.getString("photouri", null) != null) {
                    Uri photouri = Uri.parse(userDetails.getString("photouri", null));
                    if (photouri != null)
                        image.setImageURI(photouri);
                }
            }
        }

        //load other fields
        TextView tv = (TextView) findViewById(R.id.email);
        if (current_user.getEmail() != null)
            tv.setText(current_user.getEmail());
        TextView tv2 = (TextView) findViewById(R.id.password);
        if (current_user.getPassword() != null)
            tv2.setText(current_user.getPassword());
        TextView tv3 = (TextView) findViewById(R.id.first_name);
        if (current_user.getFirst_name() != null)
            tv3.setText(current_user.getFirst_name());
        TextView tv4 = (TextView) findViewById(R.id.last_name);
        if (current_user.getLast_name() != null)
            tv4.setText(current_user.getLast_name());
        TextView tv5 = (TextView) findViewById(R.id.phone_number);
        if (current_user.getPhone_number() != null)
            tv5.setText(current_user.getPhone_number());
        TextView tv6 = (TextView) findViewById(R.id.vat_number);
        if (current_user.getVat_number() != null)
            tv6.setText(current_user.getVat_number());
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
        TextView tv = (TextView) findViewById(R.id.email);
        TextView tv2 = (TextView) findViewById(R.id.password);
        TextView tv3 = (TextView) findViewById(R.id.first_name);
        TextView tv4 = (TextView) findViewById(R.id.last_name);
        TextView tv5 = (TextView) findViewById(R.id.phone_number);
        TextView tv6 = (TextView) findViewById(R.id.vat_number);

        String email = tv.getText().toString();
        String password = tv2.getText().toString();
        String first_name = tv3.getText().toString();
        String last_name = tv4.getText().toString();
        String phone_number = tv5.getText().toString();
        String vat_number = tv6.getText().toString();

        savedInstanceState.putString("email", email);
        savedInstanceState.putString("password", password);
        savedInstanceState.putString("first_name", first_name);
        savedInstanceState.putString("last_name", last_name);
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
                    image.setImageURI(photouri2);
                }
            }
            TextView tv = (TextView) findViewById(R.id.email);
            TextView tv2 = (TextView) findViewById(R.id.password);
            TextView tv3 = (TextView) findViewById(R.id.first_name);
            TextView tv4 = (TextView) findViewById(R.id.last_name);
            TextView tv5 = (TextView) findViewById(R.id.phone_number);
            TextView tv6 = (TextView) findViewById(R.id.vat_number);
            if (savedInstanceState.getString("email") != null)
                tv.setText(savedInstanceState.getString("email", null));
            if (savedInstanceState.getString("password") != null)
                tv2.setText(savedInstanceState.getString("password", null));
            if (savedInstanceState.getString("first_name") != null)
                tv3.setText(savedInstanceState.getString("first_name", null));
            if (savedInstanceState.getString("last_name") != null)
                tv4.setText(savedInstanceState.getString("last_name", null));
            if (savedInstanceState.getString("phone_number") != null)
                tv5.setText(savedInstanceState.getString("phone_number", null));
            if (savedInstanceState.getString("vat_number") != null)
                tv6.setText(savedInstanceState.getString("vat_number", null));
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
            users.remove(current_user);
            //new user
            TextView tv = (TextView) findViewById(R.id.email);
            TextView tv2 = (TextView) findViewById(R.id.password);
            TextView tv3 = (TextView) findViewById(R.id.first_name);
            TextView tv4 = (TextView) findViewById(R.id.last_name);
            TextView tv5 = (TextView) findViewById(R.id.phone_number);
            TextView tv6 = (TextView) findViewById(R.id.vat_number);
            String email = tv.getText().toString();
            String password = tv2.getText().toString();
            String first_name = tv3.getText().toString();
            String last_name = tv4.getText().toString();
            String phone_number = tv5.getText().toString();
            String vat_number = tv6.getText().toString();
            User new_user = new User();
            if(user_id!=null){
                new_user.setId(user_id);
            }
            else{
                new_user.setId(current_user.getId());
            }
            new_user.setEmail(email);
            new_user.setPassword(password);
            new_user.setFirst_name(first_name);
            new_user.setLast_name(last_name);
            new_user.setPhone_number(phone_number);
            new_user.setVat_number(vat_number);
            users.add(new_user);
            try{
                JSONUtil.saveJSONUsersList(users, context);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
        if (id == R.id.action_cancel) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantList.class);
            Bundle b1 = new Bundle();
            b1.putString("user_id", user_id);
            intent1.putExtras(b1);
            startActivity(intent1);
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
                image_to_enlarge.setImageBitmap(bitmap);
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
        //I can not save the photo, but i could save its URI
        //hide();
    }

    private String saveToInternalStorage(Bitmap bitmapImage) throws IOException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath= new File(directory, "profile.png");
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

}
